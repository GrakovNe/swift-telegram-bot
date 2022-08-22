package org.grakovne.swiftbot.dto

import java.util.*

data class PaymentStatusRequest(
    val userReference: UUID,
    val paymentId: UUID
)