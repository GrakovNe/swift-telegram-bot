package org.grakovne.swiftbot.payment.synchronization.payment

import arrow.core.Either
import org.grakovne.swiftbot.dto.PaymentView
import org.grakovne.swiftbot.payment.cache.PaymentCacheService
import org.grakovne.swiftbot.payment.synchronization.PaymentError
import org.springframework.stereotype.Service
import java.util.*

@Service
class PaymentService(
    private val dataService: PaymentSynchronizationProvider,
    private val cacheService: PaymentCacheService
) {

    fun fetchPaymentStatus(id: UUID): Either<PaymentError, PaymentView> = cacheService
        .fetchCached(id)
        .fold(
            ifLeft = { updateAndCache(id) },
            ifRight = { Either.Right(it) }
        )

    fun updateAndCache(id: UUID) = dataService
        .fetchStatus(id)
        .tap { cacheService.storePayment(it) }

}