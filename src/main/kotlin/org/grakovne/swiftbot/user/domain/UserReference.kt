package org.grakovne.swiftbot.user.domain

import java.util.*
import javax.persistence.*

@Entity
data class UserReference(
    @Id
    val id: String,
    @Enumerated(EnumType.STRING)
    val source: UserReferenceSource,
    val language: String?,
    @ElementCollection(fetch = FetchType.EAGER)
    val subscribedPayments: Set<UUID>,
    @Enumerated(EnumType.STRING)
    val type: Type
)

enum class UserReferenceSource {
    TELEGRAM,
    REST
}

enum class Type {
    FREE_USER,
    PAID_USER,
    SUPER_USER;
}