package com.misbaha.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.misbaha.app.R
import com.misbaha.app.ui.theme.Sizes
import com.misbaha.app.ui.theme.Spacing
import com.misbaha.app.ui.theme.misbahaColors

@Composable
fun HomeTopBar(
    hijriDate: String,
    weekday: String,
    onMenuClick: () -> Unit
) {
    val colors = MaterialTheme.misbahaColors
    Surface(color = colors.topBar) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Top + WindowInsetsSides.Horizontal
                    )
                )
                .padding(horizontal = Spacing.Lg, vertical = Spacing.Md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = stringResource(R.string.open_menu),
                    tint = colors.onTopBar
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = hijriDate,
                    style = MaterialTheme.typography.titleLarge,
                    color = colors.onTopBar,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(Spacing.Xs))
                Text(
                    text = weekday,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.topBarMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun ScreenTopBar(
    title: String,
    onBack: () -> Unit
) {
    val colors = MaterialTheme.misbahaColors
    Surface(color = colors.topBar) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Top + WindowInsetsSides.Horizontal
                    )
                )
                .height(Sizes.TopBarHeight)
                .padding(horizontal = Spacing.Xs),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.navigate_back),
                    tint = colors.onTopBar
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = colors.onTopBar,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(Sizes.TopBarAction))
        }
    }
}
