package org.grakovne.swiftbot.dto

import java.util.UUID

data class PaymentStatusRequest(
    val userReference: UUID,
    val paymentId: UUID
)