package com.misbaha.app.ui.theme

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object Spacing {
    val Xs = 4.dp
    val Sm = 8.dp
    val Md = 12.dp
    val Lg = 16.dp
    val Xl = 24.dp
    val Xxl = 32.dp
}

object Sizes {
    val CardBorder = 1.dp
    val TopBarHeight = 64.dp
    val TopBarAction = 48.dp
    val DrawerWidth = 300.dp
    val SelectorMinHeight = 44.dp
    val MenuIconButton = 36.dp
    val IconSmall = 18.dp
    val IconMedium = 24.dp
    val DividerHeight = 40.dp
    val ListBottomInset = 96.dp
    val DialogTextMinLines = 3
    val DialogTextMaxLines = 6
}

object Motion {
    const val PRESS_SCALE = 0.97f
    const val PRESS_DURATION_MILLIS = 90
    const val RING_DURATION_MILLIS = 220
    const val PHASE_PULSE_DURATION_MILLIS = 520L
    const val CYCLE_PULSE_DURATION_MILLIS = 780L
    const val PULSE_SCALE = 0.12f
}

object CounterMetrics {
    val MaxSize = 320.dp
    val MinSize = 160.dp
    val RingStroke = 3.dp
    val ProgressStroke = 5.dp
    val InnerInset = 10.dp
    val PulsePadding = 32.dp
    const val SHORT_COUNT_LENGTH = 4
    const val MEDIUM_COUNT_LENGTH = 6
    const val LONG_COUNT_LENGTH = 9
    val ShortCountSize = 64.sp
    val MediumCountSize = 56.sp
    val LongCountSize = 46.sp
    val ExtraLongCountSize = 38.sp
    val ShortStatSize = 24.sp
    val MediumStatSize = 20.sp
    val LongStatSize = 16.sp
}

object LastUsed {
    const val RECENT_MINUTES = 5L
}
