package org.grakovne.swiftbot.dto

import java.time.Instant
import java.util.UUID

data class PaymentStatus(
    val id: UUID,
    val status: String,
    val lastUpdateTimestamp: Instant
)