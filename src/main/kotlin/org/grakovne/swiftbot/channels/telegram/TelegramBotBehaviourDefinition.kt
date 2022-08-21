package org.grakovne.swiftbot.channels.telegram

import arrow.core.Either
import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.UpdatesListener
import org.grakovne.swiftbot.channels.telegram.command.TelegramOnMessageCommand
import org.springframework.stereotype.Service


@Service
class TelegramBotBehaviourDefinition(bot: TelegramBot, commands: List<TelegramOnMessageCommand>) {

    init {
        bot.setUpdatesListener {
            it
                .flatMap { update ->
                    commands
                        .filter { command -> command.isCommandAcceptable(update) }
                        .map { command -> command.processUpdate(bot, update) }
                }

            UpdatesListener.CONFIRMED_UPDATES_ALL
        }
    }
}