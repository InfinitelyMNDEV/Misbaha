package com.misbaha.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import com.misbaha.app.ui.theme.Sizes
import com.misbaha.app.ui.theme.Spacing

data class SelectorOption<T>(
    val value: T,
    val label: String
)

@Composable
fun <T> SegmentedSelector(
    options: List<SelectorOption<T>>,
    selected: T,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.Sm)
    ) {
        options.forEach { option ->
            val isSelected = option.value == selected
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = Sizes.SelectorMinHeight)
                    .selectable(
                        selected = isSelected,
                        role = Role.RadioButton,
                        onClick = { onSelect(option.value) }
                    ),
                shape = MaterialTheme.shapes.small,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    Color.Transparent
                },
                contentColor = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                border = if (isSelected) {
                    null
                } else {
                    BorderStroke(Sizes.CardBorder, MaterialTheme.colorScheme.outlineVariant)
                }
            ) {
                Box(
                    modifier = Modifier.padding(
                        horizontal = Spacing.Sm,
                        vertical = Spacing.Md
                    ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = option.label,
                        style = MaterialTheme.typography.labelLarge,
                        textAlign = TextAlign.Center,
                        maxLines = 2
                    )
                }
            }
        }
    }
}
