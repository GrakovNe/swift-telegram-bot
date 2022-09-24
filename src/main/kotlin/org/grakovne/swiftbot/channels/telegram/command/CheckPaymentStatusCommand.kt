package org.grakovne.swiftbot.channels.telegram.command

import arrow.core.Either
import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.SendMessage
import org.grakovne.swiftbot.channels.telegram.TelegramUpdateProcessingError
import org.grakovne.swiftbot.channels.telegram.messaging.PaymentInfo
import org.grakovne.swiftbot.channels.telegram.messaging.PaymentStatusMessageSender
import org.grakovne.swiftbot.dto.PaymentView
import org.grakovne.swiftbot.events.core.EventSender
import org.grakovne.swiftbot.events.internal.LogLevel.DEBUG
import org.grakovne.swiftbot.events.internal.LogLevel.WARN
import org.grakovne.swiftbot.events.internal.LoggingEvent
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
    private val messageSender: PaymentStatusMessageSender
) : TelegramOnMessageCommand {

    override fun getKey(): String = "check"
    override fun getArguments() = "<UETR>"
    override fun getHelp(): String = "Checks current payment status and subscribes for a changes"

    override fun accept(
        bot: TelegramBot,
        update: Update,
        user: UserReference
    ): Either<TelegramUpdateProcessingError, Unit> {
        val paymentId = update.findPaymentId()

        if (null == paymentId) {
            bot.execute(
                SendMessage(
                    update.message().chat().id(),
                    "Please provide valid UETR\n\n<b>Example</b>: <i>/check 1b5c013c-8601-46ba-a982-e88848140329</i>"
                ).parseMode(ParseMode.HTML)
            )
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
                        bot.execute(
                            SendMessage(
                                update.message().chat().id(),
                                "Unable to find payment by UETR. Please try check UETR and try again\n\n<i>" +
                                        "Please, note that payments older 3 months may not be tracked</i>"
                            ).parseMode(ParseMode.HTML)
                        )
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