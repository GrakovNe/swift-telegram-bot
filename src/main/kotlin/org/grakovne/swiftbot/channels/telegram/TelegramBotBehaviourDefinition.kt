package org.grakovne.swiftbot.channels.telegram

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.UpdatesListener
import org.grakovne.swiftbot.channels.telegram.command.SendHelpMessageCommand
import org.grakovne.swiftbot.channels.telegram.command.TelegramOnMessageCommand
import org.springframework.stereotype.Service


@Service
class TelegramBotBehaviourDefinition(
    bot: TelegramBot,
    helpMessageCommand: SendHelpMessageCommand,
    onMessageCommands: List<TelegramOnMessageCommand>
) {

    init {
        bot.setUpdatesListener {
            it
                .map { update ->
                    onMessageCommands
                        .find { command -> command.isCommandAcceptable(update) }
                        ?.processUpdate(bot, update)
                        ?: helpMessageCommand.sendHelp(bot, update)
                }

            UpdatesListener.CONFIRMED_UPDATES_ALL
        }
    }
}