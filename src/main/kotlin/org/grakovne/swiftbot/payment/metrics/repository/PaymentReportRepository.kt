package org.grakovne.swiftbot.payment.metrics.repository

import org.grakovne.swiftbot.payment.metrics.domain.PaymentReport
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface PaymentReportRepository : JpaRepository<PaymentReport, UUID> {

    fun findByPaymentIdOrderByCreatedAtDesc(paymentId: UUID): List<PaymentReport>
    fun findFirstByPaymentIdOrderByCreatedAtDesc(paymentId: UUID): PaymentReport?
}