package org.grakovne.swiftbot.payment.synchronization

import java.util.*

sealed interface PaymentError

data class PaymentNotFound(val paymentId: UUID) : PaymentError
data class UnknownError(val response: Any?, val paymentId: UUID) : PaymentError