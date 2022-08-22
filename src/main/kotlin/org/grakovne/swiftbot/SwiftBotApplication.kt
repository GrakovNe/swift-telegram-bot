package org.grakovne.swiftbot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling
import java.util.*

@EnableScheduling
@SpringBootApplication
class SwiftBotApplication

fun main(args: Array<String>) {
    TimeZone.setDefault(TimeZone.getTimeZone("GMT"))
    runApplication<SwiftBotApplication>(*args)
}
