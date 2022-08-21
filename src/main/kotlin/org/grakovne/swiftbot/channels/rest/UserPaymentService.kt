package org.grakovne.swiftbot.channels.rest

import arrow.core.Either
import org.grakovne.swiftbot.dto.PaymentStatusRequest
import org.grakovne.swiftbot.dto.PaymentView
import org.grakovne.swiftbot.payment.synchronization.PaymentError
import org.grakovne.swiftbot.payment.synchronization.payment.PaymentService
import org.grakovne.swiftbot.user.UserReferenceService
import org.grakovne.swiftbot.user.domain.UserReferenceSource
import org.springframework.stereotype.Service

@Service
class UserPaymentService(
    private val paymentService: PaymentService,
    private val userReferenceService: UserReferenceService
) {

    fun fetchPaymentStatus(request: PaymentStatusRequest): Either<PaymentError, PaymentView> = paymentService
        .fetchPaymentStatus(request.paymentId)
        .tap {
            userReferenceService.subscribeToPayment(
                request.userReference,
                request.paymentId,
                UserReferenceSource.REST
            )
        }
}