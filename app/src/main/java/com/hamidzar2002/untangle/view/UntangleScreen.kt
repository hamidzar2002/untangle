package com.hamidzar2002.untangle.view

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.libraries.ads.mobile.sdk.banner.AdSize
import com.google.android.libraries.ads.mobile.sdk.banner.AdView
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAd
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAdRequest
import com.google.android.libraries.ads.mobile.sdk.common.AdLoadCallback
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError
import com.hamidzar2002.untangle.BuildConfig
import com.hamidzar2002.untangle.model.GamePoint
import com.hamidzar2002.untangle.model.UntangleGame
import com.hamidzar2002.untangle.ui.theme.UntangleTheme
import kotlin.math.hypot
import kotlin.math.roundToInt

@Composable
fun UntangleScreen(
    game: UntangleGame,
    onPointMoved: (pointId: Int, x: Float, y: Float) -> Unit,
    onRestart: () -> Unit,
    showBanner: Boolean = false,
    showPrivacyOptions: Boolean = false,
    onPrivacyOptions: () -> Unit = {},
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Untangle",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
                if (showPrivacyOptions) {
                    TextButton(onClick = onPrivacyOptions) {
                        Text("Privacy choices")
                    }
                }
            }

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

            if (showBanner) {
                UntangleBanner(
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = "Advertisement" }
                )
            }
        }
    }
}

@Composable
private fun UntangleBanner(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }
    val isPreview = LocalInspectionMode.current

    if (activity == null || isPreview) return

    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        val widthDp = maxWidth.value.roundToInt().coerceAtLeast(1)
        val adSize = remember(activity, widthDp) {
            AdSize.getLargeAnchoredAdaptiveBannerAdSize(activity, widthDp)
        }
        val adView = remember(activity, adSize) { AdView(activity) }
        val request = remember(adSize) {
            BannerAdRequest.Builder(
                BuildConfig.ADMOB_BANNER_ID,
                adSize
            ).build()
        }

        LaunchedEffect(adView, request) {
            adView.loadAd(
                request,
                object : AdLoadCallback<BannerAd> {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        Log.w(TAG, "Banner failed to load: $adError")
                    }
                }
            )
        }

        DisposableEffect(adView) {
            onDispose { adView.destroy() }
        }

        Box(contentAlignment = Alignment.Center) {
            AndroidView(
                factory = { adView },
                modifier = Modifier.wrapContentSize()
            )
        }
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

private const val TAG = "UntangleBanner"

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
