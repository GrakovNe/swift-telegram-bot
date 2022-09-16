package org.grakovne.swiftbot.channels.telegram.command

import arrow.core.Either
import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.SendMessage
import org.grakovne.swiftbot.channels.telegram.TelegramUpdateProcessingError
import org.grakovne.swiftbot.events.core.EventSender
import org.grakovne.swiftbot.events.internal.LogLevel
import org.grakovne.swiftbot.events.internal.LoggingEvent
import org.grakovne.swiftbot.user.UserReferenceService
import org.grakovne.swiftbot.user.domain.UserReference
import org.springframework.stereotype.Service
import java.util.*
import java.util.regex.Pattern

@Service
class UnsubscribePaymentStatusCommand(
    private val userReferenceService: UserReferenceService,
    private val eventSender: EventSender
) : TelegramOnMessageCommand {

    override fun getKey(): String = "unsubscribe"
    override fun getArguments(): String = "<UETR>"
    override fun getHelp(): String = "Unsubscribes for a status changes notifications"

    override fun accept(
        bot: TelegramBot,
        update: Update,
        user: UserReference
    ): Either<TelegramUpdateProcessingError, Unit> {
        val pattern = Pattern.compile("[a-f0-9]{8}(?:-[a-f0-9]{4}){4}[a-f0-9]{8}")
        val matcher = pattern.matcher(update.message().text())

        if (!matcher.find()) {
            bot.execute(
                SendMessage(
                    update.message().chat().id(),
                    "Please provide valid UETR\n\n<b>Example</b>: <i>/unsubscribe 1b5c013c-8601-46ba-a982-e88848140329</i>"
                ).parseMode(ParseMode.HTML)
            )
            return Either.Left(TelegramUpdateProcessingError.INVALID_REQUEST)
        }

        val paymentId = UUID.fromString(matcher.group(0))
        userReferenceService.unsubscribeFromPayment(user, paymentId)

        val isMessageSent = bot.execute(SendMessage(update.message().chat().id(), "Unsubscribed")).isOk

        return when (isMessageSent) {
            true -> {
                eventSender.sendEvent(
                    LoggingEvent(
                        LogLevel.DEBUG,
                        "unsubscribed from payment id $paymentId status changes"
                    )
                )
                Either.Right(Unit)
            }
            false -> {
                eventSender.sendEvent(
                    LoggingEvent(
                        LogLevel.WARN,
                        "Unable to unsubscribe user ${update.message().chat().id()} from payment $paymentId"
                    )
                )
                Either.Left(TelegramUpdateProcessingError.RESPONSE_NOT_SENT)
            }
        }
    }
}