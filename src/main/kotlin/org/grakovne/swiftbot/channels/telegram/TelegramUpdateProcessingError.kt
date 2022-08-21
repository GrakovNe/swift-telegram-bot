package org.grakovne.swiftbot.channels.telegram

enum class TelegramUpdateProcessingError {
    RESPONSE_NOT_SENT,
    INVALID_REQUEST,
    INTERNAL_ERROR
}