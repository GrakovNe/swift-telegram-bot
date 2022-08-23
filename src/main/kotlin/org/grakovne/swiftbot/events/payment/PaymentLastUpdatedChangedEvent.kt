package org.grakovne.swiftbot.events.payment

import org.grakovne.swiftbot.dto.PaymentStatus
import org.grakovne.swiftbot.events.core.Event
import org.grakovne.swiftbot.events.core.EventType
import java.time.Instant
import java.util.*

data class PaymentLastUpdatedChangedEvent(
    val id: UUID,
    val status: PaymentStatus,
    val changedAt: Instant
) : Event(EventType.PAYMENT_LAST_UPDATE_CHANGED)