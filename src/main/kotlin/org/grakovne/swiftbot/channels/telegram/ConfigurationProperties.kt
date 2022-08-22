package org.grakovne.swiftbot.channels.telegram

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import kotlin.properties.Delegates

@Configuration
@ConfigurationProperties(prefix = "telegram")
class ConfigurationProperties {
    var token: String by Delegates.notNull()
}