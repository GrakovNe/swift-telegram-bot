package org.grakovne.swiftbot.channels.telegram.command

import com.pengrad.telegrambot.model.Update
import org.grakovne.swiftbot.channels.telegram.messaging.Help
import org.grakovne.swiftbot.channels.telegram.messaging.HelpMessageSender
import org.grakovne.swiftbot.dto.CommandType
import org.grakovne.swiftbot.events.core.EventSender
import org.grakovne.swiftbot.events.internal.LogLevel
import org.grakovne.swiftbot.events.internal.LoggingEvent
import org.grakovne.swiftbot.user.domain.UserReference
import org.springframework.stereotype.Service

@Service
class SendHelpMessageCommand(
    private val onMessageCommands: List<TelegramOnMessageCommand>,
    private val eventSender: EventSender,
    private val helpMessageSender: HelpMessageSender
) : TelegramOnMessageCommand {

    override fun getKey(): String = "help"
    override fun getType() = CommandType.SEND_HELP

    override fun accept(
        update: Update,
        user: UserReference
    ) = onMessageCommands
        .map { Help(it.getKey(), it.getType(), it.getArguments()) }
        .let { helpMessageSender.sendResponse(update, user, it) }
        .tap {
            eventSender.sendEvent(
                LoggingEvent(
                    LogLevel.DEBUG,
                    "Help text was sent in response on origin message: ${update.message().text()}"
                )
            )
        }
}