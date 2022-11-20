package org.grakovne.swiftbot.channels.telegram

enum class TelegramUpdateProcessingError {
    EXCEPTION_RESPONSE_SENT,
    RESPONSE_NOT_SENT,
    INVALID_REQUEST,
    INTERNAL_ERROR
}