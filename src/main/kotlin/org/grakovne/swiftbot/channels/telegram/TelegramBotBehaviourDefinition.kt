package org.grakovne.swiftbot.channels.telegram

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.UpdatesListener
import org.grakovne.swiftbot.channels.telegram.command.SendHelpMessageCommand
import org.grakovne.swiftbot.channels.telegram.command.TelegramOnMessageCommand
import org.grakovne.swiftbot.events.core.EventSender
import org.grakovne.swiftbot.events.internal.LogLevel
import org.grakovne.swiftbot.events.internal.LoggingEvent
import org.springframework.stereotype.Service

@Service
class TelegramBotBehaviourDefinition(
    bot: TelegramBot,
    helpMessageCommand: SendHelpMessageCommand,
    onMessageCommands: List<TelegramOnMessageCommand>,
    eventSender: EventSender
) {

    init {
        bot.setUpdatesListener {
            try {
                it
                    .filter { update -> update?.message()?.chat()?.id() != null }
                    .map { update ->
                        onMessageCommands
                            .find { command -> command.isCommandAcceptable(update) }
                            ?.processUpdate(bot, update)
                            ?: helpMessageCommand.sendHelp(bot, update)
                    }
            } catch (ex: Exception) {
                eventSender.sendEvent(
                    LoggingEvent(
                        LogLevel.WARN,
                        "Internal Exception. Message = ${ex.message}"
                    )
                )
            }

            UpdatesListener.CONFIRMED_UPDATES_ALL
        }
    }
}