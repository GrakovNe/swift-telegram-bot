package org.grakovne.swiftbot.channels.telegram.messaging

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.sequence
import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.Update
import org.grakovne.swiftbot.channels.telegram.TelegramUpdateProcessingError
import org.grakovne.swiftbot.dto.CommandType
import org.grakovne.swiftbot.localization.EnumLocalizationService
import org.grakovne.swiftbot.localization.HelpMessage
import org.grakovne.swiftbot.localization.HelpMessageItem
import org.grakovne.swiftbot.localization.MessageLocalizationService
import org.grakovne.swiftbot.user.domain.UserReference
import org.springframework.stereotype.Service

@Service
class HelpMessageSender(
    bot: TelegramBot,
    private val localizationService: MessageLocalizationService,
    private val enumLocalizationService: EnumLocalizationService
) : MessageSender(bot) {

    fun sendResponse(
        origin: Update,
        userReference: UserReference,
        helpMessage: List<Help>
    ): Either<TelegramUpdateProcessingError, Unit> {
        val targetLanguage = userReference.provideLanguage()

        return helpMessage
            .map {
                HelpMessageItem(
                    key = buildCommandUsage(it),
                    description = enumLocalizationService.localize(it.description, targetLanguage)
                )
            }
            .map { localizationService.localize(it, targetLanguage) }
            .sequence()
            .map { it.joinToString("\n") }
            .map { HelpMessage(it) }
            .flatMap { localizationService.localize(it, targetLanguage) }
            .mapLeft { TelegramUpdateProcessingError.INTERNAL_ERROR }
            .flatMap { sendRawMessage(origin, it) }
    }

    private fun buildCommandUsage(it: Help): String =
        "${it.key} ${if (it.arguments.isEmpty()) "" else it.arguments + " "}"
}

data class Help(
    val key: String,
    val description: CommandType,
    val arguments: String = ""
)