package org.grakovne.swiftbot.synchronization.payment

import arrow.core.Either
import arrow.core.sequence
import org.grakovne.swiftbot.cache.PaymentCacheService
import org.grakovne.swiftbot.dto.PaymentStatus
import org.grakovne.swiftbot.synchronization.PaymentError
import org.springframework.stereotype.Service
import java.util.*

@Service
class PaymentService(
    private val dataServices: List<PaymentSynchronizationProvider>,
    private val cacheService: PaymentCacheService
) {

    fun fetchPaymentStatus(id: UUID): Either<PaymentError, PaymentStatus> = cacheService
        .fetchCached(id)
        .fold(
            ifLeft = { retrieveAndCache(id) },
            ifRight = { Either.Right(it) }
        )

    private fun retrieveAndCache(id: UUID) = retrievePayment(id).tap { cacheService.storePayment(it) }

    private fun retrievePayment(id: UUID) = dataServices
        .map { provider ->
            when (provider.fetchStatus(id)) {
                is Either.Left -> provider.fetchStatus(id)
                is Either.Right -> return provider.fetchStatus(id)
            }
        }
        .sequence()
        .map { it.first() }
}