package org.grakovne.swiftbot.payment.synchronization

sealed interface PaymentError

data class CommonSynchronizationError(val message: String?) : PaymentError