package org.grakovne.swiftbot.events.core

interface EventListener {
    fun acceptableEvents(): List<EventType>

    fun onEvent(event: Event)
}