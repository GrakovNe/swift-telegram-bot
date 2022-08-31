package org.grakovne.swiftbot.channels.telegram.command

import arrow.core.Either
import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.SendMessage
import org.grakovne.swiftbot.channels.telegram.TelegramUpdateProcessingError
import org.grakovne.swiftbot.events.core.EventSender
import org.grakovne.swiftbot.events.internal.LogLevel
import org.grakovne.swiftbot.events.internal.LoggingEvent
import org.grakovne.swiftbot.payment.cache.PaymentCacheService
import org.springframework.stereotype.Service

@Service
class CheckMetricsCommand(
    private val cacheService: PaymentCacheService,
    private val eventSender: EventSender
) : TelegramOnMessageCommand {
    override fun getKey(): String = "metrics"

    override fun getHelp(): String = "Shows statistics on tracked payments"

    override fun accept(bot: TelegramBot, update: Update): Either<TelegramUpdateProcessingError, Unit> {
        val message = """
            Tracked payments info
            
            Successfully credited payments: <b>${cacheService.countSuccessful()}</b>
            Payments in process: <b>${cacheService.countProcessing()}</b>
            Failed or rejected payments: <b>${cacheService.countFailed()}</b>
            
            Total tracked payments: <b>${cacheService.countTotal()}</b>
        """.trimIndent()

        val isMessageSent = bot
            .execute(SendMessage(update.message().chat().id(), message).parseMode(ParseMode.HTML))
            .isOk

        return when (isMessageSent) {
            true -> {
                eventSender.sendEvent(LoggingEvent(LogLevel.DEBUG, "Metrics was requested"))
                Either.Right(Unit)
            }
            false -> Either.Left(TelegramUpdateProcessingError.RESPONSE_NOT_SENT)
        }
    }
}