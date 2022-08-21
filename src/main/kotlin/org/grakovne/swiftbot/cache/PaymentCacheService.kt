package org.grakovne.swiftbot.cache

import arrow.core.Either
import org.grakovne.swiftbot.cache.domain.Payment
import org.grakovne.swiftbot.cache.repository.PaymentRepository
import org.grakovne.swiftbot.dto.PaymentStatus
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.util.*

@Service
class PaymentCacheService(private val paymentRepository: PaymentRepository) {

    fun fetchCached(id: UUID): Either<CacheError, PaymentStatus> = paymentRepository
        .findById(id)
        .orElseGet { null }
        ?.toStatus()
        ?.let { Either.Right(it) }
        ?: Either.Left(NotFound(id))

    fun storePayment(dto: PaymentStatus) = dto.toPayment().let { paymentRepository.save(it) }
}

private fun PaymentStatus.toPayment(): Payment {
    return Payment(
        id = this.id,
        status = this.status,
        paymentLastUpdateAt = this.lastUpdateTimestamp,
        lastModifiedAt = Instant.now()
    )
}

private fun Payment.toStatus() = PaymentStatus(
    id = this.id,
    status = this.status,
    lastUpdateTimestamp = this.paymentLastUpdateAt
)
