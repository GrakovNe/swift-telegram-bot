package org.grakovne.swiftbot.payment.synchronization

import org.grakovne.swiftbot.events.payment.PaymentCacheOutdatedEvent
import org.grakovne.swiftbot.events.core.EventSender
import org.grakovne.swiftbot.payment.cache.PaymentCacheService
import org.grakovne.swiftbot.payment.synchronization.configuraion.PeriodicConfigurationProperties
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant

@Service
class PaymentStatusUpdatePeriodicService(
    private val paymentCacheService: PaymentCacheService,
    private val eventSender: EventSender,
    private val configurationProperties: PeriodicConfigurationProperties
) {

    @Scheduled(fixedDelay = 1000)
    fun checkOldestCachedPayment() {
        paymentCacheService
            .fetchOldestCached()
            ?.takeIf {
                it.lastModifiedAt
                    .isBefore(Instant.now().minus(Duration.ofMinutes(configurationProperties.paymentCacheTtlMinutes)))
            }
            ?.let { eventSender.sendEvent(PaymentCacheOutdatedEvent(it.id, it.status)) }
    }
}