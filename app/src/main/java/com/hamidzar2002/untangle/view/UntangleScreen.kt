package com.hamidzar2002.untangle.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hamidzar2002.untangle.model.GamePoint
import com.hamidzar2002.untangle.model.PuzzleGenerator
import com.hamidzar2002.untangle.model.UntangleGame
import com.hamidzar2002.untangle.ui.theme.UntangleTheme
import kotlin.math.hypot
import kotlin.math.roundToInt

private val ScreenTop = Color(0xFF050B12)
private val ScreenBottom = Color(0xFF08111A)
private val Panel = Color(0xFF0E1723)
private val PanelBorder = Color(0xFF223144)
private val BoardTop = Color(0xFF111B29)
private val BoardBottom = Color(0xFF09121D)
private val Lime = Color(0xFFA8EB72)
private val Cyan = Color(0xFF73E5F0)
private val Crossing = Color(0xFFFF7189)
private val MutedText = Color(0xFF9AA9BA)

@Composable
fun UntangleScreen(
    game: UntangleGame,
    level: Int,
    startingNodeCount: Int,
    moves: Int,
    showCompletion: Boolean,
    onPointMoved: (pointId: Int, x: Float, y: Float) -> Unit,
    onMoveFinished: () -> Unit,
    onRestart: () -> Unit,
    onNewPuzzle: () -> Unit,
    onNextPuzzle: () -> Unit,
    onNodeCountSelected: (Int) -> Unit,
    showPrivacyOptions: Boolean = false,
    onPrivacyOptions: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showNodePicker by remember { mutableStateOf(false) }
    var nodePickerValue by remember(startingNodeCount) {
        mutableStateOf(startingNodeCount.toFloat())
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(ScreenTop, ScreenBottom)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            GameHeader(level = level, moves = moves)

            UntangleBoard(
                game = game,
                onPointMoved = onPointMoved,
                onMoveFinished = onMoveFinished,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            GameStatus(game = game)

            ActionToolbar(
                startingNodeCount = startingNodeCount,
                showPrivacyOptions = showPrivacyOptions,
                onNewPuzzle = onNewPuzzle,
                onRestart = onRestart,
                onChooseNodes = {
                    nodePickerValue = startingNodeCount.toFloat()
                    showNodePicker = true
                },
                onPrivacyOptions = onPrivacyOptions
            )

        }
    }

    if (showCompletion) {
        CompletionDialog(
            level = level,
            moves = moves,
            onNextPuzzle = onNextPuzzle,
            onRestart = onRestart
        )
    }

    if (showNodePicker) {
        val selectedCount = nodePickerValue.roundToInt()
        NodePickerDialog(
            selectedCount = selectedCount,
            nodePickerValue = nodePickerValue,
            onValueChanged = { nodePickerValue = it },
            onDecrease = {
                nodePickerValue = (selectedCount - 1)
                    .coerceAtLeast(PuzzleGenerator.MIN_NODE_COUNT)
                    .toFloat()
            },
            onIncrease = {
                nodePickerValue = (selectedCount + 1)
                    .coerceAtMost(PuzzleGenerator.MAX_STARTING_NODE_COUNT)
                    .toFloat()
            },
            onConfirm = {
                showNodePicker = false
                onNodeCountSelected(selectedCount)
            },
            onDismiss = { showNodePicker = false }
        )
    }
}

@Composable
private fun GameHeader(level: Int, moves: Int) {
    Surface(
        color = Panel.copy(alpha = 0.96f),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, PanelBorder, RoundedCornerShape(18.dp))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(9.dp)
                    .background(Lime, CircleShape)
            )
            Text(
                text = "UNTANGLE",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.4.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            HeaderMetric(label = "LEVEL", value = level.toString())
            Box(
                modifier = Modifier
                    .padding(horizontal = 14.dp)
                    .size(width = 1.dp, height = 28.dp)
                    .background(PanelBorder)
            )
            HeaderMetric(label = "MOVES", value = moves.toString())
        }
    }
}

