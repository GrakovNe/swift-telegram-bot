package org.grakovne.swiftbot.payment.synchronization.payment

import arrow.core.Either
import arrow.core.sequence
import org.grakovne.swiftbot.dto.PaymentStatus
import org.grakovne.swiftbot.dto.PaymentView
import org.grakovne.swiftbot.payment.cache.PaymentCacheService
import org.grakovne.swiftbot.payment.synchronization.PaymentError
import org.springframework.stereotype.Service
import java.util.*

@Service
class PaymentService(
    private val dataServices: List<PaymentSynchronizationProvider>,
    private val cacheService: PaymentCacheService
) {

    fun fetchPaymentStatus(id: UUID): Either<PaymentError, PaymentView> = cacheService
        .fetchCached(id)
        .fold(
            ifLeft = { updateAndCache(id) },
            ifRight = { Either.Right(it) }
        )

    fun updateAndCache(id: UUID) = retrievePayment(id).tap { cacheService.storePayment(it) }

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