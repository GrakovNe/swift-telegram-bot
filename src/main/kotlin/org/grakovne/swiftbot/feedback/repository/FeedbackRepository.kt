package org.grakovne.swiftbot.feedback.repository

import org.grakovne.swiftbot.feedback.domain.Feedback
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface FeedbackRepository : JpaRepository<Feedback, UUID>