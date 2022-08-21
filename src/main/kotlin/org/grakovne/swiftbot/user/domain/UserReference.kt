package org.grakovne.swiftbot.user.domain

import java.util.*
import javax.persistence.*

@Entity
data class UserReference(
    @Id
    val id: UUID,
    @Enumerated(EnumType.STRING)
    val source: UserReferenceSource,

    @ElementCollection
    val subscribedPayments: Set<UUID>
)

enum class UserReferenceSource {
    TELEGRAM,
    REST
}