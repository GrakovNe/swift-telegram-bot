package org.grakovne.swiftbot.synchronization

import arrow.core.Either
import org.grakovne.swiftbot.client.DeutscheBankPaymentClient
import org.grakovne.swiftbot.dto.PaymentStatus
import org.springframework.stereotype.Service
import java.util.*

@Service
class DeutscheBankPaymentSynchronizationService(private val client: DeutscheBankPaymentClient) :
    PaymentSynchronizationService {

    override fun fetchStatus(id: UUID): Either<SynchronizationError, PaymentStatus> = client
        .fetchPaymentStatus(id)
        .mapLeft { CommonSynchronizationError(it) }
}