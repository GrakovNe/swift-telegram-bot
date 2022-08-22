package org.grakovne.swiftbot.payment.cache.repository

import org.grakovne.swiftbot.payment.cache.domain.Payment
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface PaymentRepository : JpaRepository<Payment, UUID> {

    fun findTopByOrderByLastModifiedAtDesc(): Payment?
}