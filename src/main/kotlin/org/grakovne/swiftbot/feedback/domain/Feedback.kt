package org.grakovne.swiftbot.feedback.domain

import java.time.Instant
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class Feedback(
    @Id
    val id: UUID,
    val userReferenceId: String,
    val text: String,
    val createdAt: Instant
)