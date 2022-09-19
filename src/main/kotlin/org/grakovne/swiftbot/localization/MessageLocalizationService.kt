package org.grakovne.swiftbot.localization

import arrow.core.Either
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.commons.text.StringSubstitutor
import org.grakovne.swiftbot.common.converter.toMessage
import org.springframework.stereotype.Service
import org.springframework.util.ResourceUtils
import java.io.File
import java.io.FileNotFoundException
import java.time.Instant
import kotlin.reflect.full.memberProperties


@Service
class MessageLocalizationService(
    val objectMapper: ObjectMapper,
    val enumLocalizationService: EnumLocalizationService
) {
    fun localize(message: Message, language: Language): Either<LocalizationError, String> {
        val messageTemplate = findLocalizationResources(language)
            .find { it.name == message.templateName }
            ?: return Either.Left(LocalizationError.TEMPLATE_NOT_FOUND)

        val values = message::class
            .memberProperties
            .mapNotNull { member -> message.getField(member.name, language)?.let { member.name to it } }
            .toMap()

        return StringSubstitutor(values)
            .replace(messageTemplate.template)
            .let { Either.Right(it) }
    }

    private fun findLocalizationResources(language: Language): List<MessageTemplate> {
        val content = getLocalizationResource(language).path
            .let { File(it) }
            .readBytes()

        return objectMapper.readValue(content, object : TypeReference<List<MessageTemplate>>() {})
    }

    private fun getLocalizationResource(language: Language): File {
        return try {
            ResourceUtils.getFile("classpath:messages_${language.code}.json")
        } catch (ex: FileNotFoundException) {
            ResourceUtils.getFile("classpath:messages.json")
        }
    }

    private fun Any.getField(fieldName: String, language: Language): String? {
        this::class.memberProperties.forEach { kCallable ->
            if (fieldName == kCallable.name) {
                val rawValue = kCallable.getter.call(this)

                return when (rawValue) {
                    is Enum<*> -> enumLocalizationService.localize(rawValue, language)
                    is Instant -> rawValue.toMessage()
                    else -> rawValue.toString()
                }
            }
        }

        return null
    }
}

enum class Language(val code: String) {
    ENGLISH("en"),
    RUSSIAN("ru")
}