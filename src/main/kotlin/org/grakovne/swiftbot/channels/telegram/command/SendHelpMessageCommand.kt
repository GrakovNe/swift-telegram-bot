package org.grakovne.swiftbot.channels.telegram.command

import arrow.core.Either
import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.request.SendMessage
import org.grakovne.swiftbot.channels.telegram.TelegramUpdateProcessingError
import org.springframework.stereotype.Service

@Service
class SendHelpMessageCommand(private val onMessageCommands: List<TelegramOnMessageCommand>) : TelegramOnMessageCommand {

    override fun getKey(): String = "/help"
    override fun getHelp() = "Prints help"

    override fun accept(bot: TelegramBot, update: Update): Either<TelegramUpdateProcessingError, Unit> {
        val isMessageSent = bot.execute(SendMessage(update.message().chat().id(), buildHelp())).isOk

        return when (isMessageSent) {
            true -> Either.Right(Unit)
            false -> Either.Left(TelegramUpdateProcessingError.RESPONSE_NOT_SENT)
        }
    }

    private fun buildHelp(): String {
        val header = """
            Unofficial SWIFT GPI Payments Tracker
            
            Author: @maxgrakov
            
            Available Commands:
            
            /help - ${getHelp()}
            
        """.trimIndent()

        val footer = """
            
            Please feel free to use this bot carefully
        """.trimIndent()

        return onMessageCommands
            .map { it.getHelp() }
            .fold(header) { acc, help -> acc + help + "\n" }
            .let { it + footer }
    }
}