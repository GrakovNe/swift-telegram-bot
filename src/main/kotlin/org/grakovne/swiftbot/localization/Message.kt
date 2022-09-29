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
    val totalSuccessfully: Long,
    val totalProcessing: Long,
    val totalFailed: Long,
    val totalCount: Long,
    val lastWeekSuccessfully: Long,
    val lastWeekProcessing: Long,
    val lastWeekFailed: Long,
    val lastWeekCount: Long,
) : Message("bot_metrics")

data class UserSubscriptions(
    val subscriptionItems: String
) : Message("user_subscriptions")

data class UserSubscriptionItem(
    val paymentId: UUID
) : Message("user_subscription_item")

data class PaymentStatusChanged(
    val paymentId: UUID,
    val previousStatus: PaymentStatus,
    val currentStatus: PaymentStatus,
    val changedAt: Instant
): Message("payment_status_changed")


data class PaymentLastUpdateDateChanged(
    val paymentId: UUID,
    val status: PaymentStatus,
    val changedAt: Instant
): Message("payment_last_update_date_changed")

object PaymentStatusNotFound : Message("payment_status_not_found")
object ReportAccepted : Message("report_accepted")
object ReportNotAcceptedEmpty : Message("report_not_accepted_empty_message")
object PaymentUpdatedSubscribed : Message("subscribed_payment_updates")
object PaymentUpdatedUnsubscribed : Message("unsubscribed_payment_updates")

data class IncorrectPaymentId(
    val key: String
) : Message("incorrect_payment_id")

data class HelpMessage(val items: String) : Message("help_message")

data class HelpMessageItem(
    val key: String,
    val description: String,
    val arguments: List<String> = emptyList()
) : Message("help_message_item")