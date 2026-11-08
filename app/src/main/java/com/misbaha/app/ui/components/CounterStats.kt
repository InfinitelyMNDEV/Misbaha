package com.misbaha.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import com.misbaha.app.R
import com.misbaha.app.ui.theme.CounterMetrics
import com.misbaha.app.ui.theme.Sizes
import com.misbaha.app.ui.theme.Spacing
import com.misbaha.app.ui.theme.misbahaColors
import com.misbaha.app.ui.util.TextFormatting
import com.misbaha.app.ui.util.currentLocale

@Composable
fun CounterStats(
    totalCount: Long,
    todayCount: Long,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.misbahaColors
    val locale = currentLocale()
    val totalText = TextFormatting.number(totalCount, locale)
    val todayText = TextFormatting.number(todayCount, locale)
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = colors.card,
        border = BorderStroke(Sizes.CardBorder, colors.cardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Spacing.Lg),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatColumn(
                label = stringResource(R.string.counter_total),
                value = totalText,
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .width(Sizes.CardBorder)
                    .height(Sizes.DividerHeight)
                    .background(MaterialTheme.colorScheme.outlineVariant)
            )
            StatColumn(
                label = stringResource(R.string.counter_today),
                value = todayText,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatColumn(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = Spacing.Sm),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(Spacing.Xs))
        val fontSize = statFontSize(value)
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = fontSize,
                lineHeight = fontSize * 1.2f
            ),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            maxLines = 1,
            softWrap = false
        )
    }
}

private fun statFontSize(value: String): TextUnit = when {
    value.length <= CounterMetrics.SHORT_COUNT_LENGTH -> CounterMetrics.ShortStatSize
    value.length <= CounterMetrics.MEDIUM_COUNT_LENGTH -> CounterMetrics.MediumStatSize
    else -> CounterMetrics.LongStatSize
}
