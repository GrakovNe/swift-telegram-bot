package org.grakovne.swiftbot.channels.telegram

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.TelegramBot.Builder
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class Configuration {

    @Bean
    fun telegramBotService(): TelegramBot = Builder("5541027338:AAHJoufyp82lT5AAh0Yu2SQyk43hfW-7dZ8").build()
}