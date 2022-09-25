package org.grakovne.swiftbot.channels.telegram.command

import arrow.core.Either
import com.pengrad.telegrambot.model.Update
import org.grakovne.swiftbot.channels.telegram.TelegramUpdateProcessingError
import org.grakovne.swiftbot.channels.telegram.messaging.PaymentSubscriptionsMessageSender
import org.grakovne.swiftbot.dto.CommandType
import org.grakovne.swiftbot.user.UserReferenceService
import org.grakovne.swiftbot.user.domain.UserReference
import org.springframework.stereotype.Service

@Service
class CheckSubscriptionsListCommand(
    private val userReferenceService: UserReferenceService,
    private val messageSender: PaymentSubscriptionsMessageSender
) : TelegramOnMessageCommand {

    override fun getKey(): String = "subscriptions"
    override fun getType() = CommandType.SHOW_SUBSCRIPTIONS

    override fun accept(
        update: Update,
        user: UserReference
    ): Either<TelegramUpdateProcessingError, Unit> {
        return userReferenceService
            .fetchUserSubscription(update.message().chat().id().toString())
            .let { messageSender.sendResponse(update, user, it) }
    }

}