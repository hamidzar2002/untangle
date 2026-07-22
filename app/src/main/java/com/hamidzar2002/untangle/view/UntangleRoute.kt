package com.hamidzar2002.untangle.view

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hamidzar2002.untangle.ads.AdsManager
import com.hamidzar2002.untangle.audio.GameSoundManager
import com.hamidzar2002.untangle.controller.UntangleController

/**
 * The only bridge between the MVC controller and the stateless Compose view.
 */
@Composable
fun UntangleRoute(
    activity: Activity,
    adsManager: AdsManager,
    gameSoundManager: GameSoundManager,
    controller: UntangleController = viewModel()
) {
    val session by controller.session.collectAsStateWithLifecycle()
    val adsState by adsManager.uiState.collectAsStateWithLifecycle()
    val soundEnabled by gameSoundManager.soundEnabled.collectAsStateWithLifecycle()
    var completionAdHandled by rememberSaveable(session.level) { mutableStateOf(false) }

    LaunchedEffect(session.showCompletion, session.level) {
        if (!session.showCompletion) {
            completionAdHandled = false
        } else if (!completionAdHandled) {
            adsManager.showLevelCompletionInterstitial(
                activity = activity,
                completedLevel = session.level,
                onComplete = { completionAdHandled = true }
            )
        }
    }

    UntangleScreen(
        game = session.game,
        level = session.level,
        startingNodeCount = session.startingNodeCount,
        moves = session.moves,
        showCompletion = session.showCompletion && completionAdHandled,
        onPointMoved = controller::movePoint,
        onMoveFinished = { pointId, wasFreeBeforeMove ->
            val movedGame = controller.session.value.game
            val solved = movedGame.isSolved
            val becameFree = !wasFreeBeforeMove && movedGame.isPointFree(pointId)
            controller.finishMove()
            when {
                solved -> gameSoundManager.playSolved()
                becameFree -> gameSoundManager.playNodeFreed()
                else -> gameSoundManager.playMove()
            }
        },
        onRestart = controller::restartPuzzle,
        onNewPuzzle = controller::newPuzzle,
        onNextPuzzle = controller::nextPuzzle,
        onNodeCountSelected = controller::selectStartingNodeCount,
        soundEnabled = soundEnabled,
        onSoundToggle = gameSoundManager::toggleSound,
        showPrivacyOptions = adsState.privacyOptionsRequired,
        onPrivacyOptions = { adsManager.showPrivacyOptions(activity) }
    )
}
