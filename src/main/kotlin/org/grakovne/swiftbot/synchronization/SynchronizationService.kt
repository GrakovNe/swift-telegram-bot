package org.grakovne.swiftbot.synchronization

import arrow.core.Either
import arrow.core.sequence
import org.grakovne.swiftbot.cache.PaymentCacheService
import org.grakovne.swiftbot.dto.PaymentStatus
import org.grakovne.swiftbot.synchronization.payment.PaymentSynchronizationProvider
import org.springframework.stereotype.Service
import java.util.*

@Service
class SynchronizationService(
    private val dataServices: List<PaymentSynchronizationProvider>,
    private val cacheService: PaymentCacheService
) {

    fun fetchPaymentStatus(id: UUID): Either<SynchronizationError, PaymentStatus> = cacheService
        .fetchCached(id)
        .fold(
            ifLeft = { retrieveAndCache(id) },
            ifRight = { Either.Right(it) }
        )


    private fun retrieveAndCache(id: UUID): Either<SynchronizationError, PaymentStatus> =
        retrievePayment(id).tap { cacheService.storePayment(it) }

    private fun retrievePayment(id: UUID): Either<SynchronizationError, PaymentStatus> = dataServices.map { provider ->
        when (provider.fetchStatus(id)) {
            is Either.Left -> provider.fetchStatus(id)
            is Either.Right -> return provider.fetchStatus(id)
        }
    }
        .sequence()
        .map { it.first() }
}