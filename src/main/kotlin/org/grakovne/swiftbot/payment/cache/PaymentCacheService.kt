package org.grakovne.swiftbot.payment.cache

import arrow.core.Either
import org.grakovne.swiftbot.dto.PaymentView
import org.grakovne.swiftbot.payment.cache.domain.Payment
import org.grakovne.swiftbot.payment.cache.repository.PaymentRepository
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class PaymentCacheService(private val paymentRepository: PaymentRepository) {

    fun fetchOldestCached() = paymentRepository.findTopByOrderByLastModifiedAtAsc()

    fun fetchCached(id: UUID): Either<CacheError, PaymentView> = paymentRepository
        .findById(id)
        .orElseGet { null }
        ?.toView()
        ?.let { Either.Right(it) }
        ?: Either.Left(NotFound(id))

    fun storePayment(dto: PaymentView) = dto.toPayment().let { paymentRepository.save(it) }

    fun updateLastModifiedAt(id: UUID, now: Instant) = paymentRepository
        .findById(id)
        .orElseGet { null }
        ?.copy(lastModifiedAt = now)
        ?.let { paymentRepository.save(it) }
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
