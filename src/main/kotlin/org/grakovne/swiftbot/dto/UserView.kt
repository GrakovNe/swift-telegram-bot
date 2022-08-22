package org.grakovne.swiftbot.dto

import org.grakovne.swiftbot.user.domain.UserReferenceSource
import java.util.*

data class UserView(
    val id: String,
    val channel: UserReferenceSource,
    val subscribedPayments: Set<UUID>
)