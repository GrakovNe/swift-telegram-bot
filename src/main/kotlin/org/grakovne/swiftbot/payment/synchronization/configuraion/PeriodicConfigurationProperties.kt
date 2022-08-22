package org.grakovne.swiftbot.payment.synchronization.configuraion

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import kotlin.properties.Delegates


@Configuration
@ConfigurationProperties(prefix = "periodic")
class PeriodicConfigurationProperties {

    var paymentCacheTtlMinutes: Long by Delegates.notNull()
}