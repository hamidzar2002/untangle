package com.hamidzar2002.untangle.controller

import androidx.lifecycle.ViewModel
import com.hamidzar2002.untangle.model.GameSession
import com.hamidzar2002.untangle.model.PuzzleGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Translates user actions from the view into model updates.
 *
 * Puzzle rules remain in the model; this controller only coordinates state.
 */
class UntangleController(
    private val puzzleGenerator: PuzzleGenerator = PuzzleGenerator(),
    private val seedProvider: () -> Long = { System.nanoTime() }
) : ViewModel() {
    private var puzzleSequence = 0L
    private val _session = MutableStateFlow(createSession(level = 1, startingNodeCount = 6))
    val session: StateFlow<GameSession> = _session.asStateFlow()

    fun movePoint(pointId: Int, x: Float, y: Float) {
        _session.value = _session.value.copy(
            game = _session.value.game.movePoint(pointId = pointId, x = x, y = y)
        )
    }

    fun finishMove() {
        val current = _session.value
        _session.value = current.copy(
            moves = current.moves + 1,
            showCompletion = current.game.isSolved
        )
    }

    fun restartPuzzle() {
        val current = _session.value
        _session.value = current.copy(
            game = current.initialGame,
            moves = 0,
            showCompletion = false
        )
    }

    fun newPuzzle() {
        val current = _session.value
        _session.value = createSession(
            level = current.level,
            startingNodeCount = current.startingNodeCount
        )
    }

    fun nextPuzzle() {
        val current = _session.value
        _session.value = createSession(
            level = current.level + 1,
            startingNodeCount = current.startingNodeCount
        )
    }

    fun selectStartingNodeCount(nodeCount: Int) {
        require(nodeCount in PuzzleGenerator.MIN_NODE_COUNT..PuzzleGenerator.MAX_STARTING_NODE_COUNT)
        _session.value = createSession(level = 1, startingNodeCount = nodeCount)
    }

    private fun createSession(level: Int, startingNodeCount: Int): GameSession {
        val nodeCount = (startingNodeCount + level - 1)
            .coerceAtMost(PuzzleGenerator.MAX_NODE_COUNT)
        val generated = puzzleGenerator.generate(
            nodeCount = nodeCount,
            level = level,
            seed = seedProvider() + puzzleSequence++
        )
        return GameSession(
            game = generated.game,
            initialGame = generated.game,
            level = level,
            startingNodeCount = startingNodeCount
        )
    }
}
