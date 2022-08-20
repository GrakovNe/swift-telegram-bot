package org.grakovne.swiftbot.synchronization

sealed interface SynchronizationError

data class CommonSynchronizationError(val message: String?) : SynchronizationError