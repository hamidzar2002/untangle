package com.hamidzar2002.untangle.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hamidzar2002.untangle.ui.theme.UntangleTheme

@Composable
fun UntangleApp() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            KnotMark()
            Text(
                text = "Untangle",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "A new puzzle is taking shape.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun KnotMark() {
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val background = MaterialTheme.colorScheme.background

    Box(
        modifier = Modifier.size(144.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(112.dp)) {
            val lineWidth = 9.dp.toPx()
            drawLine(
                color = primary,
                start = Offset(size.width * 0.12f, size.height * 0.22f),
                end = Offset(size.width * 0.86f, size.height * 0.78f),
                strokeWidth = lineWidth,
                cap = StrokeCap.Round
            )
            drawLine(
                color = secondary,
                start = Offset(size.width * 0.18f, size.height * 0.82f),
                end = Offset(size.width * 0.82f, size.height * 0.14f),
                strokeWidth = lineWidth,
                cap = StrokeCap.Round
            )
            drawCircle(
                color = background,
                radius = 14.dp.toPx(),
                center = center
            )
            drawCircle(
                color = primary,
                radius = 10.dp.toPx(),
                center = center,
                style = Stroke(width = 5.dp.toPx())
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun UntangleAppPreview() {
    UntangleTheme {
        UntangleApp()
    }
}
