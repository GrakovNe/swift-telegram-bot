package org.grakovne.swiftbot.channels.telegram.notification

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.SendMessage
import org.grakovne.swiftbot.common.converter.toMessage
import org.grakovne.swiftbot.events.payment.PaymentStatusChangedEvent
import org.grakovne.swiftbot.events.core.Event
import org.grakovne.swiftbot.events.core.EventListener
import org.grakovne.swiftbot.events.core.EventType
import org.grakovne.swiftbot.user.UserReferenceService
import org.grakovne.swiftbot.user.domain.UserReferenceSource
import org.springframework.stereotype.Service

@Service
class PaymentStatusChangeNotificationService(
    val userReferenceService: UserReferenceService,
    val bot: TelegramBot
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

    private fun sendNotification(chatId: String, event: PaymentStatusChangedEvent) =
        bot.execute(SendMessage(chatId, event.toMessage()).parseMode(ParseMode.HTML))

    private fun PaymentStatusChangedEvent.toMessage(): String {
        return """
            Payment status has been changed
            
            <b>UETR</b>: ${this.id}
            
            <b>Previous status</b>: ${this.from}
            <b>Current status</b>: ${this.to}
            <b>Last update</b>: ${this.changedAt.toMessage()}
        """.trimIndent()
    }
}