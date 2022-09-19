package org.grakovne.swiftbot.localization

import org.grakovne.swiftbot.dto.PaymentStatus
import java.time.Instant
import java.util.UUID

sealed class Message(val templateName: String)

data class PaymentStatusMessage(
    val paymentId: UUID,
    val status: PaymentStatus,
    val lastUpdateTimestamp: Instant
) : Message("payment_status")

data class PaymentStatusWithHistoryMessage(
    val paymentId: UUID,
    val status: PaymentStatus,
    val lastUpdateTimestamp: Instant,
    val history: String
) : Message("payment_status_with_history")

data class PaymentStatusHistoryItem(
    val timestamp: Instant,
    val status: PaymentStatus
) : Message("payment_status_history_item")