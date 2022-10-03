package org.grakovne.swiftbot.user.repository

import org.grakovne.swiftbot.user.domain.Type
import org.grakovne.swiftbot.user.domain.UserReference
import org.grakovne.swiftbot.user.domain.UserReferenceSource
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserReferenceRepository : JpaRepository<UserReference, String> {

    fun findByType(type: Type): List<UserReference>
    fun findBySubscribedPaymentsAndSource(paymentId: UUID, source: UserReferenceSource): List<UserReference>
}