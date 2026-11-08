package com.misbaha.app.domain.validation

sealed interface ZikrTextValidation {
    data class Valid(val text: String) : ZikrTextValidation
    data object Empty : ZikrTextValidation
    data object Duplicate : ZikrTextValidation
}

object ZikrTextValidator {
    fun validate(rawText: String, existingTexts: Collection<String>): ZikrTextValidation {
        val text = rawText.trim()
        if (text.isEmpty()) {
            return ZikrTextValidation.Empty
        }
        if (existingTexts.any { it.trim() == text }) {
            return ZikrTextValidation.Duplicate
        }
        return ZikrTextValidation.Valid(text)
    }
}
