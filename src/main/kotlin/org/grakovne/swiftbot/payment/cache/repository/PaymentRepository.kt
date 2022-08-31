package org.grakovne.swiftbot.payment.cache.repository

import org.grakovne.swiftbot.dto.PaymentStatus
import org.grakovne.swiftbot.payment.cache.domain.Payment
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface PaymentRepository : JpaRepository<Payment, UUID> {

    fun countByStatusIn(status: List<PaymentStatus>): Long

    fun findFirstByStatusInOrderByLastModifiedAtAsc(status: List<PaymentStatus>): Payment?
}