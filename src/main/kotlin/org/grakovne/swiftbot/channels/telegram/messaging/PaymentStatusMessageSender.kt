package org.grakovne.swiftbot.channels.telegram.messaging

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.sequence
import arrow.core.tail
import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.Update
import org.grakovne.swiftbot.channels.telegram.TelegramUpdateProcessingError
import org.grakovne.swiftbot.dto.PaymentStatus
import org.grakovne.swiftbot.localization.*
import org.grakovne.swiftbot.user.domain.UserReference
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class PaymentStatusMessageSender(
    bot: TelegramBot,
    private val localizationService: MessageLocalizationService
) : MessageSender(bot) {

    fun sendResponse(
        origin: Update,
        userReference: UserReference,
        paymentInfo: PaymentInfo
    ): Either<TelegramUpdateProcessingError, Unit> {
        val targetLanguage = userReference.provideLanguage()

        val message: Either<LocalizationError, Message> = when (paymentInfo.history.tail().isEmpty()) {
            true -> paymentInfo.toPaymentStatusMessage().let { Either.Right(it) }
            false -> paymentInfo.toPaymentStatusWithHistoryMessage(targetLanguage, localizationService)
        }

        return message
            .flatMap { localizationService.localize(it, targetLanguage) }
            .mapLeft { TelegramUpdateProcessingError.INTERNAL_ERROR }
            .flatMap { sendRawMessage(origin, it) }

    }
}

private fun PaymentInfo.toPaymentStatusWithHistoryMessage(
    language: Language,
    localizationService: MessageLocalizationService
) = this
    .history
    .tail()
    .map { it.toHistoryItem() }
    .map { localizationService.localize(it, language) }
    .sequence()
    .map { it.joinToString("\n\n") }
    .map {
        PaymentStatusWithHistoryMessage(
            paymentId = this.paymentId,
            status = this.status,
            lastUpdateTimestamp = this.lastUpdateTimestamp,
            history = it
        )
    }

private fun Pair<PaymentStatus, Instant>.toHistoryItem(): PaymentStatusHistoryItem {
    return PaymentStatusHistoryItem(
        status = this.first,
        timestamp = this.second
    )
}

private fun PaymentInfo.toPaymentStatusMessage(): PaymentStatusMessage {
    return PaymentStatusMessage(
        paymentId = this.paymentId,
        status = this.status,
        lastUpdateTimestamp = this.lastUpdateTimestamp,
    )
}

data class PaymentInfo(
    val paymentId: UUID,
    val status: PaymentStatus,
    val lastUpdateTimestamp: Instant,
    val history: List<Pair<PaymentStatus, Instant>>
)