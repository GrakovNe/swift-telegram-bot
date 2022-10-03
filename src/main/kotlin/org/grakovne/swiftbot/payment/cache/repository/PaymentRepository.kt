package org.grakovne.swiftbot.payment.cache.repository

import org.grakovne.swiftbot.dto.PaymentStatus
import org.grakovne.swiftbot.payment.cache.domain.Payment
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant
import java.util.*

interface PaymentRepository : JpaRepository<Payment, UUID> {

    fun countByStatusIn(status: List<PaymentStatus>): Long

    fun countByStatusInAndPaymentLastUpdateAtLessThan(status: List<PaymentStatus>, date: Instant): Long

    fun countByStatusInAndPaymentLastUpdateAtBetween(status: List<PaymentStatus>, from: Instant, to: Instant): Long

    fun findFirstByStatusInOrderByLastModifiedAtAsc(status: List<PaymentStatus>): Payment?
}