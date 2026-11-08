package com.misbaha.app.domain.model

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

enum class FontSizeOption(val scaleFactor: Float) {
    SMALL(0.85f),
    DEFAULT(1f),
    LARGE(1.15f),
    EXTRA_LARGE(1.3f)
}

enum class AppLanguage(val tag: String) {
    ARABIC("ar"),
    ENGLISH("en")
}

data class AppSettings(
    val theme: ThemeMode = ThemeMode.SYSTEM,
    val fontSize: FontSizeOption = FontSizeOption.DEFAULT,
    val language: AppLanguage = AppLanguage.ARABIC
)