@Composable
private fun HeaderMetric(label: String, value: String) {
    Column(horizontalAlignment = Alignment.End) {
        Text(
            text = label,
            color = MutedText,
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp
        )
        Text(
            text = value,
            color = Color.White,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun GameStatus(game: UntangleGame) {
    val solved = game.isSolved
    val statusText = when {
        solved -> "Perfect! All lines are untangled."
        game.crossingCount == 1 -> "Keep going — 1 crossing remains."
        else -> "Drag the nodes — ${game.crossingCount} crossings remain."
    }

    Surface(
        color = Panel,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, PanelBorder, RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = if (solved) Lime.copy(alpha = 0.15f) else Cyan.copy(alpha = 0.12f),
                shape = CircleShape,
                modifier = Modifier.size(30.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = if (solved) "✓" else "◇",
                        color = if (solved) Lime else Cyan,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Text(
                text = statusText,
                color = if (solved) Lime else Color(0xFFDDE6EF),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .padding(start = 12.dp)
                    .weight(1f)
            )
        }
    }
}

@Composable
private fun ActionToolbar(
    startingNodeCount: Int,
    showPrivacyOptions: Boolean,
    onNewPuzzle: () -> Unit,
    onRestart: () -> Unit,
    onChooseNodes: () -> Unit,
    onPrivacyOptions: () -> Unit
) {
    Surface(
        color = Panel,
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, PanelBorder, RoundedCornerShape(18.dp))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ToolbarAction(
                symbol = "✦",
                label = "NEW",
                onClick = onNewPuzzle,
                modifier = Modifier.weight(1f)
            )
            ToolbarAction(
                symbol = "↶",
                label = "RESTART",
                onClick = onRestart,
                modifier = Modifier.weight(1f)
            )
            ToolbarAction(
                symbol = startingNodeCount.toString(),
                label = "NODES",
                onClick = onChooseNodes,
                modifier = Modifier.weight(1f)
            )
            if (showPrivacyOptions) {
                ToolbarAction(
                    symbol = "⋮",
                    label = "PRIVACY",
                    onClick = onPrivacyOptions,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ToolbarAction(
    symbol: String,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = symbol,
                color = Lime,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 18.sp
            )
            Text(
                text = label,
                color = MutedText,
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.7.sp
            )
        }
    }
}

@Composable
private fun CompletionDialog(
    level: Int,
    moves: Int,
    onNextPuzzle: () -> Unit,
    onRestart: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        containerColor = Panel,
        titleContentColor = Lime,
        textContentColor = Color(0xFFDDE6EF),
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "✓", color = Lime, fontSize = 38.sp)
                Text("Perfect!", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Text(
                text = "Level $level untangled in $moves moves. " +
                    "Your next puzzle adds another node and more connections.",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = onNextPuzzle,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Lime,
                    contentColor = Color(0xFF10200C)
                )
            ) {
                Text("Next level", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onRestart) {
                Text("Replay")
            }
        }
    )
}

@Composable
private fun NodePickerDialog(
    selectedCount: Int,
    nodePickerValue: Float,
    onValueChanged: (Float) -> Unit,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Panel,
        titleContentColor = Color.White,
        textContentColor = MutedText,
        title = { Text("Custom game") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Choose how many nodes your first level should have.")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NodeStepButton(symbol = "−", onClick = onDecrease)
                    Text(
                        text = selectedCount.toString(),
                        color = Color.White,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 28.dp)
                    )
                    NodeStepButton(symbol = "+", onClick = onIncrease)
                }
                Slider(
                    value = nodePickerValue,
                    onValueChange = onValueChanged,
                    valueRange = PuzzleGenerator.MIN_NODE_COUNT.toFloat()..
                        PuzzleGenerator.MAX_STARTING_NODE_COUNT.toFloat(),
                    steps = PuzzleGenerator.MAX_STARTING_NODE_COUNT -
                        PuzzleGenerator.MIN_NODE_COUNT - 1
                )
                Text(
                    text = "Each completed level adds one node and more connections.",
                    fontSize = 12.sp
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Lime,
                    contentColor = Color(0xFF10200C)
                )
            ) {
                Text("Generate", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun NodeStepButton(symbol: String, onClick: () -> Unit) {
    Surface(
        color = Color(0xFF121E2C),
        shape = CircleShape,
        modifier = Modifier
            .size(42.dp)
            .border(1.dp, PanelBorder, CircleShape)
    ) {
        IconButton(onClick = onClick) {
            Text(symbol, color = Color.White, fontSize = 22.sp)
        }
    }
}

@Composable
private fun UntangleBoard(
    game: UntangleGame,
    onPointMoved: (pointId: Int, x: Float, y: Float) -> Unit,
    onMoveFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val latestGame by rememberUpdatedState(game)
    val latestOnPointMoved by rememberUpdatedState(onPointMoved)
    val latestOnMoveFinished by rememberUpdatedState(onMoveFinished)
    var draggedPointId by remember { mutableStateOf<Int?>(null) }

    Canvas(
        modifier = modifier
            .semantics {
                contentDescription =
                    "Untangle puzzle board with ${game.crossingCount} crossings"
            }
            .pointerInput(Unit) {
                val hitRadius = 30.dp.toPx()
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
                    onDragEnd = {
                        val pointWasMoved = draggedPointId != null
                        draggedPointId = null
                        if (pointWasMoved) latestOnMoveFinished()
                    },
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
        val pointRadius = 12.dp.toPx()
        val cornerRadius = 22.dp.toPx()
        val pointsById = game.points.associateBy(GamePoint::id)
        val crossingEdges = game.crossingEdgeIndexes()

        fun pointOffset(point: GamePoint): Offset = Offset(
            x = boardPadding + (point.x * (size.width - (boardPadding * 2f))),
            y = boardPadding + (point.y * (size.height - (boardPadding * 2f)))
        )

        drawRoundRect(
            brush = Brush.verticalGradient(listOf(BoardTop, BoardBottom)),
            size = Size(size.width, size.height),
            cornerRadius = CornerRadius(cornerRadius)
        )

        val gridColor = Color.White.copy(alpha = 0.022f)
        val gridSpacing = 34.dp.toPx()
        var gridX = gridSpacing
        while (gridX < size.width) {
            drawLine(gridColor, Offset(gridX, 0f), Offset(gridX, size.height), 1f)
            gridX += gridSpacing
        }
        var gridY = gridSpacing
        while (gridY < size.height) {
            drawLine(gridColor, Offset(0f, gridY), Offset(size.width, gridY), 1f)
            gridY += gridSpacing
        }

        game.edges.forEachIndexed { index, edge ->
            val start = pointOffset(pointsById.getValue(edge.firstPointId))
            val end = pointOffset(pointsById.getValue(edge.secondPointId))
            val color = when {
                game.isSolved -> Lime
                index in crossingEdges -> Crossing
                else -> Cyan
            }
            drawLine(
                color = color.copy(alpha = 0.12f),
                start = start,
                end = end,
                strokeWidth = 8.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawLine(
                color = color.copy(alpha = 0.9f),
                start = start,
                end = end,
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
        }

        game.points.forEach { point ->
            val center = pointOffset(point)
            val active = point.id == draggedPointId
            val nodeColor = if (active) Lime else Cyan
            drawCircle(
                color = nodeColor.copy(alpha = if (active) 0.24f else 0.13f),
                radius = pointRadius * 2.15f,
                center = center
            )
            drawCircle(
                color = Color(0xFF07101A),
                radius = pointRadius * 1.32f,
                center = center
            )
            drawCircle(
                color = Color(0xFF657386),
                radius = pointRadius * 1.16f,
                center = center
            )
            drawCircle(
                color = Color(0xFF172434),
                radius = pointRadius * 0.84f,
                center = center
            )
            drawCircle(
                color = nodeColor,
                radius = pointRadius * 0.48f,
                center = center
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.8f),
                radius = pointRadius * 0.16f,
                center = center - Offset(pointRadius * 0.13f, pointRadius * 0.13f)
            )
        }

        drawRoundRect(
            color = PanelBorder,
            size = Size(size.width, size.height),
            cornerRadius = CornerRadius(cornerRadius),
            style = Stroke(width = 1.dp.toPx())
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 780)
@Composable
private fun UntangleScreenPreview() {
    UntangleTheme {
        UntangleScreen(
            game = PuzzleGenerator().generate(nodeCount = 9, level = 4, seed = 1L).game,
            level = 4,
            startingNodeCount = 6,
            moves = 8,
            showCompletion = false,
            onPointMoved = { _, _, _ -> },
            onMoveFinished = {},
            onRestart = {},
            onNewPuzzle = {},
            onNextPuzzle = {},
            onNodeCountSelected = {}
        )
    }
}
