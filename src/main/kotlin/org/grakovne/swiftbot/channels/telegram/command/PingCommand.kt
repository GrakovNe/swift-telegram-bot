package org.grakovne.swiftbot.channels.telegram.command

import arrow.core.Either
import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.request.SendMessage
import org.grakovne.swiftbot.channels.telegram.TelegramUpdateProcessingError
import org.springframework.stereotype.Service

@Service
class PingCommand : TelegramOnMessageCommand {

    override fun getKey(): String = "/ping"
    override fun getHelp(): String = "/ping - Sends dummy text"

    override fun accept(bot: TelegramBot, update: Update): Either<TelegramUpdateProcessingError, Unit> {
        val isMessageSent = bot
            .execute(
                SendMessage(
                    update.message().chat().id(),
                    "The quick brown fox jumps over the lazy dog"
                )
            ).isOk

        return when (isMessageSent) {
            true -> Either.Right(Unit)
            false -> Either.Left(TelegramUpdateProcessingError.RESPONSE_NOT_SENT)
        }
    }
}