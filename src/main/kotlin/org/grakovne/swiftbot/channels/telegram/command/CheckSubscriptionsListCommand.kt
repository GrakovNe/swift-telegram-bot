package org.grakovne.swiftbot.channels.telegram.command

import arrow.core.Either
import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.request.SendMessage
import org.grakovne.swiftbot.channels.telegram.TelegramUpdateProcessingError
import org.grakovne.swiftbot.user.UserReferenceService
import org.grakovne.swiftbot.user.domain.UserReference
import org.springframework.stereotype.Service

@Service
class CheckSubscriptionsListCommand(
    private val userReferenceService: UserReferenceService
) : TelegramOnMessageCommand {

    override fun getKey(): String = "subscriptions"
    override fun getHelp(): String = "Shows list of subscriptions"

    override fun accept(
        bot: TelegramBot,
        update: Update,
        user: UserReference
    ): Either<TelegramUpdateProcessingError, Unit> {
        val isMessageSent = userReferenceService
            .fetchUserSubscription(update.message().chat().id().toString())
            .fold("Subscriptions: \n\n") { acc, uuid -> acc + "UETR: $uuid\n" }
            .let { message -> bot.execute(SendMessage(update.message().chat().id(), message)) }
            .isOk

        return when (isMessageSent) {
            true -> Either.Right(Unit)
            false -> Either.Left(TelegramUpdateProcessingError.RESPONSE_NOT_SENT)
        }
    }

}