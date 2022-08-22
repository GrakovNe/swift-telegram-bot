package org.grakovne.swiftbot.channels.telegram.notification

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.SendMessage
import org.grakovne.swiftbot.channels.telegram.ConfigurationProperties
import org.grakovne.swiftbot.events.core.Event
import org.grakovne.swiftbot.events.core.EventListener
import org.grakovne.swiftbot.events.core.EventType
import org.grakovne.swiftbot.events.internal.LoggingEvent
import org.springframework.stereotype.Service

@Service
class TelegramAlertNotificationSender(
    private val bot: TelegramBot,
    private val properties: ConfigurationProperties
) : EventListener {
    override fun acceptableEvents(): List<EventType> = listOf(EventType.LOG_SENT)

    override fun onEvent(event: Event) {
        when (event) {
            is LoggingEvent -> processLoggingEvent(event)
        }
    }

    private fun processLoggingEvent(event: LoggingEvent) =
        bot.execute(SendMessage(properties.adminChat, event.toMessage()).parseMode(ParseMode.HTML))

    private fun LoggingEvent.toMessage(): String = """
    <i>[Admin] Logging Event Occurred!
    
    Status: ${this.level}
    Message: ${this.message}</i>
""".trimIndent()
}
