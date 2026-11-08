package com.misbaha.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.misbaha.app.domain.counter.ZahraCompletion
import com.misbaha.app.ui.theme.CounterMetrics
import com.misbaha.app.ui.theme.Motion
import com.misbaha.app.ui.theme.Spacing
import com.misbaha.app.ui.theme.misbahaColors

data class PulseSignal(
    val id: Int,
    val completion: ZahraCompletion
)

@Composable
fun CounterCircle(
    countText: String,
    hintText: String,
    phaseTitle: String?,
    progress: Float?,
    progressKey: Int,
    diameter: Dp,
    tapActionLabel: String,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.misbahaColors
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) Motion.PRESS_SCALE else 1f,
        animationSpec = tween(Motion.PRESS_DURATION_MILLIS)
    )
    val ring = remember(progressKey) { Animatable(0f) }
    LaunchedEffect(progress, progressKey) {
        val target = progress?.coerceIn(0f, 1f) ?: 0f
        ring.animateTo(target, tween(Motion.RING_DURATION_MILLIS))
    }
    val haptic = LocalHapticFeedback.current
    val countSize = countFontSize(countText)
    Box(
        modifier = modifier
            .size(diameter)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClickLabel = tapActionLabel,
                role = Role.Button
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onTap()
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val ringStroke = CounterMetrics.RingStroke.toPx()
            val progressStroke = CounterMetrics.ProgressStroke.toPx()
            val innerInset = CounterMetrics.InnerInset.toPx()
            val outerRadius = (diameter.toPx() - maxOf(ringStroke, progressStroke)) / 2f
            val innerRadius = (diameter.toPx() / 2f) - ringStroke - innerInset
            drawCircle(
                color = colors.ringTrack,
                radius = outerRadius,
                style = Stroke(width = ringStroke)
            )
            drawCircle(
                color = colors.circleSurface,
                radius = innerRadius
            )
            if (progress != null) {
                drawArc(
                    color = colors.ringProgress,
                    startAngle = -90f,
                    sweepAngle = 360f * ring.value,
                    useCenter = false,
                    topLeft = Offset(
                        x = center.x - outerRadius,
                        y = center.y - outerRadius
                    ),
                    size = Size(outerRadius * 2f, outerRadius * 2f),
                    style = Stroke(width = progressStroke, cap = StrokeCap.Round)
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.Xl),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (phaseTitle != null) {
                Text(
                    text = phaseTitle,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(Spacing.Xs))
            }
            Text(
                text = countText,
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = countSize,
                    lineHeight = countSize * 1.15f
                ),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 1,
                softWrap = false
            )
            Spacer(modifier = Modifier.height(Spacing.Xs))
            Text(
                text = hintText,
                style = MaterialTheme.typography.labelLarge,
                color = colors.hint
            )
        }
    }
}

@Composable
fun CompletionPulse(
    signal: PulseSignal?,
    diameter: Dp
) {
    val colors = MaterialTheme.misbahaColors
    val progress = remember { Animatable(0f) }
    LaunchedEffect(signal?.id) {
        if (signal == null) {
            progress.snapTo(0f)
        } else {
            progress.snapTo(0f)
            val duration = if (signal.completion == ZahraCompletion.CYCLE) {
                Motion.CYCLE_PULSE_DURATION_MILLIS
            } else {
                Motion.PHASE_PULSE_DURATION_MILLIS
            }
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = duration.toInt(),
                    easing = LinearOutSlowInEasing
                )
            )
        }
    }
    val activeSignal = signal
    if (activeSignal != null && progress.value > 0f) {
        Canvas(
            modifier = Modifier.size(diameter + CounterMetrics.PulsePadding * 2f)
        ) {
            val baseRadius = diameter.toPx() / 2f
            val radius = baseRadius * (1f + Motion.PULSE_SCALE * progress.value)
            val strength = if (activeSignal.completion == ZahraCompletion.CYCLE) 0.55f else 0.35f
            val alpha = (1f - progress.value) * strength
            drawCircle(
                color = colors.ringProgress.copy(alpha = alpha),
                radius = radius,
                style = Stroke(width = CounterMetrics.ProgressStroke.toPx())
            )
            if (activeSignal.completion == ZahraCompletion.CYCLE) {
                drawCircle(
                    color = colors.ringProgress.copy(alpha = alpha * 0.6f),
                    radius = radius * 0.9f,
                    style = Stroke(width = CounterMetrics.RingStroke.toPx())
                )
            }
        }
    }
}

private fun countFontSize(countText: String): TextUnit = when {
    countText.length <= CounterMetrics.SHORT_COUNT_LENGTH -> CounterMetrics.ShortCountSize
    countText.length <= CounterMetrics.MEDIUM_COUNT_LENGTH -> CounterMetrics.MediumCountSize
    countText.length <= CounterMetrics.LONG_COUNT_LENGTH -> CounterMetrics.LongCountSize
    else -> CounterMetrics.ExtraLongCountSize
}
