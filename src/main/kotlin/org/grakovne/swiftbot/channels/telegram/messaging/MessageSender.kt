package org.grakovne.swiftbot.channels.telegram.messaging

import arrow.core.Either
import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.SendMessage
import org.grakovne.swiftbot.channels.telegram.TelegramUpdateProcessingError
import org.grakovne.swiftbot.localization.Language
import org.grakovne.swiftbot.localization.MessageType
import org.grakovne.swiftbot.user.domain.UserReference
import org.springframework.stereotype.Service

@Service
abstract class MessageSender(private val bot: TelegramBot) {

    protected fun sendRawMessage(
        origin: Update,
        text: String,
        type: MessageType = MessageType.HTML,
    ): Either<TelegramUpdateProcessingError, Unit> {
        val isMessageSent = SendMessage(origin.message().chat().id(), text)
            .setParseMode(type)
            .let { bot.execute(it).isOk }

        return when (isMessageSent) {
            true -> Either.Right(Unit)
            false -> Either.Left(TelegramUpdateProcessingError.RESPONSE_NOT_SENT)
        }
    }
}

private fun SendMessage.setParseMode(type: MessageType): SendMessage = when (type) {
    MessageType.PLAIN -> this
    MessageType.HTML -> this.parseMode(ParseMode.HTML)
}

fun UserReference.provideLanguage(): Language = when (this.language) {
    "ru" -> Language.RUSSIAN
    else -> Language.ENGLISH
}