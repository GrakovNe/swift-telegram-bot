package org.grakovne.swiftbot.payment.client

import arrow.core.Either
import org.grakovne.swiftbot.dto.PaymentStatus
import org.grakovne.swiftbot.dto.PaymentView
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.client.RestTemplate
import java.time.Instant
import java.util.*

@Service
class DeutscheBankPaymentClient(private val restTemplate: RestTemplate) {

    fun fetchPaymentStatus(id: UUID) = try {
        fetchResponse(id)
    } catch (ex: HttpStatusCodeException) {
        Either.Left(UnknownError(ex.statusText, id))
    }

    private fun fetchResponse(id: UUID): Either<DeutscheBankPaymentError, PaymentView> = restTemplate
        .getForEntity("$url=$id", DeutscheBankPaymentStatus::class.java)
        .let {
            when (it.statusCode) {
                HttpStatus.OK -> it.body
                    ?.asCommon()
                    ?.let { status -> Either.Right(status) }
                    ?: Either.Left(InconsistencyError(it.statusCodeValue, id))
                HttpStatus.NO_CONTENT -> Either.Left(PaymentNotFound(id))
                else -> Either.Left(UnknownError(it, id))
            }
        }

    companion object {
        private const val url = "https://corporateapi.db.com/payments/data/gpi/v1/gpiTransactionInfo?uetr"
    }
}

data class DeutscheBankPaymentStatus(
    val uetr: UUID,
    val status: String,
    val valueDate: Instant?,
    val lastUpdate: Instant
)

fun DeutscheBankPaymentStatus.asCommon() = PaymentView(
    id = this.uetr,
    status = this.status.let { PaymentStatus.fromString(it) ?: PaymentStatus.UNEXPECTED },
    lastUpdateTimestamp = this.lastUpdate
)