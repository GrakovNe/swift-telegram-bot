package org.grakovne.swiftbot.user

import org.grakovne.swiftbot.user.domain.Type
import org.grakovne.swiftbot.user.domain.UserReference
import org.grakovne.swiftbot.user.domain.UserReferenceSource
import org.grakovne.swiftbot.user.repository.UserReferenceRepository
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class UserReferenceService(private val userReferenceRepository: UserReferenceRepository) {

    fun fetchSuperUsers() = userReferenceRepository.findByType(Type.SUPER_USER)

    fun fetchUsersWithSubscription(paymentId: UUID, source: UserReferenceSource): List<UserReference> =
        userReferenceRepository
            .findBySubscribedPaymentsAndSource(paymentId, source)

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
            .orElseGet { persistUser(userId, setOf(), source, language, Type.FREE_USER) }
            .copy(language = language)
            .let { persistUser(it.id, it.subscribedPayments, it.source, it.language ?: "en", it.type) }


    private fun persistUser(
        id: String,
        subscribedPayments: Set<UUID>,
        source: UserReferenceSource,
        language: String,
        type: Type
    ): UserReference = UserReference(
        id = id,
        subscribedPayments = subscribedPayments,
        source = source,
        language = language,
        type = type,
        lastActivityTimestamp = Instant.now()
    ).let { userReferenceRepository.save(it) }
}