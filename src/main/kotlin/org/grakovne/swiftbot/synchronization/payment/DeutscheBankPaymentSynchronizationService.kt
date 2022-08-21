package org.grakovne.swiftbot.synchronization.payment

import arrow.core.Either
import org.grakovne.swiftbot.client.DeutscheBankPaymentClient
import org.grakovne.swiftbot.dto.PaymentStatus
import org.grakovne.swiftbot.synchronization.CommonSynchronizationError
import org.grakovne.swiftbot.synchronization.PaymentError
import org.springframework.stereotype.Service
import java.util.*

@Service
class DeutscheBankPaymentSynchronizationService(private val client: DeutscheBankPaymentClient) :
    PaymentSynchronizationProvider {

    override fun fetchStatus(id: UUID): Either<PaymentError, PaymentStatus> = client
        .fetchPaymentStatus(id)
        .mapLeft { CommonSynchronizationError(it) }
}