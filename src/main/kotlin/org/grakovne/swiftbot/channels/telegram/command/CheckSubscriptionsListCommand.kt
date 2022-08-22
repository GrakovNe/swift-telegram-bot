package org.grakovne.swiftbot.channels.telegram.command

import arrow.core.Either
import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.request.SendMessage
import org.grakovne.swiftbot.channels.telegram.TelegramUpdateProcessingError
import org.grakovne.swiftbot.user.UserReferenceService
import org.springframework.stereotype.Service

@Service

class CheckSubscriptionsListCommand(
    private val userReferenceService: UserReferenceService
) : TelegramOnMessageCommand {
    override fun isCommandAcceptable(update: Update): Boolean = update.message().text().startsWith("/subscriptions")

    override fun processUpdate(bot: TelegramBot, update: Update): Either<TelegramUpdateProcessingError, Unit> {
        val isMessageSent = userReferenceService
            .fetchUserSubscription(update.message().chat().id().toString())
            .fold("Subscriptions: \n\n") { acc, uuid -> acc + "UETR: $uuid" }
            .let { message -> bot.execute(SendMessage(update.message().chat().id(), message)) }
            .isOk

        return when (isMessageSent) {
            true -> Either.Right(Unit)
            false -> Either.Left(TelegramUpdateProcessingError.RESPONSE_NOT_SENT)
        }
    }

}