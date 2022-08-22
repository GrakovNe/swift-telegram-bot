package org.grakovne.swiftbot.channels.telegram.command

import arrow.core.Either
import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.request.SendMessage
import org.grakovne.swiftbot.channels.telegram.TelegramUpdateProcessingError
import org.grakovne.swiftbot.dto.PaymentView
import org.grakovne.swiftbot.payment.synchronization.CommonSynchronizationError
import org.grakovne.swiftbot.payment.synchronization.payment.PaymentService
import org.grakovne.swiftbot.user.UserReferenceService
import org.grakovne.swiftbot.user.domain.UserReferenceSource
import org.springframework.format.datetime.standard.InstantFormatter
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

@Service
class CheckPaymentStatusCommand(
    private val paymentService: PaymentService,
    private val userReferenceService: UserReferenceService
) : TelegramOnMessageCommand {
    override fun isCommandAcceptable(update: Update): Boolean = update.message().text().startsWith("/check")

    override fun processUpdate(bot: TelegramBot, update: Update): Either<TelegramUpdateProcessingError, Unit> {
        val pattern = Pattern.compile("[a-f0-9]{8}(?:-[a-f0-9]{4}){4}[a-f0-9]{8}")
        val matcher = pattern.matcher(update.message().text())

        if (!matcher.find()) {
            bot.execute(SendMessage(update.message().chat().id(), "UETR not found"))
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
            .map { view -> bot.execute(SendMessage(update.message().chat().id(), view.toMessage())) }
            .map { }
            .mapLeft {
                when (it) {
                    is CommonSynchronizationError -> TelegramUpdateProcessingError.INTERNAL_ERROR
                }
            }
    }

    private fun PaymentView.toMessage(): String {
        return """
            Payment Info:
            
            UETR: ${this.id}
            Current status: ${this.status}
            Last update: ${this.lastUpdateTimestamp.toMessage()}
        """.trimIndent()
    }

    private fun Instant.toMessage(): String = dateFormatter.format(this)

    companion object {
        val dateFormatter = DateTimeFormatter
            .ofPattern("dd.MM.yyyy hh:mm:ss")
            .withZone(ZoneId.of("UTC"));

    }

}