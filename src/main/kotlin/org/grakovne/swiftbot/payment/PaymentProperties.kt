package org.grakovne.swiftbot.payment

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import kotlin.properties.Delegates

@Configuration
@ConfigurationProperties(prefix = "payment")
class PaymentProperties {
    var daysBeforeSuspended: Long by Delegates.notNull()
}