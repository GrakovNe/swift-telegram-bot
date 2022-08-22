package org.grakovne.swiftbot.payment.cache.domain

import org.grakovne.swiftbot.dto.PaymentStatus
import java.time.Instant
import java.util.*
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id

@Entity
data class Payment(
    @Id
    val id: UUID,

    @Enumerated(EnumType.STRING)
    val status: PaymentStatus,
    val paymentLastUpdateAt: Instant,
    val lastModifiedAt: Instant
)