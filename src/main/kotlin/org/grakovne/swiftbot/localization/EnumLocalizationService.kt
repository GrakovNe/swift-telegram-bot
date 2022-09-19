package org.grakovne.swiftbot.localization

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import org.springframework.util.ResourceUtils
import java.io.File
import java.io.FileNotFoundException

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
        val content = getLocalizationResource(language).path
            .let { File(it) }
            .readBytes()

        return objectMapper.readValue(content, object : TypeReference<List<EnumTemplate>>() {})
    }

    private fun getLocalizationResource(language: Language): File {
        return try {
            ResourceUtils.getFile("classpath:enums_${language.code}.json")
        } catch (ex: FileNotFoundException) {
            ResourceUtils.getFile("classpath:enums.json")
        }
    }
}