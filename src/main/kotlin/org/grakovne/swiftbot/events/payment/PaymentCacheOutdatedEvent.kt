package org.grakovne.swiftbot.events.payment

import org.grakovne.swiftbot.dto.PaymentStatus
import org.grakovne.swiftbot.events.core.Event
import org.grakovne.swiftbot.events.core.EventType
import java.util.UUID

data class PaymentCacheOutdatedEvent(
    val id: UUID,
    val status: PaymentStatus
) : Event(EventType.PAYMENT_CACHE_OUTDATED)