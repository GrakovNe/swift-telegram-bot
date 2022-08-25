package org.grakovne.swiftbot.channels.telegram.command

import arrow.core.Either
import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.UpdatesListener
import com.pengrad.telegrambot.model.BotCommand
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.request.SetMyCommands
import org.grakovne.swiftbot.channels.telegram.TelegramUpdateProcessingError
import org.grakovne.swiftbot.events.core.EventSender
import org.grakovne.swiftbot.events.internal.LogLevel.WARN
import org.grakovne.swiftbot.events.internal.LoggingEvent
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class TelegramOnMessageConfiguration(
    private val bot: TelegramBot,
    private val unknownCommand: SendHelpMessageCommand,
    private val commands: List<TelegramOnMessageCommand>,
    private val eventSender: EventSender
) {

    @PostConstruct
    fun onCreate() = bot
        .setUpdatesListener { updates ->
            onMessageBatch(updates)
            UpdatesListener.CONFIRMED_UPDATES_ALL
        }

    private fun onMessageBatch(batch: List<Update>) =
        batch
            .filter { update -> update.hasSender() }
            .forEach { update -> onMessage(update) }

    private fun onMessage(update: Update) = try {
        update
            .findCommand()
            .accept(bot, update)
            .tap { bot.execute(SetMyCommands(*commandsDescription)) }
    } catch (ex: Exception) {
        eventSender.sendEvent(LoggingEvent(WARN, "Internal Exception. Message = ${ex.message}"))
        Either.Left(TelegramUpdateProcessingError.INTERNAL_ERROR)
    }

    private fun Update.findCommand() =
        commands
            .find { command -> command.isAcceptable(this) }
            ?: unknownCommand

    private fun Update.hasSender() = this.message()?.chat()?.id() != null
    private val commandsDescription = commands.map { BotCommand(it.getKey(), it.getHelp()) }.toTypedArray()
}