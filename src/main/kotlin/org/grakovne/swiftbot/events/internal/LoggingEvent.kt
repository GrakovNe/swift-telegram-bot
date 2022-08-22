package org.grakovne.swiftbot.events.internal

import org.grakovne.swiftbot.events.core.Event
import org.grakovne.swiftbot.events.core.EventType

data class LoggingEvent(val level: LogLevel, val message: String) : Event(EventType.LOG_SENT)

enum class LogLevel(private val severity: Int) {
    DEBUG(0),
    INFO(1),
    WARN(2);

    companion object {
        fun LogLevel.isWorseOrEqualThan(other: LogLevel): Boolean = severity >= other.severity
    }
}