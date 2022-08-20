package org.grakovne.swiftbot.cache.repository

import org.grakovne.swiftbot.cache.domain.Payment
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant
import java.util.UUID

interface PaymentRepository: JpaRepository<Payment, UUID> {

    fun findByIdAndLastModifiedAtLessThan(id: UUID, timestamp: Instant): Payment?
}