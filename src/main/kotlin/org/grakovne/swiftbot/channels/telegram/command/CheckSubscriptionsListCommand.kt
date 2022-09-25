package org.grakovne.swiftbot.channels.telegram.command

import arrow.core.Either
import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.Update
import org.grakovne.swiftbot.channels.telegram.TelegramUpdateProcessingError
import org.grakovne.swiftbot.channels.telegram.messaging.PaymentSubscriptionsMessageSender
import org.grakovne.swiftbot.user.UserReferenceService
import org.grakovne.swiftbot.user.domain.UserReference
import org.springframework.stereotype.Service

@Service
class CheckSubscriptionsListCommand(
    private val userReferenceService: UserReferenceService,
    private val messageSender: PaymentSubscriptionsMessageSender
) : TelegramOnMessageCommand {

    override fun getKey(): String = "subscriptions"
    override fun getHelp(): String = "Shows list of subscriptions"

    override fun accept(
        bot: TelegramBot,
        update: Update,
        user: UserReference
    ): Either<TelegramUpdateProcessingError, Unit> {
        return userReferenceService
            .fetchUserSubscription(update.message().chat().id().toString())
            .let { messageSender.sendResponse(update, user, it) }
    }

}