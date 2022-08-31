package org.grakovne.swiftbot.dto

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue

enum class PaymentStatus(val value: String) {
    CREDITED("ACCC"),
    PROCESSING("ACSP"),
    SENT_TO_GPI_BANK("ACSP/G000"),
    SENT_TO_NON_GPI_BANK("ACSP/G001"),
    PENDING_CREDIT("ACSP/G002"),
    CREDITED_FOR_ACCOUNT_CLARIFICATION("ACSP/G003"),
    COVERAGE_WAITING("ACSP/G004"),
    REJECTED("RJCT"),

    @JsonEnumDefaultValue
    UNEXPECTED("unexpected");

    companion object {
        fun fromString(value: String): PaymentStatus? = values().find { it.value == value }
        fun successStatues(): List<PaymentStatus> = listOf(CREDITED)
        fun failedStatues(): List<PaymentStatus> = listOf(REJECTED)
        fun processingStatuses(): List<PaymentStatus> = listOf(
            PROCESSING,
            SENT_TO_GPI_BANK,
            SENT_TO_NON_GPI_BANK,
            PENDING_CREDIT,
            CREDITED_FOR_ACCOUNT_CLARIFICATION,
            COVERAGE_WAITING
        )
    }
}