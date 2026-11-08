package com.misbaha.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalDensity
import com.misbaha.app.domain.model.FontSizeOption
import com.misbaha.app.domain.model.ThemeMode

@Immutable
data class MisbahaColors(
    val topBar: Color,
    val onTopBar: Color,
    val topBarMuted: Color,
    val card: Color,
    val cardBorder: Color,
    val circleSurface: Color,
    val ringTrack: Color,
    val ringProgress: Color,
    val hint: Color
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer,
    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    secondaryContainer = LightSecondaryContainer,
    onSecondaryContainer = LightOnSecondaryContainer,
    tertiary = LightTertiary,
    onTertiary = LightOnTertiary,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline,
    outlineVariant = LightOutlineVariant,
    error = LightError,
    onError = LightOnError,
    errorContainer = LightErrorContainer,
    onErrorContainer = LightOnErrorContainer,
    inverseSurface = LightInverseSurface,
    inverseOnSurface = LightInverseOnSurface,
    inversePrimary = LightInversePrimary,
    scrim = LightScrim
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,
    tertiary = DarkTertiary,
    onTertiary = DarkOnTertiary,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant,
    error = DarkError,
    onError = DarkOnError,
    errorContainer = DarkErrorContainer,
    onErrorContainer = DarkOnErrorContainer,
    inverseSurface = DarkInverseSurface,
    inverseOnSurface = DarkInverseOnSurface,
    inversePrimary = DarkInversePrimary,
    scrim = DarkScrim
)

private val LightMisbahaColors = MisbahaColors(
    topBar = LightTopBar,
    onTopBar = LightOnTopBar,
    topBarMuted = LightTopBarMuted,
    card = LightCard,
    cardBorder = LightCardBorder,
    circleSurface = LightCircleSurface,
    ringTrack = LightRingTrack,
    ringProgress = LightRingProgress,
    hint = LightHint
)

private val DarkMisbahaColors = MisbahaColors(
    topBar = DarkTopBar,
    onTopBar = DarkOnTopBar,
    topBarMuted = DarkTopBarMuted,
    card = DarkCard,
    cardBorder = DarkCardBorder,
    circleSurface = DarkCircleSurface,
    ringTrack = DarkRingTrack,
    ringProgress = DarkRingProgress,
    hint = DarkHint
)

val LocalMisbahaColors = staticCompositionLocalOf { LightMisbahaColors }
val LocalMisbahaDark = staticCompositionLocalOf { false }

val MaterialTheme.misbahaColors: MisbahaColors
    @Composable
    @ReadOnlyComposable
    get() = LocalMisbahaColors.current

val MisbahaShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun MisbahaTheme(
    themeMode: ThemeMode,
    fontSize: FontSizeOption,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val extendedColors = if (darkTheme) DarkMisbahaColors else LightMisbahaColors
    val density = LocalDensity.current
    val scaledDensity = Density(
        density = density.density,
        fontScale = density.fontScale * fontSize.scaleFactor
    )
    CompositionLocalProvider(
        LocalMisbahaColors provides extendedColors,
        LocalMisbahaDark provides darkTheme,
        LocalDensity provides scaledDensity
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = MisbahaTypography,
            shapes = MisbahaShapes,
            content = content
        )
    }
}
