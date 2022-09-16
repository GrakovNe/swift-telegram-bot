package org.grakovne.swiftbot.user

import org.grakovne.swiftbot.user.domain.UserReference
import org.grakovne.swiftbot.user.domain.UserReferenceSource
import org.grakovne.swiftbot.user.repository.UserReferenceRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserReferenceService(private val userReferenceRepository: UserReferenceRepository) {

    fun fetchUsersWithSubscription(paymentId: UUID, source: UserReferenceSource): List<String> =
        userReferenceRepository
            .findBySubscribedPaymentsAndSource(paymentId, source)
            .map { it.id }

    fun fetchUserSubscription(userId: String): List<UUID> = userReferenceRepository
        .findById(userId)
        .map { it.subscribedPayments }
        .orElseGet { emptySet() }
        .toList()

    fun unsubscribeFromPayment(user: UserReference, paymentId: UUID) =
        user
            .let { it.copy(subscribedPayments = it.subscribedPayments - paymentId) }
            .let { userReferenceRepository.save(it) }

    fun subscribeToPayment(user: UserReference, paymentId: UUID) =
        user
            .let { it.copy(subscribedPayments = it.subscribedPayments + paymentId) }
            .let { userReferenceRepository.save(it) }

    fun fetchUser(userId: String, source: UserReferenceSource, language: String): UserReference =
        userReferenceRepository
            .findById(userId)
            .orElseGet { createUser(userId, setOf(), source, language) }
            .copy(language = language)
            .let { createUser(it.id, it.subscribedPayments, it.source, it.language ?: "en") }


    private fun createUser(
        id: String,
        subscribedPayments: Set<UUID>,
        source: UserReferenceSource,
        language: String
    ): UserReference = UserReference(
        id = id,
        subscribedPayments = subscribedPayments,
        source = source,
        language = language
    ).let { userReferenceRepository.save(it) }
}