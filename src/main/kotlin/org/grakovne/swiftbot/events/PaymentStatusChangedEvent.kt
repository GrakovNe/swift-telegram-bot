package org.grakovne.swiftbot.events

import org.grakovne.swiftbot.dto.PaymentStatus
import org.grakovne.swiftbot.events.core.Event
import org.grakovne.swiftbot.events.core.EventType
import java.time.Instant
import java.util.UUID

data class PaymentStatusChangedEvent(
    val id: UUID,
    val from: PaymentStatus,
    val to: PaymentStatus,
    val changedAt: Instant
) : Event(EventType.PAYMENT_STATUS_CHANGED)