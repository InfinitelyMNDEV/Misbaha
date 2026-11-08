package com.misbaha.app.data.preferences

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.misbaha.app.domain.model.AppLanguage

interface LocaleController {
    fun apply(language: AppLanguage)
}

class AndroidLocaleController : LocaleController {
    override fun apply(language: AppLanguage) {
        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.forLanguageTags(language.tag)
        )
    }
}
