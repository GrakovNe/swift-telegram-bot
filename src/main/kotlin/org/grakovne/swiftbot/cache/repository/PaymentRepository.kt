package org.grakovne.swiftbot.cache.repository

import org.grakovne.swiftbot.cache.domain.Payment
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface PaymentRepository: JpaRepository<Payment, UUID> {

    fun findTopByOrderByLastModifiedAtDesc(): Payment?
}