package org.grakovne.swiftbot.synchronization

import arrow.core.Either
import org.grakovne.swiftbot.dto.PaymentStatus
import java.util.UUID

interface PaymentSynchronizationService {

    fun fetchStatus(id: UUID): Either<SynchronizationError, PaymentStatus>
}