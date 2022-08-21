package org.grakovne.swiftbot.payment.synchronization.payment

import arrow.core.Either
import org.grakovne.swiftbot.payment.client.DeutscheBankPaymentClient
import org.grakovne.swiftbot.dto.PaymentView
import org.grakovne.swiftbot.payment.synchronization.CommonSynchronizationError
import org.grakovne.swiftbot.payment.synchronization.PaymentError
import org.springframework.stereotype.Service
import java.util.*

@Service
class DeutscheBankPaymentSynchronizationService(private val client: DeutscheBankPaymentClient) :
    PaymentSynchronizationProvider {

    override fun fetchStatus(id: UUID): Either<PaymentError, PaymentView> = client
        .fetchPaymentStatus(id)
        .mapLeft { CommonSynchronizationError(it) }
}