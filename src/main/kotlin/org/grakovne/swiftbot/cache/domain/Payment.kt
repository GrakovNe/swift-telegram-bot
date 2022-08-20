package org.grakovne.swiftbot.cache.domain

import java.time.Instant
import java.util.UUID
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class Payment(
    @Id
    val id: UUID,
    val status: String,
    val paymentLastUpdateAt: Instant,
    val lastModifiedAt: Instant
)