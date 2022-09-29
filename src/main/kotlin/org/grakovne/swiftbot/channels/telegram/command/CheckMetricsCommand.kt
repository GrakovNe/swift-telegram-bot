package org.grakovne.swiftbot.channels.telegram.command

import arrow.core.Either
import com.pengrad.telegrambot.model.Update
import org.grakovne.swiftbot.channels.telegram.TelegramUpdateProcessingError
import org.grakovne.swiftbot.channels.telegram.messaging.SimpleMessageSender
import org.grakovne.swiftbot.dto.CommandType
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

    override fun getType() = CommandType.METRICS

    override fun accept(
        update: Update,
        user: UserReference
    ): Either<TelegramUpdateProcessingError, Unit> {

        val total = cacheService.countTotal()
        val lastWeek = cacheService.countLastWeek()


        return simpleMessageSender
            .sendResponse(
                update,
                user,
                BotMetrics(
                    totalProcessing = total.processing,
                    totalFailed = total.failed,
                    totalSuccessfully = total.successfully,
                    totalCount = total.total,
                    lastWeekProcessing = lastWeek.processing,
                    lastWeekFailed = lastWeek.failed,
                    lastWeekSuccessfully = lastWeek.successfully,
                    lastWeekCount = lastWeek.total
                )
            ).tap { eventSender.sendEvent(LoggingEvent(LogLevel.DEBUG, "Metrics was requested")) }
    }
}