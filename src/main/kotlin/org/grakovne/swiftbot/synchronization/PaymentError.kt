package org.grakovne.swiftbot.synchronization

sealed interface PaymentError

data class CommonSynchronizationError(val message: String?) : PaymentError