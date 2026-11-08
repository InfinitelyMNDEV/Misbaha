package com.misbaha.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.misbaha.app.R
import com.misbaha.app.ui.home.HomeZikrItem
import com.misbaha.app.ui.theme.Sizes
import com.misbaha.app.ui.theme.Spacing
import com.misbaha.app.ui.theme.misbahaColors
import com.misbaha.app.ui.util.TextFormatting
import com.misbaha.app.ui.util.currentLocale
import com.misbaha.app.ui.util.lastUsedLabel
import java.time.Instant
import java.time.ZoneId

@Composable
fun ZikrCard(
    zikr: HomeZikrItem,
    now: Instant,
    zoneId: ZoneId,
    onClick: () -> Unit,
    onReset: () -> Unit,
    onEdit: (() -> Unit)?,
    onDelete: (() -> Unit)?
) {
    val colors = MaterialTheme.misbahaColors
    val locale = currentLocale()
    var menuExpanded by remember { mutableStateOf(false) }
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = colors.card,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(Sizes.CardBorder, colors.cardBorder)
    ) {
        Column(modifier = Modifier.padding(Spacing.Lg)) {
            Row(verticalAlignment = Alignment.Top) {
                Text(
                    text = zikr.text,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = Spacing.Sm)
                )
                Box {
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier.size(Sizes.MenuIconButton)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = stringResource(R.string.more_options),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        if (onEdit != null) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.edit_action)) },
                                onClick = {
                                    menuExpanded = false
                                    onEdit()
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = null
                                    )
                                }
                            )
                        }
                        if (onDelete != null) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = stringResource(R.string.delete_action),
                                        color = MaterialTheme.colorScheme.error
                                    )
                                },
                                onClick = {
                                    menuExpanded = false
                                    onDelete()
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            )
                        }
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.reset_count)) },
                            onClick = {
                                menuExpanded = false
                                onReset()
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.RestartAlt,
                                    contentDescription = null
                                )
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(Spacing.Sm))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(
                        R.string.tasbeeh_count,
                        TextFormatting.number(zikr.totalCount, locale)
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = lastUsedLabel(zikr.lastUsedAt, now, zoneId),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
