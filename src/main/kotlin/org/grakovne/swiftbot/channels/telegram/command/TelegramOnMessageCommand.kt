package org.grakovne.swiftbot.channels.telegram.command

import arrow.core.Either
import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.Update
import org.grakovne.swiftbot.channels.telegram.TelegramUpdateProcessingError

interface TelegramOnMessageCommand {

    fun getHelp(): String

    fun isCommandAcceptable(update: Update): Boolean

    fun processUpdate(bot: TelegramBot, update: Update): Either<TelegramUpdateProcessingError, Unit>
}