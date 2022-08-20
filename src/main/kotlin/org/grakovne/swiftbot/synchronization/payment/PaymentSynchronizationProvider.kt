package org.grakovne.swiftbot.synchronization.payment

import arrow.core.Either
import org.grakovne.swiftbot.dto.PaymentStatus
import org.grakovne.swiftbot.synchronization.SynchronizationError
import java.util.UUID

interface PaymentSynchronizationProvider {

    fun fetchStatus(id: UUID): Either<SynchronizationError, PaymentStatus>
}