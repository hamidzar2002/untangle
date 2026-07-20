package com.hamidzar2002.untangle.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hamidzar2002.untangle.controller.UntangleController

/**
 * The only bridge between the MVC controller and the stateless Compose view.
 */
@Composable
fun UntangleRoute(controller: UntangleController = viewModel()) {
    val game by controller.game.collectAsStateWithLifecycle()

    UntangleScreen(
        game = game,
        onPointMoved = controller::movePoint,
        onRestart = controller::restartPuzzle
    )
}
