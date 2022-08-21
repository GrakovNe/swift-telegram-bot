package org.grakovne.swiftbot.synchronization

import org.grakovne.swiftbot.cache.PaymentCacheService
import org.grakovne.swiftbot.synchronization.payment.PaymentService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant

@Service
class PaymentStatusUpdatePeriodicService(
    private val paymentCacheService: PaymentCacheService,
    private val paymentService: PaymentService
) {

    @Scheduled(fixedDelay = 1000)
    fun updateOldestPaymentStatus() {
        paymentCacheService
            .fetchOldestCached()
            ?.takeIf { it.lastModifiedAt.isBefore(Instant.now().minus(Duration.ofHours(1))) }
            ?.id
            ?.let { paymentService.updateAndCache(it) }
    }
}