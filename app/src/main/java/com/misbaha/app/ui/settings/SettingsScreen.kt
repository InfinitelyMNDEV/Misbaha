package com.misbaha.app.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.misbaha.app.R
import com.misbaha.app.domain.model.AppLanguage
import com.misbaha.app.domain.model.AppSettings
import com.misbaha.app.domain.model.FontSizeOption
import com.misbaha.app.domain.model.ThemeMode
import com.misbaha.app.domain.model.Zikr
import com.misbaha.app.ui.components.ConfirmDialog
import com.misbaha.app.ui.components.ScreenTopBar
import com.misbaha.app.ui.components.SegmentedSelector
import com.misbaha.app.ui.components.SelectorOption
import com.misbaha.app.ui.components.ZikrFormDialog
import com.misbaha.app.ui.theme.LocalMisbahaDark
import com.misbaha.app.ui.theme.Sizes
import com.misbaha.app.ui.theme.Spacing
import com.misbaha.app.ui.theme.misbahaColors
import com.misbaha.app.ui.util.SystemBarAppearance

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isDark = LocalMisbahaDark.current
    var showForm by rememberSaveable { mutableStateOf(false) }
    var editingId by rememberSaveable { mutableStateOf<Long?>(null) }
    var deleteId by rememberSaveable { mutableStateOf<Long?>(null) }
    val editingItem = uiState.customZikrs.firstOrNull { it.id == editingId }
    val deleteItem = uiState.customZikrs.firstOrNull { it.id == deleteId }

    SystemBarAppearance(
        statusBarIconsDark = false,
        navigationBarIconsDark = !isDark
    )

    Scaffold(
        topBar = {
            ScreenTopBar(
                title = stringResource(R.string.settings),
                onBack = onBack
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing.only(
            WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal
        )
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(
                start = Spacing.Lg,
                end = Spacing.Lg,
                top = Spacing.Xl,
                bottom = Spacing.Xxl
            ),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(Spacing.Lg)
        ) {
            item {
                SectionHeader(title = stringResource(R.string.system_settings))
            }
            item {
                SystemSettingsCard(
                    settings = uiState.settings,
                    onThemeChange = viewModel::setTheme,
                    onFontSizeChange = viewModel::setFontSize,
                    onLanguageChange = viewModel::setLanguage
                )
            }
            item {
                SectionHeader(
                    title = stringResource(R.string.custom_settings),
                    action = {
                        TextButton(
                            onClick = {
                                editingId = null
                                showForm = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(Sizes.IconSmall)
                            )
                            Spacer(modifier = Modifier.width(Spacing.Xs))
                            Text(stringResource(R.string.add_zikr))
                        }
                    }
                )
            }
            item {
                CustomZikrCard(
                    customZikrs = uiState.customZikrs,
                    onEdit = { id ->
                        editingId = id
                        showForm = true
                    },
                    onDelete = { id -> deleteId = id }
                )
            }
        }
    }

    if (showForm) {
        ZikrFormDialog(
            title = stringResource(
                if (editingItem == null) R.string.add_zikr else R.string.edit_zikr
            ),
            initialText = editingItem?.text.orEmpty(),
            existingTexts = uiState.allTexts.filter { it != editingItem?.text },
            onDismiss = {
                showForm = false
                editingId = null
            },
            onSave = { text ->
                val item = editingItem
                if (item == null) {
                    viewModel.addCustom(text)
                } else {
                    viewModel.updateCustom(item.id, text)
                }
                showForm = false
                editingId = null
            }
        )
    }

    if (deleteItem != null) {
        ConfirmDialog(
            title = stringResource(R.string.delete_zikr_title),
            message = stringResource(R.string.delete_zikr_message),
            confirmLabel = stringResource(R.string.delete),
            onConfirm = { viewModel.deleteCustom(deleteItem.id) },
            onDismiss = { deleteId = null }
        )
    }
}

@Composable
private fun SectionHeader(
    title: String,
    action: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.Xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )
        if (action != null) {
            action()
        }
    }
}

@Composable
private fun SystemSettingsCard(
    settings: AppSettings,
    onThemeChange: (ThemeMode) -> Unit,
    onFontSizeChange: (FontSizeOption) -> Unit,
    onLanguageChange: (AppLanguage) -> Unit
) {
    val colors = MaterialTheme.misbahaColors
    val themeOptions = listOf(
        SelectorOption(ThemeMode.LIGHT, stringResource(R.string.theme_light)),
        SelectorOption(ThemeMode.DARK, stringResource(R.string.theme_dark)),
        SelectorOption(ThemeMode.SYSTEM, stringResource(R.string.theme_system))
    )
    val fontSizeOptions = listOf(
        SelectorOption(FontSizeOption.SMALL, stringResource(R.string.font_small)),
        SelectorOption(FontSizeOption.DEFAULT, stringResource(R.string.font_default)),
        SelectorOption(FontSizeOption.LARGE, stringResource(R.string.font_large)),
        SelectorOption(FontSizeOption.EXTRA_LARGE, stringResource(R.string.font_extra_large))
    )
    val languageOptions = listOf(
        SelectorOption(AppLanguage.ARABIC, stringResource(R.string.language_arabic)),
        SelectorOption(AppLanguage.ENGLISH, stringResource(R.string.language_english))
    )
    Surface(
        shape = MaterialTheme.shapes.large,
        color = colors.card,
        border = BorderStroke(Sizes.CardBorder, colors.cardBorder)
    ) {
        Column {
            SettingBlock(label = stringResource(R.string.theme)) {
                SegmentedSelector(
                    options = themeOptions,
                    selected = settings.theme,
                    onSelect = onThemeChange
                )
            }
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.padding(horizontal = Spacing.Lg)
            )
            SettingBlock(label = stringResource(R.string.font_size)) {
                SegmentedSelector(
                    options = fontSizeOptions,
                    selected = settings.fontSize,
                    onSelect = onFontSizeChange
                )
            }
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.padding(horizontal = Spacing.Lg)
            )
            SettingBlock(label = stringResource(R.string.language)) {
                SegmentedSelector(
                    options = languageOptions,
                    selected = settings.language,
                    onSelect = onLanguageChange
                )
            }
        }
    }
}

@Composable
private fun SettingBlock(
    label: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Spacing.Lg)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.padding(top = Spacing.Md))
        content()
    }
}

@Composable
private fun CustomZikrCard(
    customZikrs: List<Zikr>,
    onEdit: (Long) -> Unit,
    onDelete: (Long) -> Unit
) {
    val colors = MaterialTheme.misbahaColors
    Surface(
        shape = MaterialTheme.shapes.large,
        color = colors.card,
        border = BorderStroke(Sizes.CardBorder, colors.cardBorder)
    ) {
        Column {
            if (customZikrs.isEmpty()) {
                Text(
                    text = stringResource(R.string.custom_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.Xl)
                )
            } else {
                customZikrs.forEachIndexed { index, zikr ->
                    if (index > 0) {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant,
                            modifier = Modifier.padding(start = Spacing.Lg, end = Spacing.Lg)
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = Spacing.Lg,
                                end = Spacing.Sm,
                                top = Spacing.Md,
                                bottom = Spacing.Md
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = zikr.text,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = Spacing.Sm)
                        )
                        IconButton(onClick = { onEdit(zikr.id) }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = stringResource(R.string.edit_action),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = { onDelete(zikr.id) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = stringResource(R.string.delete_action),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}
