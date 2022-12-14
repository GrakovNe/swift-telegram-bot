package org.grakovne.swiftbot.localization

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import java.io.FileNotFoundException
import java.io.InputStream

@Service
class EnumLocalizationService(val objectMapper: ObjectMapper) {

    fun localize(enum: Enum<*>, language: Language) = findLocalizationResources(language)
        .find { it.name == enum::class.simpleName }
        ?.values
        ?.toList()
        ?.find { (value, _) -> value == enum.name }
        ?.second
        ?: enum.name

    private fun findLocalizationResources(language: Language): List<EnumTemplate> {
        val content = getLocalizationResource(language)
            .readBytes()

        return objectMapper.readValue(content, object : TypeReference<List<EnumTemplate>>() {})
    }

    private fun getLocalizationResource(language: Language): InputStream {
        return try {
            ClassPathResource("enums_${language.code}.json").inputStream
        } catch (ex: FileNotFoundException) {
            ClassPathResource("enums.json").inputStream
        }
    }
}