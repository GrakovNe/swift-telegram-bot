package org.grakovne.swiftbot.dto

import java.time.Instant
import java.util.*

data class PaymentView(
    val id: UUID,
    val status: PaymentStatus,
    val lastUpdateTimestamp: Instant
)