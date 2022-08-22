package org.grakovne.swiftbot.channels.telegram.command

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.SendMessage
import org.springframework.stereotype.Service

@Service
class SendHelpMessageCommand(private val onMessageCommands: List<TelegramOnMessageCommand>) {

    fun sendHelp(bot: TelegramBot, update: Update) {
        val header = """
            Unofficial SWIFT GPI Payments Tracker
            
            Author: @maxgrakov
            
            Available Commands:
            
            
        """.trimIndent()

        val footer = """
            
            Please feel free to use this bot carefully
        """.trimIndent()

        val message = onMessageCommands
            .map { it.getHelp() }
            .fold(header) { acc, help -> acc + help + "\n" }
            .let { it + footer }

        bot.execute(SendMessage(update.message().chat().id(), message)).isOk
    }
}