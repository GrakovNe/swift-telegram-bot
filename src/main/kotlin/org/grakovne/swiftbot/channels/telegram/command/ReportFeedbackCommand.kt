package org.grakovne.swiftbot.channels.telegram.command

import arrow.core.Either
import com.pengrad.telegrambot.model.Update
import org.grakovne.swiftbot.channels.telegram.TelegramUpdateProcessingError
import org.grakovne.swiftbot.channels.telegram.messaging.SimpleMessageSender
import org.grakovne.swiftbot.dto.CommandType
import org.grakovne.swiftbot.events.core.EventSender
import org.grakovne.swiftbot.events.internal.LogLevel
import org.grakovne.swiftbot.events.internal.LoggingEvent
import org.grakovne.swiftbot.feedback.FeedbackService
import org.grakovne.swiftbot.localization.ReportAccepted
import org.grakovne.swiftbot.localization.ReportNotAcceptedEmpty
import org.grakovne.swiftbot.user.UserReferenceService
import org.grakovne.swiftbot.user.domain.UserReference
import org.grakovne.swiftbot.user.domain.UserReferenceSource
import org.springframework.stereotype.Service

@Service
class ReportFeedbackCommand(
    private val userReferenceService: UserReferenceService,
    private val feedbackService: FeedbackService,
    private val eventSender: EventSender,
    private val messageSender: SimpleMessageSender
) : TelegramOnMessageCommand {

    override fun getKey(): String = "report"
    override fun getType() = CommandType.REPORT_FEEDBACK

    override fun accept(
        update: Update,
        user: UserReference
    ): Either<TelegramUpdateProcessingError, Unit> {
        val text = update.message().text()?.substringAfter(getKey())?.trim()

        if (text == null || text.isEmpty()) {
            messageSender.sendResponse(update, user, ReportNotAcceptedEmpty)
            return Either.Left(TelegramUpdateProcessingError.INVALID_REQUEST)
        }

        userReferenceService
            .fetchUser(
                update.message().chat().id().toString(),
                UserReferenceSource.TELEGRAM,
                update.message().from().languageCode() ?: "en"
            )
            .let { feedbackService.reportFeedback(it, text) }

        return messageSender
            .sendResponse(update, user, ReportAccepted)
            .tap {
                eventSender.sendEvent(
                    LoggingEvent(
                        LogLevel.WARN,
                        "Received feedback message: $text"
                    )
                )
            }
    }

}