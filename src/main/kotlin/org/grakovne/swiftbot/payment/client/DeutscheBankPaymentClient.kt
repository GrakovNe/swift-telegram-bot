package org.grakovne.swiftbot.payment.client

import arrow.core.Either
import arrow.core.flatMap
import org.grakovne.swiftbot.dto.PaymentView
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.client.RestTemplate
import java.time.Instant
import java.util.*

@Service
class DeutscheBankPaymentClient(private val restTemplate: RestTemplate) {

    fun fetchPaymentStatus(id: UUID): Either<String, PaymentView> {
        val response = try {
            fetchResponse(id)
        } catch (ex: HttpStatusCodeException) {
            Either.Left(ex.statusText)
        }

        return response
            .flatMap {
                it.body
                    ?.asCommon()
                    ?.let { status -> Either.Right(status) }
                    ?: Either.Left("Unable to find payment status")
            }
            .mapLeft { "Third-party service responded with error status: $it" }
    }

    private fun fetchResponse(id: UUID) = restTemplate
        .getForEntity("$url=$id", DeutscheBankPaymentStatus::class.java)
        .let { Either.Right(it) }

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
    status = this.status,
    lastUpdateTimestamp = this.lastUpdate
)