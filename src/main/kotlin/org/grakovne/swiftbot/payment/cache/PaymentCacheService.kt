package org.grakovne.swiftbot.payment.cache

import arrow.core.Either
import org.grakovne.swiftbot.dto.PaymentStatus
import org.grakovne.swiftbot.dto.PaymentView
import org.grakovne.swiftbot.payment.cache.domain.Payment
import org.grakovne.swiftbot.payment.cache.repository.PaymentRepository
import org.grakovne.swiftbot.payment.metrics.PaymentReportService
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class PaymentCacheService(
    private val paymentRepository: PaymentRepository,
    private val paymentReportService: PaymentReportService
) {

    fun countTotal(): Long = paymentRepository.countByStatusIn(PaymentStatus.values().toList())

    fun countSuccessful(): Long = paymentRepository.countByStatusIn(PaymentStatus.successStatues())

    fun countProcessing(): Long = paymentRepository.countByStatusIn(PaymentStatus.processingStatuses())

    fun countFailed(): Long = paymentRepository.countByStatusIn(PaymentStatus.failedStatues())

    fun fetchOldestProcessing() =
        paymentRepository.findFirstByStatusInOrderByLastModifiedAtAsc(PaymentStatus.processingStatuses())

    fun fetchCached(id: UUID): Either<CacheError, PaymentView> = paymentRepository
        .findById(id)
        .orElseGet { null }
        ?.toView()
        ?.let { Either.Right(it) }
        ?: Either.Left(NotFound(id))

    fun storePayment(dto: PaymentView) = dto
        .toPayment()
        .let {
            val payment = paymentRepository.save(it)
            paymentReportService.createReportEntry(payment)
        }

    fun updateLastModifiedAt(id: UUID, now: Instant) = paymentRepository
        .findById(id)
        .orElseGet { null }
        ?.copy(lastModifiedAt = now)
        ?.let {
            val payment = paymentRepository.save(it)
            paymentReportService.createReportEntry(payment)

            payment
        }
}

private fun PaymentView.toPayment(): Payment {
    return Payment(
        id = this.id,
        status = this.status,
        paymentLastUpdateAt = this.lastUpdateTimestamp,
        lastModifiedAt = Instant.now()
    )
}

private fun Payment.toView() = PaymentView(
    id = this.id,
    status = this.status,
    lastUpdateTimestamp = this.paymentLastUpdateAt
)
