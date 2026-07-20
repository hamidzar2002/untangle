package com.hamidzar2002.untangle.controller

import androidx.lifecycle.ViewModel
import com.hamidzar2002.untangle.model.UntangleGame
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Translates user actions from the view into model updates.
 *
 * Puzzle rules remain in [UntangleGame]; this controller only coordinates state.
 */
class UntangleController : ViewModel() {
    private val _game = MutableStateFlow(UntangleGame.starterPuzzle())
    val game: StateFlow<UntangleGame> = _game.asStateFlow()

    fun movePoint(pointId: Int, x: Float, y: Float) {
        _game.value = _game.value.movePoint(pointId = pointId, x = x, y = y)
    }

    fun restartPuzzle() {
        _game.value = UntangleGame.starterPuzzle()
    }
}
