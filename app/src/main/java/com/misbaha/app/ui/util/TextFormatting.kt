package com.misbaha.app.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalConfiguration
import java.text.NumberFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.chrono.HijrahChronology
import java.time.format.TextStyle
import java.time.temporal.ChronoField
import java.util.Locale

object TextFormatting {
    private const val ARABIC_LANGUAGE = "ar"
    private val ArabicDigits = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')

    fun number(value: Long, locale: Locale): String =
        localizeDigits(NumberFormat.getIntegerInstance(locale).format(value), locale)

    fun year(value: Long, locale: Locale): String {
        val format = NumberFormat.getIntegerInstance(locale).apply { isGroupingUsed = false }
        return localizeDigits(format.format(value), locale)
    }

    fun time(instant: Instant, zone: ZoneId, locale: Locale): String {
        val dateTime = instant.atZone(zone)
        return localizeDigits(
            String.format(Locale.US, "%02d:%02d", dateTime.hour, dateTime.minute),
            locale
        )
    }

    fun gregorianDate(date: LocalDate, locale: Locale): String {
        val year = localizeDigits(String.format(Locale.US, "%04d", date.year), locale)
        val month = localizeDigits(String.format(Locale.US, "%02d", date.monthValue), locale)
        val day = localizeDigits(String.format(Locale.US, "%02d", date.dayOfMonth), locale)
        return "$year/$month/$day"
    }

    fun hijriDate(date: LocalDate, locale: Locale, monthNames: List<String>): String {
        val hijriDate = HijrahChronology.INSTANCE.date(date)
        val day = number(hijriDate.get(ChronoField.DAY_OF_MONTH).toLong(), locale)
        val month = monthNames[hijriDate.get(ChronoField.MONTH_OF_YEAR) - 1]
        val yearText = year(hijriDate.get(ChronoField.YEAR).toLong(), locale)
        return "$day $month $yearText"
    }

    fun weekday(date: LocalDate, locale: Locale): String =
        date.dayOfWeek.getDisplayName(TextStyle.FULL, locale)

    private fun localizeDigits(value: String, locale: Locale): String {
        if (locale.language != ARABIC_LANGUAGE) {
            return value
        }
        return buildString(value.length) {
            value.forEach { character ->
                append(if (character in '0'..'9') ArabicDigits[character - '0'] else character)
            }
        }
    }
}

@Composable
@ReadOnlyComposable
fun currentLocale(): Locale = LocalConfiguration.current.locales[0]
