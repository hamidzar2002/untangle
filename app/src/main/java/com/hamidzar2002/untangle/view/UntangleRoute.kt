package com.hamidzar2002.untangle.view

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hamidzar2002.untangle.ads.AdsManager
import com.hamidzar2002.untangle.controller.UntangleController

/**
 * The only bridge between the MVC controller and the stateless Compose view.
 */
@Composable
fun UntangleRoute(
    activity: Activity,
    adsManager: AdsManager,
    controller: UntangleController = viewModel()
) {
    val game by controller.game.collectAsStateWithLifecycle()
    val adsState by adsManager.uiState.collectAsStateWithLifecycle()

    UntangleScreen(
        game = game,
        onPointMoved = controller::movePoint,
        onRestart = controller::restartPuzzle,
        showBanner = adsState.adsReady,
        showPrivacyOptions = adsState.privacyOptionsRequired,
        onPrivacyOptions = { adsManager.showPrivacyOptions(activity) }
    )
}
