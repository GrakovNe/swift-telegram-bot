package org.grakovne.swiftbot.channels.telegram.command

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.Update
import org.grakovne.swiftbot.channels.telegram.messaging.SimpleMessageSender
import org.grakovne.swiftbot.events.core.EventSender
import org.grakovne.swiftbot.events.internal.LogLevel
import org.grakovne.swiftbot.events.internal.LoggingEvent
import org.grakovne.swiftbot.localization.BotMetrics
import org.grakovne.swiftbot.payment.cache.PaymentCacheService
import org.grakovne.swiftbot.user.domain.UserReference
import org.springframework.stereotype.Service

@Service
class CheckMetricsCommand(
    private val cacheService: PaymentCacheService,
    private val simpleMessageSender: SimpleMessageSender,
    private val eventSender: EventSender
) : TelegramOnMessageCommand {
    override fun getKey(): String = "metrics"

    override fun getHelp(): String = "Shows statistics on tracked payments"

    override fun accept(
        bot: TelegramBot,
        update: Update,
        user: UserReference
    ) = simpleMessageSender
        .sendResponse(
            update,
            user,
            BotMetrics(
                countProcessing = cacheService.countProcessing(),
                countFailed = cacheService.countFailed(),
                countSuccessfully = cacheService.countSuccessful(),
                countTotal = cacheService.countTotal()
            )
        ).tap { eventSender.sendEvent(LoggingEvent(LogLevel.DEBUG, "Metrics was requested")) }
}