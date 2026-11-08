package com.misbaha.app.ui.counter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.misbaha.app.R
import com.misbaha.app.domain.counter.ZahraCompletion
import com.misbaha.app.domain.counter.ZahraCycle
import com.misbaha.app.domain.model.ZikrType
import com.misbaha.app.ui.components.CompletionPulse
import com.misbaha.app.ui.components.CounterCircle
import com.misbaha.app.ui.components.CounterStats
import com.misbaha.app.ui.components.PulseSignal
import com.misbaha.app.ui.theme.CounterMetrics
import com.misbaha.app.ui.theme.LocalMisbahaDark
import com.misbaha.app.ui.theme.Motion
import com.misbaha.app.ui.theme.Spacing
import com.misbaha.app.ui.util.SystemBarAppearance
import com.misbaha.app.ui.util.TextFormatting
import com.misbaha.app.ui.util.currentLocale
import kotlinx.coroutines.delay

@Composable
fun CounterScreen(
    viewModel: CounterViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isDark = LocalMisbahaDark.current
    val haptic = LocalHapticFeedback.current
    var pulseSignal by remember { mutableStateOf<PulseSignal?>(null) }
    var pulseId by remember { mutableIntStateOf(0) }

    SystemBarAppearance(
        statusBarIconsDark = !isDark,
        navigationBarIconsDark = !isDark
    )

    LaunchedEffect(viewModel) {
        viewModel.events.collect { completion ->
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            pulseId += 1
            val signal = PulseSignal(id = pulseId, completion = completion)
            pulseSignal = signal
            val duration = if (completion == ZahraCompletion.CYCLE) {
                Motion.CYCLE_PULSE_DURATION_MILLIS
            } else {
                Motion.PHASE_PULSE_DURATION_MILLIS
            }
            delay(duration)
            if (pulseSignal?.id == signal.id) {
                pulseSignal = null
            }
        }
    }

    LaunchedEffect(uiState.isMissing) {
        if (uiState.isMissing) {
            onBack()
        }
    }

    if (uiState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val locale = currentLocale()
    val phaseTitle = if (uiState.type == ZikrType.ZAHRA) {
        phaseTitleFor(uiState.phaseIndex)
    } else {
        null
    }
    val progress = if (uiState.type == ZikrType.ZAHRA) {
        uiState.phaseCount.toFloat() / ZahraCycle.TARGET_PER_PHASE.toFloat()
    } else {
        null
    }
    val countText = if (uiState.type == ZikrType.ZAHRA) {
        stringResource(
            R.string.phase_progress,
            TextFormatting.number(uiState.phaseCount.toLong(), locale),
            TextFormatting.number(ZahraCycle.TARGET_PER_PHASE.toLong(), locale)
        )
    } else {
        TextFormatting.number(uiState.totalCount, locale)
    }
    val hintText = stringResource(R.string.counter_tap_hint)
    val tapActionLabel = stringResource(R.string.counter_tap_action)

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        val availableWidth = maxWidth
        val availableHeight = maxHeight
        val isLandscape = availableWidth > availableHeight
        if (isLandscape) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Spacing.Xl)
            ) {
                CounterTopRow(onBack = onBack)
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = uiState.text,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(Spacing.Xl))
                        CounterStats(
                            totalCount = uiState.totalCount,
                            todayCount = uiState.todayCount
                        )
                    }
                    Spacer(modifier = Modifier.width(Spacing.Xl))
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        val diameter = landscapeCircleSize(
                            width = availableWidth,
                            height = availableHeight
                        )
                        CounterCircle(
                            countText = countText,
                            hintText = hintText,
                            phaseTitle = phaseTitle,
                            progress = progress,
                            progressKey = uiState.phaseIndex,
                            diameter = diameter,
                            tapActionLabel = tapActionLabel,
                            onTap = viewModel::onTap
                        )
                        CompletionPulse(signal = pulseSignal, diameter = diameter)
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = Spacing.Xl),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CounterTopRow(onBack = onBack)
                Spacer(modifier = Modifier.height(Spacing.Sm))
                Text(
                    text = uiState.text,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(Spacing.Xl))
                val diameter = portraitCircleSize(width = availableWidth)
                Box(contentAlignment = Alignment.Center) {
                    CounterCircle(
                        countText = countText,
                        hintText = hintText,
                        phaseTitle = phaseTitle,
                        progress = progress,
                        progressKey = uiState.phaseIndex,
                        diameter = diameter,
                        tapActionLabel = tapActionLabel,
                        onTap = viewModel::onTap
                    )
                    CompletionPulse(signal = pulseSignal, diameter = diameter)
                }
                Spacer(modifier = Modifier.height(Spacing.Xl))
                CounterStats(
                    totalCount = uiState.totalCount,
                    todayCount = uiState.todayCount
                )
                Spacer(modifier = Modifier.height(Spacing.Xl))
            }
        }
    }
}

@Composable
private fun CounterTopRow(onBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.navigate_back),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun phaseTitleFor(index: Int): String = when (index) {
    0 -> stringResource(R.string.zahra_phase_0)
    1 -> stringResource(R.string.zahra_phase_1)
    else -> stringResource(R.string.zahra_phase_2)
}

private fun portraitCircleSize(width: Dp): Dp =
    minOf(width - Spacing.Xxl * 2f, CounterMetrics.MaxSize)
        .coerceIn(CounterMetrics.MinSize, CounterMetrics.MaxSize)

private fun landscapeCircleSize(width: Dp, height: Dp): Dp =
    minOf(width / 2f - Spacing.Xl * 2f, height - Spacing.Xxl * 3f)
        .coerceIn(CounterMetrics.MinSize, CounterMetrics.MaxSize)
