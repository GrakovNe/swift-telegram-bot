package org.grakovne.swiftbot.channels.telegram.notification

import com.pengrad.telegrambot.TelegramBot
import org.grakovne.swiftbot.channels.telegram.messaging.SimpleMessageSender
import org.grakovne.swiftbot.events.core.Event
import org.grakovne.swiftbot.events.core.EventListener
import org.grakovne.swiftbot.events.core.EventSender
import org.grakovne.swiftbot.events.core.EventType
import org.grakovne.swiftbot.events.internal.LogLevel
import org.grakovne.swiftbot.events.internal.LoggingEvent
import org.grakovne.swiftbot.events.payment.PaymentStatusChangedEvent
import org.grakovne.swiftbot.localization.PaymentStatusChanged
import org.grakovne.swiftbot.user.UserReferenceService
import org.grakovne.swiftbot.user.domain.UserReference
import org.grakovne.swiftbot.user.domain.UserReferenceSource
import org.springframework.stereotype.Service

@Service
class PaymentStatusChangeNotificationService(
    private val userReferenceService: UserReferenceService,
    private val eventSender: EventSender,
    private val messageSender: SimpleMessageSender
) : EventListener {

    override fun acceptableEvents(): List<EventType> = listOf(EventType.PAYMENT_STATUS_CHANGED)

    override fun onEvent(event: Event) {
        when (event) {
            is PaymentStatusChangedEvent -> processPaymentStatusChangedEvent(event)
        }

    }

    private fun processPaymentStatusChangedEvent(event: PaymentStatusChangedEvent) {
        userReferenceService
            .fetchUsersWithSubscription(event.id, UserReferenceSource.TELEGRAM)
            .map { sendNotification(it, event) }
    }

    private fun sendNotification(user: UserReference, event: PaymentStatusChangedEvent) =
        messageSender
            .sendResponse(
                chatId = user.id,
                userReference = user,
                message = PaymentStatusChanged(
                    paymentId = event.id,
                    previousStatus = event.from,
                    currentStatus = event.to,
                    changedAt = event.changedAt
                )
            ).tap {
                eventSender.sendEvent(
                    LoggingEvent(
                        LogLevel.DEBUG,
                        "Payment status change notification sent"
                    )
                )
            }
}