package org.grakovne.swiftbot.dto

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue
import com.fasterxml.jackson.annotation.JsonFormat

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
    }
}