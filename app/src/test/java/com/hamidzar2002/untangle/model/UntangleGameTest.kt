package com.hamidzar2002.untangle.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UntangleGameTest {
    @Test
    fun starterPuzzleHasOneCrossing() {
        val game = UntangleGame.starterPuzzle()

        assertEquals(1, game.crossingCount)
        assertFalse(game.isSolved)
    }

    @Test
    fun movingPointsIntoCycleOrderSolvesPuzzle() {
        val solved = UntangleGame.starterPuzzle()
            .movePoint(pointId = 0, x = 0.15f, y = 0.15f)
            .movePoint(pointId = 1, x = 0.85f, y = 0.15f)
            .movePoint(pointId = 2, x = 0.85f, y = 0.85f)
            .movePoint(pointId = 3, x = 0.15f, y = 0.85f)

        assertEquals(0, solved.crossingCount)
        assertTrue(solved.isSolved)
    }

    @Test
    fun sharedEndpointDoesNotCountAsCrossing() {
        val game = UntangleGame(
            points = listOf(
                GamePoint(id = 0, x = 0.1f, y = 0.1f),
                GamePoint(id = 1, x = 0.5f, y = 0.5f),
                GamePoint(id = 2, x = 0.9f, y = 0.1f)
            ),
            edges = listOf(
                GameEdge(firstPointId = 0, secondPointId = 1),
                GameEdge(firstPointId = 1, secondPointId = 2)
            )
        )

        assertEquals(0, game.crossingCount)
    }

    @Test
    fun movedPointIsClampedToBoard() {
        val moved = UntangleGame.starterPuzzle()
            .movePoint(pointId = 0, x = -2f, y = 3f)
            .points
            .first { it.id == 0 }

        assertEquals(0f, moved.x)
        assertEquals(1f, moved.y)
    }
}
