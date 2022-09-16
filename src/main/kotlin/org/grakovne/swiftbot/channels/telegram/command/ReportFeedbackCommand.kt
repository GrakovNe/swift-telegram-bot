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
import org.grakovne.swiftbot.feedback.FeedbackService
import org.grakovne.swiftbot.user.UserReferenceService
import org.grakovne.swiftbot.user.domain.UserReference
import org.grakovne.swiftbot.user.domain.UserReferenceSource
import org.springframework.stereotype.Service

@Service
class ReportFeedbackCommand(
    private val userReferenceService: UserReferenceService,
    private val feedbackService: FeedbackService,
    private val eventSender: EventSender
) : TelegramOnMessageCommand {
    override fun getKey(): String = "report"

    override fun getHelp(): String = "Reports a text message to the developer of this bot"

    override fun accept(
        bot: TelegramBot,
        update: Update,
        user: UserReference
    ): Either<TelegramUpdateProcessingError, Unit> {
        val text = update.message().text()?.substringAfter(getKey())?.trim()

        if (text == null || text.isEmpty()) {
            bot.execute(
                SendMessage(
                    update.message().chat().id(),
                    "Please provide non-empty report\n\n<b>Example</b>: <i>/report I have few words for you!</i>"
                ).parseMode(ParseMode.HTML)
            )

            return Either.Left(TelegramUpdateProcessingError.INVALID_REQUEST)
        }

        userReferenceService
            .fetchUser(
                update.message().chat().id().toString(),
                UserReferenceSource.TELEGRAM,
                update.message().from().languageCode() ?: "en"
            )
            .let { feedbackService.reportFeedback(it, text) }

        val isMessageSent = bot.execute(
            SendMessage(update.message().chat().id(), "Thank you for your feedback!\n\n")
        ).isOk

        return when (isMessageSent) {
            true -> {
                eventSender.sendEvent(
                    LoggingEvent(
                        LogLevel.WARN,
                        "Received feedback message: $text"
                    )
                )
                Either.Right(Unit)
            }
            false -> {
                LoggingEvent(
                    LogLevel.WARN,
                    "Tried to get feedback with text $text but failed"
                )
                Either.Left(TelegramUpdateProcessingError.RESPONSE_NOT_SENT)
            }
        }
    }

}