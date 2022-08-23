package org.grakovne.swiftbot.payment.synchronization

import org.grakovne.swiftbot.dto.PaymentView
import org.grakovne.swiftbot.events.core.Event
import org.grakovne.swiftbot.events.core.EventListener
import org.grakovne.swiftbot.events.core.EventSender
import org.grakovne.swiftbot.events.core.EventType
import org.grakovne.swiftbot.events.payment.PaymentCacheOutdatedEvent
import org.grakovne.swiftbot.events.payment.PaymentLastUpdatedChangedEvent
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

    private fun processPaymentCacheOutdatedEvent(event: PaymentCacheOutdatedEvent) =
        paymentService
            .updateAndCache(event.id)
            .map { paymentView ->
                ifPaymentChangedAtChanged(paymentView, event)
                ifPaymentStatusChanged(paymentView, event)
            }

    private fun ifPaymentChangedAtChanged(paymentView: PaymentView, event: PaymentCacheOutdatedEvent) = paymentView
        .takeIf { it.status == event.status && it.lastUpdateTimestamp != event.changedAt }
        ?.let {
            eventSender.sendEvent(
                PaymentLastUpdatedChangedEvent(
                    id = it.id,
                    status = it.status,
                    changedAt = it.lastUpdateTimestamp
                )
            )
        }


    private fun ifPaymentStatusChanged(paymentView: PaymentView, event: PaymentCacheOutdatedEvent) = paymentView
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