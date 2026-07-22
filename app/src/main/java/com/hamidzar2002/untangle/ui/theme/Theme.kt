package com.hamidzar2002.untangle.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val UntangleColors = darkColorScheme(
    primary = Color(0xFFA8EB72),
    secondary = Color(0xFF73E5F0),
    error = Color(0xFFFF7189),
    background = Color(0xFF050B12),
    surface = Color(0xFF0E1723),
    surfaceVariant = Color(0xFF121E2C),
    outline = Color(0xFF33465C),
    onPrimary = Color(0xFF10200C),
    onSecondary = Color(0xFF071B1E),
    onBackground = Color(0xFFF3F7FA),
    onSurface = Color(0xFFF3F7FA),
    onSurfaceVariant = Color(0xFF9AA9BA)
)

@Composable
fun UntangleTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = UntangleColors,
        content = content
    )
}
