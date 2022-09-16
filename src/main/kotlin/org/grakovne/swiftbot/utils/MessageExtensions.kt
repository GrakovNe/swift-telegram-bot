package org.grakovne.swiftbot.utils

import com.pengrad.telegrambot.model.Update
import java.util.*
import java.util.regex.Pattern

fun Update.findPaymentId(): UUID? {
    val matcher = uetrPattern.matcher(this.message().text())
    return when {
        matcher.find() -> UUID.fromString(matcher.group(0))
        else -> null
    }

}

private val uetrPattern = Pattern.compile("[a-f0-9]{8}(?:-[a-f0-9]{4}){4}[a-f0-9]{8}")