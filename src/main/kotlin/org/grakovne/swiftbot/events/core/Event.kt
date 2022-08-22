package org.grakovne.swiftbot.events.core

abstract class Event(val eventType: EventType)

enum class EventType {
    PAYMENT_CACHE_OUTDATED,
    PAYMENT_STATUS_CHANGED
}


