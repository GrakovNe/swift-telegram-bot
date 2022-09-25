package org.grakovne.swiftbot.channels.telegram.command

import arrow.core.Either
import com.pengrad.telegrambot.model.Update
import org.grakovne.swiftbot.channels.telegram.TelegramUpdateProcessingError
import org.grakovne.swiftbot.channels.telegram.messaging.PaymentInfo
import org.grakovne.swiftbot.channels.telegram.messaging.PaymentStatusMessageSender
import org.grakovne.swiftbot.channels.telegram.messaging.SimpleMessageSender
import org.grakovne.swiftbot.dto.CommandType
import org.grakovne.swiftbot.dto.PaymentView
import org.grakovne.swiftbot.events.core.EventSender
import org.grakovne.swiftbot.events.internal.LogLevel.DEBUG
import org.grakovne.swiftbot.events.internal.LogLevel.WARN
import org.grakovne.swiftbot.events.internal.LoggingEvent
import org.grakovne.swiftbot.localization.IncorrectPaymentId
import org.grakovne.swiftbot.localization.PaymentStatusNotFound
import org.grakovne.swiftbot.payment.metrics.PaymentReportService
import org.grakovne.swiftbot.payment.synchronization.CommonSynchronizationError
import org.grakovne.swiftbot.payment.synchronization.payment.PaymentService
import org.grakovne.swiftbot.user.UserReferenceService
import org.grakovne.swiftbot.user.domain.UserReference
import org.grakovne.swiftbot.utils.findPaymentId
import org.springframework.stereotype.Service

@Service
class CheckPaymentStatusCommand(
    private val paymentService: PaymentService,
    private val paymentReportService: PaymentReportService,
    private val userReferenceService: UserReferenceService,
    private val eventSender: EventSender,
    private val messageSender: PaymentStatusMessageSender,
    private val simpleMessageSender: SimpleMessageSender
) : TelegramOnMessageCommand {

    override fun getKey(): String = "check"
    override fun getArguments() = "[UETR]"
    override fun getType() = CommandType.CHECK_PAYMENT_STATUS

    override fun accept(
        update: Update,
        user: UserReference
    ): Either<TelegramUpdateProcessingError, Unit> {
        val paymentId = update.findPaymentId()

        if (null == paymentId) {
            simpleMessageSender.sendResponse(update, user, IncorrectPaymentId(getKey()))
            return Either.Left(TelegramUpdateProcessingError.INVALID_REQUEST)
        }

        return paymentService
            .fetchPaymentStatus(paymentId)
            .tap { userReferenceService.subscribeToPayment(user, paymentId) }
            .map { view ->
                messageSender.sendResponse(update, user, view.toMessage())
            }
            .tap { eventSender.sendEvent(LoggingEvent(DEBUG, "Checked payment status with id $paymentId")) }
            .map { }
            .mapLeft {
                when (it) {
                    is CommonSynchronizationError -> {
                        simpleMessageSender.sendResponse(update, user, PaymentStatusNotFound)
                        eventSender.sendEvent(
                            LoggingEvent(
                                WARN,
                                "Unable to track payment status due to ${it.message} source user's message is: ${
                                    update.message().text()
                                }"
                            )
                        )
                        TelegramUpdateProcessingError.INTERNAL_ERROR
                    }
                }
            }
    }

    private fun PaymentView.toMessage() = PaymentInfo(
        paymentId = this.id,
        status = this.status,
        lastUpdateTimestamp = this.lastUpdateTimestamp,
        history = paymentReportService.fetchEntries(this.id).map { it.newStatus to it.timestamp }
    )
}