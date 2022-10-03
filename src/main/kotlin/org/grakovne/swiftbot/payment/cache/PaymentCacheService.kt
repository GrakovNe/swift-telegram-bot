package org.grakovne.swiftbot.payment.cache

import arrow.core.Either
import org.grakovne.swiftbot.dto.PaymentStatus
import org.grakovne.swiftbot.dto.PaymentView
import org.grakovne.swiftbot.payment.PaymentProperties
import org.grakovne.swiftbot.payment.cache.domain.Payment
import org.grakovne.swiftbot.payment.cache.domain.PaymentCountReport
import org.grakovne.swiftbot.payment.cache.repository.PaymentRepository
import org.grakovne.swiftbot.payment.metrics.PaymentReportService
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.util.*

@Service
class PaymentCacheService(
    private val paymentRepository: PaymentRepository,
    private val paymentReportService: PaymentReportService,
    private val paymentProperties: PaymentProperties
) {

    fun countTotal(): PaymentCountReport {
        val to = Instant.now().minus(Duration.ofDays(paymentProperties.daysBeforeSuspended))

        return PaymentCountReport(
            total = paymentRepository.countByStatusIn(PaymentStatus.values().toList()),
            processing = paymentRepository.countByStatusIn(PaymentStatus.processingStatuses()),
            failed = paymentRepository.countByStatusIn(PaymentStatus.failedStatues()),
            successfully = paymentRepository.countByStatusIn(PaymentStatus.successStatues()),
            suspended = paymentRepository.countByStatusInAndPaymentLastUpdateAtLessThan(PaymentStatus.processingStatuses(), to),
        )
    }

    fun countLastWeek(): PaymentCountReport {
        val to = Instant.now()
        val from = to.minus(Duration.ofDays(7))

        return PaymentCountReport(
            total = paymentRepository.countByStatusInAndPaymentLastUpdateAtBetween(PaymentStatus.values().toList(), from, to),
            processing = paymentRepository.countByStatusInAndPaymentLastUpdateAtBetween(PaymentStatus.processingStatuses(), from, to),
            failed = paymentRepository.countByStatusInAndPaymentLastUpdateAtBetween(PaymentStatus.failedStatues(), from, to),
            successfully = paymentRepository.countByStatusInAndPaymentLastUpdateAtBetween(PaymentStatus.successStatues(), from, to),
            suspended = 0
        )
    }

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
