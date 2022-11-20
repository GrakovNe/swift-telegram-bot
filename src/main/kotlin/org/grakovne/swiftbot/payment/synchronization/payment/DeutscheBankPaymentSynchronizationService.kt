package org.grakovne.swiftbot.payment.synchronization.payment

import arrow.core.Either
import org.grakovne.swiftbot.dto.PaymentView
import org.grakovne.swiftbot.payment.client.DeutscheBankPaymentClient
import org.grakovne.swiftbot.payment.synchronization.PaymentError
import org.grakovne.swiftbot.payment.synchronization.PaymentNotFound
import org.grakovne.swiftbot.payment.synchronization.UnknownError
import org.springframework.stereotype.Service
import java.util.*
import org.grakovne.swiftbot.payment.client.InconsistencyError as ClientInconsistencyError
import org.grakovne.swiftbot.payment.client.PaymentNotFound as ClientPaymentNotFound
import org.grakovne.swiftbot.payment.client.UnknownError as ClientUnknownError

@Service
class DeutscheBankPaymentSynchronizationService(private val client: DeutscheBankPaymentClient) :
    PaymentSynchronizationProvider {

    override fun fetchStatus(id: UUID): Either<PaymentError, PaymentView> = client
        .fetchPaymentStatus(id)
        .mapLeft {
            when (it) {
                is ClientInconsistencyError -> UnknownError(it.toString(), it.paymentId)
                is ClientPaymentNotFound -> PaymentNotFound(it.paymentId)
                is ClientUnknownError -> UnknownError(it.response, it.paymentId)
            }
        }
}