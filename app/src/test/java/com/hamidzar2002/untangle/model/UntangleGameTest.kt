package com.hamidzar2002.untangle.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UntangleGameTest {
    @Test
    fun generatedPuzzleStartsWithCrossings() {
        val generated = PuzzleGenerator().generate(nodeCount = 6, level = 1, seed = 7L)

        assertTrue(generated.game.crossingCount > 0)
        assertFalse(generated.game.isSolved)
    }

    @Test
    fun generatedSolutionHasNoCrossings() {
        val solved = PuzzleGenerator().generate(nodeCount = 12, level = 4, seed = 99L)
            .solution

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
        val moved = PuzzleGenerator().generate(nodeCount = 6, level = 1, seed = 13L)
            .game
            .movePoint(pointId = 0, x = -2f, y = 3f)
            .points
            .first { it.id == 0 }

        assertEquals(0f, moved.x)
        assertEquals(1f, moved.y)
    }

    @Test
    fun generatedGraphIsMaximalOuterPlanarSize() {
        val generated = PuzzleGenerator().generate(nodeCount = 18, level = 8, seed = 123L)

        assertEquals(18, generated.game.points.size)
        assertEquals((2 * 18) - 3, generated.game.edges.size)
    }

    @Test
    fun sameSeedProducesSamePuzzle() {
        val generator = PuzzleGenerator()

        val first = generator.generate(nodeCount = 10, level = 3, seed = 456L)
        val second = generator.generate(nodeCount = 10, level = 3, seed = 456L)

        assertEquals(first, second)
    }

    @Test
    fun visibleLayoutUsesRandomCoordinatesInsteadOfSolutionCircle() {
        val generated = PuzzleGenerator().generate(nodeCount = 10, level = 2, seed = 808L)
        val visibleCoordinates = generated.game.points.map { it.x to it.y }.toSet()
        val solutionCoordinates = generated.solution.points.map { it.x to it.y }.toSet()

        assertFalse(visibleCoordinates == solutionCoordinates)
    }

    @Test
    fun everySupportedNodeCountProducesTangledPuzzleWithSolvedLayout() {
        val generator = PuzzleGenerator()

        for (nodeCount in PuzzleGenerator.MIN_NODE_COUNT..PuzzleGenerator.MAX_NODE_COUNT) {
            val generated = generator.generate(
                nodeCount = nodeCount,
                level = 1,
                seed = nodeCount.toLong() * 1_009L
            )

            assertEquals(nodeCount, generated.game.points.size)
            assertFalse("$nodeCount-node puzzle started solved", generated.game.isSolved)
            assertTrue("$nodeCount-node solution contains crossings", generated.solution.isSolved)
        }
    }
}
