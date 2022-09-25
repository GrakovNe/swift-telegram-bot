package org.grakovne.swiftbot.localization

import org.grakovne.swiftbot.dto.PaymentStatus
import java.time.Instant
import java.util.*

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

data class BotMetrics(
    val countSuccessfully: Long,
    val countProcessing: Long,
    val countFailed: Long,
    val countTotal: Long
) : Message("bot_metrics")

data class UserSubscriptions(
    val subscriptionItems: String
) : Message("user_subscriptions")

data class UserSubscriptionItem(
    val paymentId: UUID
) : Message("user_subscription_item")