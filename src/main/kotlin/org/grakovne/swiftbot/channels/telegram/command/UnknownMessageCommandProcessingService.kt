package org.grakovne.swiftbot.channels.telegram.command

import com.pengrad.telegrambot.model.Update
import org.grakovne.swiftbot.utils.findPaymentId
import org.springframework.stereotype.Service

@Service
class UnknownMessageCommandProcessingService(
    private val helpMessageCommand: SendHelpMessageCommand,
    private val checkPaymentStatusCommand: CheckPaymentStatusCommand
) {

    fun findCommand(update: Update): TelegramOnMessageCommand {
        if (update.findPaymentId() != null) {
            return checkPaymentStatusCommand
        }

        return helpMessageCommand
    }
}