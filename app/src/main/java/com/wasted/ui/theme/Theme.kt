package com.wasted.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val WastedBlack   = Color(0xFF000000)
val WastedWhite   = Color(0xFFFFFFFF)
val WastedOrange  = Color(0xFFFF9500)
val WastedRed     = Color(0xFFFF3B30)
val WastedCardBg  = Color(0xFF0E0E0E)
val WastedDimText = Color(0xFF404040)

private val colorScheme = darkColorScheme(
    background   = WastedBlack,
    surface      = WastedCardBg,
    primary      = WastedWhite,
    onBackground = WastedWhite,
    onSurface    = WastedWhite,
)

@Composable
fun WastedTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = colorScheme, content = content)
}
