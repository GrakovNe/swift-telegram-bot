package org.grakovne.swiftbot.localization

data class MessageTemplate(
    val name: String,
    val type: MessageType,
    val template: String
)

enum class MessageType {
    PLAIN,
    HTML
}