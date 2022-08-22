package org.grakovne.swiftbot.payment.cache

import java.util.*

sealed interface CacheError

data class NotFound(val id: UUID) : CacheError