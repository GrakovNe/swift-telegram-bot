package org.grakovne.swiftbot.feedback

import org.grakovne.swiftbot.feedback.domain.Feedback
import org.grakovne.swiftbot.feedback.repository.FeedbackRepository
import org.grakovne.swiftbot.user.domain.UserReference
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class FeedbackService(private val feedbackRepository: FeedbackRepository) {

    fun reportFeedback(userReference: UserReference, text: String) = feedbackRepository.save(
        Feedback(
            id = UUID.randomUUID(),
            userReferenceId = userReference.id,
            text = text,
            createdAt = Instant.now()
        )
    )
}