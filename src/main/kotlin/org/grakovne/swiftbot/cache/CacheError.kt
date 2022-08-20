package org.grakovne.swiftbot.cache

import java.util.UUID

sealed interface CacheError

data class NotFound(val id: UUID): CacheError