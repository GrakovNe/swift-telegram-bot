package org.grakovne.swiftbot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SwiftBotApplication

fun main(args: Array<String>) {
	runApplication<SwiftBotApplication>(*args)
}
