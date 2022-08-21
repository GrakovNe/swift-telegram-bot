package org.grakovne.swiftbot.payment.synchronization.payment

import arrow.core.Either
import org.grakovne.swiftbot.dto.PaymentView
import org.grakovne.swiftbot.payment.synchronization.PaymentError
import java.util.UUID

interface PaymentSynchronizationProvider {

    fun fetchStatus(id: UUID): Either<PaymentError, PaymentView>
}