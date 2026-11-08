package com.misbaha.app.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.misbaha.app.R
import com.misbaha.app.domain.model.ZikrType
import com.misbaha.app.ui.components.ConfirmDialog
import com.misbaha.app.ui.components.HomeTopBar
import com.misbaha.app.ui.components.ZikrCard
import com.misbaha.app.ui.components.ZikrFormDialog
import com.misbaha.app.ui.theme.LocalMisbahaDark
import com.misbaha.app.ui.theme.Sizes
import com.misbaha.app.ui.theme.Spacing
import com.misbaha.app.ui.util.SystemBarAppearance
import com.misbaha.app.ui.util.TextFormatting
import com.misbaha.app.ui.util.currentLocale
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onOpenCounter: (Long) -> Unit,
    onOpenSettings: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val isDark = LocalMisbahaDark.current
    var showForm by rememberSaveable { mutableStateOf(false) }
    var editingId by rememberSaveable { mutableStateOf<Long?>(null) }
    var resetId by rememberSaveable { mutableStateOf<Long?>(null) }
    var deleteId by rememberSaveable { mutableStateOf<Long?>(null) }

    val locale = currentLocale()
    val monthNames = stringArrayResource(R.array.hijri_months).toList()
    val hijriDate = TextFormatting.hijriDate(uiState.today, locale, monthNames)
    val weekday = TextFormatting.weekday(uiState.today, locale)
    val editingItem = uiState.items.firstOrNull { it.id == editingId }
    val resetItem = uiState.items.firstOrNull { it.id == resetId }
    val deleteItem = uiState.items.firstOrNull { it.id == deleteId }

    SystemBarAppearance(
        statusBarIconsDark = false,
        navigationBarIconsDark = !isDark
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            HomeDrawer(
                onSettings = {
                    scope.launch {
                        drawerState.close()
                        onOpenSettings()
                    }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                HomeTopBar(
                    hijriDate = hijriDate,
                    weekday = weekday,
                    onMenuClick = {
                        scope.launch { drawerState.open() }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        editingId = null
                        showForm = true
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = CircleShape,
                    modifier = Modifier.navigationBarsPadding()
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.add_zikr)
                    )
                }
            },
            contentWindowInsets = WindowInsets.safeDrawing.only(
                WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal
            )
        ) { innerPadding ->
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(
                        start = Spacing.Lg,
                        end = Spacing.Lg,
                        top = Spacing.Lg,
                        bottom = Sizes.ListBottomInset
                    ),
                    verticalArrangement = Arrangement.spacedBy(Spacing.Md)
                ) {
                    items(items = uiState.items, key = { it.id }) { item ->
                        ZikrCard(
                            zikr = item,
                            now = uiState.now,
                            zoneId = uiState.zoneId,
                            onClick = { onOpenCounter(item.id) },
                            onReset = { resetId = item.id },
                            onEdit = if (item.type == ZikrType.CUSTOM) {
                                {
                                    editingId = item.id
                                    showForm = true
                                }
                            } else {
                                null
                            },
                            onDelete = if (item.type == ZikrType.CUSTOM) {
                                { deleteId = item.id }
                            } else {
                                null
                            }
                        )
                    }
                }
            }
        }
    }

    if (showForm) {
        ZikrFormDialog(
            title = stringResource(
                if (editingItem == null) R.string.add_zikr else R.string.edit_zikr
            ),
            initialText = editingItem?.text.orEmpty(),
            existingTexts = uiState.items
                .map { it.text }
                .filter { it != editingItem?.text },
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

    if (resetItem != null) {
        ConfirmDialog(
            title = stringResource(R.string.reset_count_title),
            message = stringResource(
                if (resetItem.type == ZikrType.ZAHRA) {
                    R.string.reset_zahra_message
                } else {
                    R.string.reset_count_message
                }
            ),
            confirmLabel = stringResource(R.string.reset),
            onConfirm = { viewModel.reset(resetItem.id) },
            onDismiss = { resetId = null }
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
