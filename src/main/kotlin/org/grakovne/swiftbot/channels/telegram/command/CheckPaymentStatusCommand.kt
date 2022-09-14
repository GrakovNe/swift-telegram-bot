package org.grakovne.swiftbot.channels.telegram.command

import arrow.core.Either
import arrow.core.tail
import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.SendMessage
import org.grakovne.swiftbot.channels.telegram.TelegramUpdateProcessingError
import org.grakovne.swiftbot.common.converter.toMessage
import org.grakovne.swiftbot.dto.PaymentView
import org.grakovne.swiftbot.events.core.EventSender
import org.grakovne.swiftbot.events.internal.LogLevel.DEBUG
import org.grakovne.swiftbot.events.internal.LogLevel.WARN
import org.grakovne.swiftbot.events.internal.LoggingEvent
import org.grakovne.swiftbot.payment.metrics.PaymentReportService
import org.grakovne.swiftbot.payment.synchronization.CommonSynchronizationError
import org.grakovne.swiftbot.payment.synchronization.payment.PaymentService
import org.grakovne.swiftbot.user.UserReferenceService
import org.grakovne.swiftbot.user.domain.UserReferenceSource
import org.springframework.stereotype.Service
import java.util.*
import java.util.regex.Pattern

@Service
class CheckPaymentStatusCommand(
    private val paymentService: PaymentService,
    private val paymentReportService: PaymentReportService,
    private val userReferenceService: UserReferenceService,
    private val eventSender: EventSender
) : TelegramOnMessageCommand {

    override fun getKey(): String = "check"
    override fun getArguments() = "<UETR>"
    override fun getHelp(): String = "Checks current payment status and subscribes for a changes"

    override fun isAcceptable(update: Update): Boolean {
        val isStartsFromKey = update.message().text().startsWith("/" + getKey())
        val isStartsFromPaymentId = pattern.matcher(update.message().text()).find()

        return isStartsFromPaymentId || isStartsFromKey
    }

    override fun accept(bot: TelegramBot, update: Update): Either<TelegramUpdateProcessingError, Unit> {
        val matcher = pattern.matcher(update.message().text())

        if (!matcher.find()) {
            bot.execute(
                SendMessage(
                    update.message().chat().id(),
                    "Please provide valid UETR\n\n<b>Example</b>: <i>/check 1b5c013c-8601-46ba-a982-e88848140329</i>"
                ).parseMode(ParseMode.HTML)
            )
            return Either.Left(TelegramUpdateProcessingError.INVALID_REQUEST)
        }

        val paymentId = UUID.fromString(matcher.group(0))

        return paymentService
            .fetchPaymentStatus(paymentId)
            .tap {
                userReferenceService.subscribeToPayment(
                    update.message().chat().id().toString(),
                    paymentId,
                    UserReferenceSource.TELEGRAM
                )
            }
            .map { view ->
                bot.execute(SendMessage(update.message().chat().id(), view.toMessage()).parseMode(ParseMode.HTML))
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

    private fun PaymentView.toMessage(): String {
        return """
            Payment info
            
            <b>UETR</b>: ${this.id}
            
            <b>Current status</b>: ${this.status}
            <b>Last update</b>: ${this.lastUpdateTimestamp.toMessage()}
            ${if (paymentReportService.fetchEntries(this.id).size > 1) this.toHistory() else ""}
            now you're subscribed to payment updates
        """.trimIndent()
    }

    private fun PaymentView.toHistory(): String {
        val history = paymentReportService
            .fetchEntries(this.id)
            .tail()
            .joinToString(separator = "") {
                """
            <b>Time</b>: ${it.timestamp.toMessage()}
            <b>Status</b>: ${it.newStatus}"""
            }

        return """
            Previous updates:
            $history
        """
    }

    companion object {
        private val pattern = Pattern.compile("[a-f0-9]{8}(?:-[a-f0-9]{4}){4}[a-f0-9]{8}")
    }

}