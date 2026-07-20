package com.hamidzar2002.untangle.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val UntangleColors = darkColorScheme(
    primary = Color(0xFF6FE7C1),
    secondary = Color(0xFFFFB86B),
    background = Color(0xFF111416),
    surface = Color(0xFF1B2023),
    onPrimary = Color(0xFF082019),
    onSecondary = Color(0xFF291708),
    onBackground = Color(0xFFF0F5F3),
    onSurface = Color(0xFFF0F5F3),
    onSurfaceVariant = Color(0xFFAAB6B1)
)

@Composable
fun UntangleTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = UntangleColors,
        content = content
    )
}

