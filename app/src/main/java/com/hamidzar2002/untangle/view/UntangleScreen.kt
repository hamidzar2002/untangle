package com.hamidzar2002.untangle.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hamidzar2002.untangle.model.GamePoint
import com.hamidzar2002.untangle.model.UntangleGame
import com.hamidzar2002.untangle.ui.theme.UntangleTheme
import kotlin.math.hypot

@Composable
fun UntangleScreen(
    game: UntangleGame,
    onPointMoved: (pointId: Int, x: Float, y: Float) -> Unit,
    onRestart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Untangle",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            UntangleBoard(
                game = game,
                onPointMoved = onPointMoved,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when {
                        game.isSolved -> "Solved — no lines cross!"
                        game.crossingCount == 1 -> "1 crossing remains"
                        else -> "${game.crossingCount} crossings remain"
                    },
                    color = if (game.isSolved) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    fontWeight = FontWeight.Medium
                )
                Button(onClick = onRestart) {
                    Text("Restart")
                }
            }
        }
    }
}

@Composable
private fun UntangleBoard(
    game: UntangleGame,
    onPointMoved: (pointId: Int, x: Float, y: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val boardColor = MaterialTheme.colorScheme.surface
    val edgeColor = MaterialTheme.colorScheme.outline
    val crossingColor = MaterialTheme.colorScheme.error
    val pointColor = MaterialTheme.colorScheme.primary
    val activePointColor = MaterialTheme.colorScheme.secondary
    val latestGame by rememberUpdatedState(game)
    val latestOnPointMoved by rememberUpdatedState(onPointMoved)
    var draggedPointId by remember { mutableStateOf<Int?>(null) }

    Canvas(
        modifier = modifier
            .semantics {
                contentDescription =
                    "Untangle puzzle board with ${game.crossingCount} crossings"
            }
            .pointerInput(Unit) {
                val hitRadius = 28.dp.toPx()
                val boardPadding = 24.dp.toPx()

                fun pointOffset(point: GamePoint): Offset {
                    val usableWidth = (size.width - (boardPadding * 2f)).coerceAtLeast(1f)
                    val usableHeight = (size.height - (boardPadding * 2f)).coerceAtLeast(1f)
                    return Offset(
                        x = boardPadding + (point.x * usableWidth),
                        y = boardPadding + (point.y * usableHeight)
                    )
                }

                detectDragGestures(
                    onDragStart = { touch ->
                        draggedPointId = latestGame.points
                            .map { point -> point.id to pointOffset(point) }
                            .filter { (_, offset) ->
                                hypot(touch.x - offset.x, touch.y - offset.y) <= hitRadius
                            }
                            .minByOrNull { (_, offset) ->
                                hypot(touch.x - offset.x, touch.y - offset.y)
                            }
                            ?.first
                    },
                    onDragEnd = { draggedPointId = null },
                    onDragCancel = { draggedPointId = null },
                    onDrag = { change, _ ->
                        draggedPointId?.let { pointId ->
                            val usableWidth =
                                (size.width - (boardPadding * 2f)).coerceAtLeast(1f)
                            val usableHeight =
                                (size.height - (boardPadding * 2f)).coerceAtLeast(1f)
                            latestOnPointMoved(
                                pointId,
                                (change.position.x - boardPadding) / usableWidth,
                                (change.position.y - boardPadding) / usableHeight
                            )
                        }
                    }
                )
            }
    ) {
        val boardPadding = 24.dp.toPx()
        val pointRadius = 11.dp.toPx()
        val pointsById = game.points.associateBy(GamePoint::id)
        val crossingEdges = game.crossingEdgeIndexes()

        fun pointOffset(point: GamePoint): Offset = Offset(
            x = boardPadding + (point.x * (size.width - (boardPadding * 2f))),
            y = boardPadding + (point.y * (size.height - (boardPadding * 2f)))
        )

        drawRoundRect(
            color = boardColor,
            size = Size(size.width, size.height),
            cornerRadius = CornerRadius(20.dp.toPx())
        )

        game.edges.forEachIndexed { index, edge ->
            drawLine(
                color = if (index in crossingEdges) crossingColor else edgeColor,
                start = pointOffset(pointsById.getValue(edge.firstPointId)),
                end = pointOffset(pointsById.getValue(edge.secondPointId)),
                strokeWidth = 4.dp.toPx(),
                cap = StrokeCap.Round
            )
        }

        game.points.forEach { point ->
            drawCircle(
                color = if (point.id == draggedPointId) activePointColor else pointColor,
                radius = pointRadius,
                center = pointOffset(point)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun UntangleScreenPreview() {
    UntangleTheme {
        UntangleScreen(
            game = UntangleGame.starterPuzzle(),
            onPointMoved = { _, _, _ -> },
            onRestart = {}
        )
    }
}
