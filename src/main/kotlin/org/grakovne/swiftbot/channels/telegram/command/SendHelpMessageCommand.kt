package org.grakovne.swiftbot.channels.telegram.command

import arrow.core.Either
import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.request.SendMessage
import org.grakovne.swiftbot.channels.telegram.TelegramUpdateProcessingError
import org.grakovne.swiftbot.events.core.EventSender
import org.grakovne.swiftbot.events.internal.LogLevel
import org.grakovne.swiftbot.events.internal.LoggingEvent
import org.grakovne.swiftbot.user.domain.UserReference
import org.springframework.stereotype.Service

@Service
class SendHelpMessageCommand(
    private val onMessageCommands: List<TelegramOnMessageCommand>,
    private val eventSender: EventSender
) : TelegramOnMessageCommand {

    override fun getKey(): String = "help"
    override fun getHelp() = "Prints help"

    override fun accept(
        bot: TelegramBot,
        update: Update,
        user: UserReference
    ): Either<TelegramUpdateProcessingError, Unit> {
        val isMessageSent = bot.execute(SendMessage(update.message().chat().id(), buildHelp())).isOk

        return when (isMessageSent) {
            true -> {
                eventSender.sendEvent(
                    LoggingEvent(
                        LogLevel.DEBUG,
                        "Help text was sent in response on origin message: ${update.message().text()}"
                    )
                )
                Either.Right(Unit)
            }
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
            
            Please note that we don't collect any detailed payment information
        """.trimIndent()

        return onMessageCommands
            .map { buildCommandUsage(it) }
            .fold(header) { acc, help -> acc + help + "\n" }
            .let { it + footer }
    }

    private fun buildCommandUsage(it: TelegramOnMessageCommand) =
        "/${it.getKey()} ${if (it.getArguments().isEmpty()) "" else it.getArguments() + " "}- ${it.getHelp()}"
}