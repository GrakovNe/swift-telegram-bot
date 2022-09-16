package org.grakovne.swiftbot.payment.metrics.domain

import org.grakovne.swiftbot.dto.PaymentStatus
import java.time.Instant
import java.util.*
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id

@Entity
data class PaymentReport(
    @Id
    val id: UUID,
    val paymentId: UUID,
    @Enumerated(EnumType.STRING)
    val newStatus: PaymentStatus,
    val timestamp: Instant,
    val createdAt: Instant
)