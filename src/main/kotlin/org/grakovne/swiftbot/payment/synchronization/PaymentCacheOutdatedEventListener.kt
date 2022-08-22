package org.grakovne.swiftbot.payment.synchronization

import org.grakovne.swiftbot.events.core.Event
import org.grakovne.swiftbot.events.core.EventListener
import org.grakovne.swiftbot.events.core.EventSender
import org.grakovne.swiftbot.events.core.EventType
import org.grakovne.swiftbot.events.payment.PaymentCacheOutdatedEvent
import org.grakovne.swiftbot.events.payment.PaymentStatusChangedEvent
import org.grakovne.swiftbot.payment.synchronization.payment.PaymentService
import org.springframework.stereotype.Service

@Service
class PaymentCacheOutdatedEventListener(
    private val paymentService: PaymentService,
    private val eventSender: EventSender
) : EventListener {
    override fun acceptableEvents(): List<EventType> = listOf(EventType.PAYMENT_CACHE_OUTDATED)

    override fun onEvent(event: Event) {
        when (event) {
            is PaymentCacheOutdatedEvent -> processPaymentCacheOutdatedEvent(event)
        }
    }

    private fun processPaymentCacheOutdatedEvent(event: PaymentCacheOutdatedEvent) {
        paymentService
            .updateAndCache(event.id)
            .map { paymentStatus ->
                paymentStatus
                    .takeIf { it.status != event.status }
                    ?.let {
                        eventSender
                            .sendEvent(
                                PaymentStatusChangedEvent(
                                    id = it.id,
                                    from = event.status,
                                    to = it.status,
                                    changedAt = it.lastUpdateTimestamp
                                )
                            )
                    }
            }
    }
}