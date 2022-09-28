package org.grakovne.swiftbot.channels.telegram.notification

import org.grakovne.swiftbot.channels.telegram.messaging.SimpleMessageSender
import org.grakovne.swiftbot.events.core.Event
import org.grakovne.swiftbot.events.core.EventListener
import org.grakovne.swiftbot.events.core.EventSender
import org.grakovne.swiftbot.events.core.EventType
import org.grakovne.swiftbot.events.internal.LogLevel
import org.grakovne.swiftbot.events.internal.LoggingEvent
import org.grakovne.swiftbot.events.payment.PaymentLastUpdatedChangedEvent
import org.grakovne.swiftbot.localization.PaymentLastUpdateDateChanged
import org.grakovne.swiftbot.user.UserReferenceService
import org.grakovne.swiftbot.user.domain.UserReference
import org.grakovne.swiftbot.user.domain.UserReferenceSource
import org.springframework.stereotype.Service

@Service
class PaymentLastUpdateDateChangeNotificationService(
    private val userReferenceService: UserReferenceService,
    private val eventSender: EventSender,
    private val messageSender: SimpleMessageSender
) : EventListener {

    override fun acceptableEvents(): List<EventType> = listOf(EventType.PAYMENT_LAST_UPDATE_CHANGED)

    override fun onEvent(event: Event) {
        when (event) {
            is PaymentLastUpdatedChangedEvent -> processPaymentStatusChangedEvent(event)
        }

    }

    private fun processPaymentStatusChangedEvent(event: PaymentLastUpdatedChangedEvent) {
        userReferenceService
            .fetchUsersWithSubscription(event.id, UserReferenceSource.TELEGRAM)
            .map { sendNotification(it, event) }
    }

    private fun sendNotification(user: UserReference, event: PaymentLastUpdatedChangedEvent) =
        messageSender.sendResponse(
            user.id,
            user,
            PaymentLastUpdateDateChanged(
                event.id,
                event.status,
                event.changedAt
            )
        ).tap {
            eventSender.sendEvent(
                LoggingEvent(
                    LogLevel.DEBUG,
                    "Payment last update date notification sent"
                )
            )
        }
}