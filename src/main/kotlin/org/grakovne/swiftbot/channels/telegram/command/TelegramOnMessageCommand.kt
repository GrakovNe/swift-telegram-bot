package org.grakovne.swiftbot.channels.telegram.command

import arrow.core.Either
import com.pengrad.telegrambot.model.Update
import org.grakovne.swiftbot.channels.telegram.TelegramUpdateProcessingError
import org.grakovne.swiftbot.dto.CommandType
import org.grakovne.swiftbot.user.domain.UserReference

interface TelegramOnMessageCommand {

    fun getKey(): String
    fun getType(): CommandType
    fun getArguments(): String = ""

    fun isAcceptable(update: Update): Boolean = update.message().text().startsWith("/" + getKey())
    fun accept(update: Update, user: UserReference): Either<TelegramUpdateProcessingError, Unit>
}