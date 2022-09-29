package org.grakovne.swiftbot.payment.cache.domain

data class PaymentCountReport(
    val total: Long,
    val processing: Long,
    val successfully: Long,
    val failed: Long
)