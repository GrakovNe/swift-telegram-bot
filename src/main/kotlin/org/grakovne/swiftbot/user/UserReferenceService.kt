package org.grakovne.swiftbot.user

import org.grakovne.swiftbot.dto.UserView
import org.grakovne.swiftbot.user.domain.UserReference
import org.grakovne.swiftbot.user.domain.UserReferenceSource
import org.grakovne.swiftbot.user.repository.UserReferenceRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserReferenceService(private val userReferenceRepository: UserReferenceRepository) {

    fun fetchUserSubscription(userId: String): List<UUID> = userReferenceRepository
        .findById(userId)
        .map { it.subscribedPayments }
        .orElseGet { emptySet() }
        .toList()

    fun subscribeToPayment(userId: String, paymentId: UUID, source: UserReferenceSource) =
        userReferenceRepository
            .findById(userId)
            .orElseGet { createUser(userId, setOf(paymentId), source) }
            .let { it.copy(subscribedPayments = it.subscribedPayments + paymentId) }
            .let { userReferenceRepository.save(it) }


    fun updateUser(request: UserView) = request.toUser().let { userReferenceRepository.save(it) }

    fun createUser(
        id: String,
        subscribedPayments: Set<UUID>,
        source: UserReferenceSource,
    ): UserReference = UserReference(
        id = id,
        subscribedPayments = subscribedPayments,
        source = source
    ).let { userReferenceRepository.save(it) }
}

private fun UserView.toUser(): UserReference = UserReference(
    id = this.id,
    source = this.channel,
    subscribedPayments = this.subscribedPayments
)
