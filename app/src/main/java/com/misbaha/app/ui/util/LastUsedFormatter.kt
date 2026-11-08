package com.misbaha.app.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.misbaha.app.R
import com.misbaha.app.ui.theme.LastUsed
import java.time.Duration
import java.time.Instant
import java.time.ZoneId

@Composable
fun lastUsedLabel(lastUsedAt: Instant?, now: Instant, zone: ZoneId): String {
    if (lastUsedAt == null) {
        return stringResource(R.string.last_used_never)
    }
    val locale = currentLocale()
    val usedDate = lastUsedAt.atZone(zone).toLocalDate()
    val today = now.atZone(zone).toLocalDate()
    val minutesAgo = Duration.between(lastUsedAt, now).toMinutes()
    return when {
        usedDate == today && minutesAgo in 0..LastUsed.RECENT_MINUTES ->
            stringResource(R.string.last_used_just_now)
        usedDate == today ->
            stringResource(R.string.last_used_today, TextFormatting.time(lastUsedAt, zone, locale))
        else ->
            TextFormatting.gregorianDate(usedDate, locale)
    }
}
