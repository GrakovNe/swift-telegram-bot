package org.grakovne.swiftbot.events.internal

import org.grakovne.swiftbot.events.core.Event
import org.grakovne.swiftbot.events.core.EventType

data class LoggingEvent(val level: LogLevel, val message: String) : Event(EventType.LOG_SENT)

enum class LogLevel {
    DEBUG,
    INFO,
    WARN
}