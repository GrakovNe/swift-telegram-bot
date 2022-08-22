package org.grakovne.swiftbot.channels.telegram.command

import arrow.core.Either
import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.request.SendMessage
import org.grakovne.swiftbot.channels.telegram.TelegramUpdateProcessingError
import org.grakovne.swiftbot.user.UserReferenceService
import org.grakovne.swiftbot.user.domain.UserReferenceSource
import org.springframework.stereotype.Service
import java.util.*
import java.util.regex.Pattern

@Service
class UnsubscribePaymentStatusCommand(
    private val userReferenceService: UserReferenceService
) : TelegramOnMessageCommand {

    override fun getHelp(): String = "/unsubscribe <UETR> - Unsubscribes for a status changes notifications"
    override fun isCommandAcceptable(update: Update): Boolean = update.message().text().startsWith("/unsubscribe")

    override fun processUpdate(bot: TelegramBot, update: Update): Either<TelegramUpdateProcessingError, Unit> {
        val pattern = Pattern.compile("[a-f0-9]{8}(?:-[a-f0-9]{4}){4}[a-f0-9]{8}")
        val matcher = pattern.matcher(update.message().text())

        if (!matcher.find()) {
            bot.execute(SendMessage(update.message().chat().id(), "UETR required but not sent"))
            return Either.Left(TelegramUpdateProcessingError.INVALID_REQUEST)
        }

        val paymentId = UUID.fromString(matcher.group(0))

        userReferenceService.unsubscribeFromPayment(
            update.message().chat().id().toString(),
            paymentId,
            UserReferenceSource.TELEGRAM
        )

        val isMessageSent = bot.execute(SendMessage(update.message().chat().id(), "Unsubscribed!")).isOk

        return when (isMessageSent) {
            true -> Either.Right(Unit)
            false -> Either.Left(TelegramUpdateProcessingError.RESPONSE_NOT_SENT)
        }
    }
}