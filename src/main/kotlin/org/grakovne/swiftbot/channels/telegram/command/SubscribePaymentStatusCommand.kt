package org.grakovne.swiftbot.channels.telegram.command

import arrow.core.Either
import com.pengrad.telegrambot.model.Update
import org.grakovne.swiftbot.channels.telegram.TelegramUpdateProcessingError
import org.grakovne.swiftbot.channels.telegram.messaging.SimpleMessageSender
import org.grakovne.swiftbot.dto.CommandType
import org.grakovne.swiftbot.events.core.EventSender
import org.grakovne.swiftbot.events.internal.LogLevel
import org.grakovne.swiftbot.events.internal.LoggingEvent
import org.grakovne.swiftbot.localization.IncorrectPaymentId
import org.grakovne.swiftbot.localization.PaymentUpdatedSubscribed
import org.grakovne.swiftbot.user.UserReferenceService
import org.grakovne.swiftbot.user.domain.UserReference
import org.springframework.stereotype.Service
import java.util.*
import java.util.regex.Pattern

@Service
class SubscribePaymentStatusCommand(
    private val userReferenceService: UserReferenceService,
    private val eventSender: EventSender,
    private val messageSender: SimpleMessageSender
) : TelegramOnMessageCommand {

    override fun getKey(): String = "subscribe"
    override fun getArguments(): String = "[UETR]"
    override fun getType() = CommandType.SUBSCRIBE_UPDATES

    override fun accept(
        update: Update,
        user: UserReference
    ): Either<TelegramUpdateProcessingError, Unit> {
        val pattern = Pattern.compile("[a-f0-9]{8}(?:-[a-f0-9]{4}){4}[a-f0-9]{8}")
        val matcher = pattern.matcher(update.message().text())

        if (!matcher.find()) {
            messageSender.sendResponse(update, user, IncorrectPaymentId(getKey()))
            return Either.Left(TelegramUpdateProcessingError.INVALID_REQUEST)
        }

        val paymentId = UUID.fromString(matcher.group(0))
        userReferenceService.subscribeToPayment(user, paymentId)

        return messageSender
            .sendResponse(update, user, PaymentUpdatedSubscribed)
            .tap {
                eventSender.sendEvent(
                    LoggingEvent(
                        LogLevel.DEBUG,
                        "subscribed to payment id $paymentId status changes"
                    )
                )
            }
    }
}