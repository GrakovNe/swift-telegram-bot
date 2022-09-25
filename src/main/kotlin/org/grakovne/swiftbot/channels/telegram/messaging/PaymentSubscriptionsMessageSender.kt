package org.grakovne.swiftbot.channels.telegram.messaging

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.sequence
import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.Update
import org.grakovne.swiftbot.channels.telegram.TelegramUpdateProcessingError
import org.grakovne.swiftbot.localization.MessageLocalizationService
import org.grakovne.swiftbot.localization.UserSubscriptionItem
import org.grakovne.swiftbot.localization.UserSubscriptions
import org.grakovne.swiftbot.user.domain.UserReference
import org.springframework.stereotype.Service
import java.util.*

@Service
class PaymentSubscriptionsMessageSender(
    bot: TelegramBot,
    private val localizationService: MessageLocalizationService
) : MessageSender(bot) {

    fun sendResponse(
        origin: Update,
        userReference: UserReference,
        subscriptions: List<UUID>
    ): Either<TelegramUpdateProcessingError, Unit> {
        val targetLanguage = userReference.provideLanguage()

        return subscriptions
            .map { UserSubscriptionItem(it) }
            .map { localizationService.localize(it, targetLanguage) }
            .sequence()
            .map { it.joinToString("\n") }
            .map { UserSubscriptions(it) }
            .flatMap { localizationService.localize(it, targetLanguage) }
            .mapLeft { TelegramUpdateProcessingError.INTERNAL_ERROR }
            .flatMap { sendRawMessage(origin, it) }
    }
}