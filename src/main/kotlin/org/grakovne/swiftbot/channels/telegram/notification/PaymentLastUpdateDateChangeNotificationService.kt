package org.grakovne.swiftbot.channels.telegram.notification

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.SendMessage
import org.grakovne.swiftbot.common.converter.toMessage
import org.grakovne.swiftbot.events.core.Event
import org.grakovne.swiftbot.events.core.EventListener
import org.grakovne.swiftbot.events.core.EventSender
import org.grakovne.swiftbot.events.core.EventType
import org.grakovne.swiftbot.events.internal.LogLevel
import org.grakovne.swiftbot.events.internal.LoggingEvent
import org.grakovne.swiftbot.events.payment.PaymentLastUpdatedChangedEvent
import org.grakovne.swiftbot.events.payment.PaymentStatusChangedEvent
import org.grakovne.swiftbot.user.UserReferenceService
import org.grakovne.swiftbot.user.domain.UserReferenceSource
import org.springframework.stereotype.Service

@Service
class PaymentLastUpdateDateChangeNotificationService(
    private val userReferenceService: UserReferenceService,
    private val bot: TelegramBot,
    private val eventSender: EventSender
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

    private fun sendNotification(chatId: String, event: PaymentLastUpdatedChangedEvent) =
        bot.execute(SendMessage(chatId, event.toMessage()).parseMode(ParseMode.HTML)).also {
            eventSender.sendEvent(
                LoggingEvent(
                    LogLevel.DEBUG,
                    "Payment last update date notification sent"
                )
            )
        }

    private fun PaymentLastUpdatedChangedEvent.toMessage(): String {
        return """
            Payment has been tracked in another bank
            
            <b>UETR</b>: ${this.id}
            
            <b>Payment status</b>: ${this.status}
            <b>Last update</b>: ${this.changedAt.toMessage()}
        """.trimIndent()
    }
}