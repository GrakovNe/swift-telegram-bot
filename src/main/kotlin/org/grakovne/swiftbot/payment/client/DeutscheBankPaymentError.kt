package org.grakovne.swiftbot.payment.client

import java.util.*

sealed interface DeutscheBankPaymentError

data class PaymentNotFound(val paymentId: UUID): DeutscheBankPaymentError
data class UnknownError(val response: Any?, val paymentId: UUID): DeutscheBankPaymentError
data class InconsistencyError(val status: Int, val paymentId: UUID): DeutscheBankPaymentError