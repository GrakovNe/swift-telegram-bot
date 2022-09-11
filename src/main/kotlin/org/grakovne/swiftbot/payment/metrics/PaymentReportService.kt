package org.grakovne.swiftbot.payment.metrics

import org.grakovne.swiftbot.payment.cache.domain.Payment
import org.grakovne.swiftbot.payment.metrics.domain.PaymentReport
import org.grakovne.swiftbot.payment.metrics.repository.PaymentReportRepository
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class PaymentReportService(private val repository: PaymentReportRepository) {

    fun createReportEntry(payment: Payment): PaymentReport {
        val latestReport = repository.findFirstByPaymentIdOrderByCreatedAtDesc(payment.id) ?: createEntry(payment)

        return when (latestReport.hasDiff(payment)) {
            true -> createEntry(payment)
            false -> latestReport
        }
    }

    private fun PaymentReport.hasDiff(payment: Payment) =
        payment.status != this.newStatus || payment.paymentLastUpdateAt != this.timestamp

    private fun createEntry(payment: Payment) = PaymentReport(
        id = UUID.randomUUID(),
        paymentId = payment.id,
        newStatus = payment.status,
        timestamp = payment.paymentLastUpdateAt,
        createdAt = Instant.now()
    ).let(repository::save)
}