package org.grakovne.swiftbot.events.core

abstract class Event(val eventType: EventType)

enum class EventType {
    PAYMENT_CACHE_OUTDATED,
    PAYMENT_STATUS_CHANGED,
    PAYMENT_LAST_UPDATE_CHANGED,
    LOG_SENT
}


