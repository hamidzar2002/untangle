package com.hamidzar2002.untangle.controller

import com.hamidzar2002.untangle.model.PuzzleGenerator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UntangleControllerTest {
    @Test
    fun moveActionUpdatesModelThroughController() {
        val controller = testController()

        controller.movePoint(pointId = 0, x = 0.4f, y = 0.6f)

        val movedPoint = controller.session.value.game.points.first { it.id == 0 }
        assertEquals(0.4f, movedPoint.x)
        assertEquals(0.6f, movedPoint.y)
    }

    @Test
    fun restartRestoresStarterPuzzle() {
        val controller = testController()
        val initialPoint = controller.session.value.initialGame.points.first { it.id == 0 }
        controller.movePoint(pointId = 0, x = 0.4f, y = 0.6f)

        controller.restartPuzzle()

        val restartedPoint = controller.session.value.game.points.first { it.id == 0 }
        assertEquals(initialPoint, restartedPoint)
        assertEquals(0, controller.session.value.moves)
        assertFalse(controller.session.value.game.isSolved)
    }

    @Test
    fun solvingPuzzleShowsCongratulationsAndNextPuzzleIsHarder() {
        val generator = PuzzleGenerator()
        val solution = generator.generate(nodeCount = 6, level = 1, seed = 42L).solution
        val controller = UntangleController(
            puzzleGenerator = generator,
            seedProvider = { 42L }
        )

        solution.points.forEach { point ->
            controller.movePoint(point.id, point.x, point.y)
        }
        controller.finishMove()

        assertTrue(controller.session.value.showCompletion)
        assertEquals(1, controller.session.value.moves)

        controller.nextPuzzle()

        assertEquals(2, controller.session.value.level)
        assertEquals(7, controller.session.value.game.points.size)
        assertEquals(11, controller.session.value.game.edges.size)
        assertFalse(controller.session.value.game.isSolved)
    }

    @Test
    fun selectingNodeCountStartsNewProgression() {
        val controller = testController()
        controller.nextPuzzle()

        controller.selectStartingNodeCount(14)

        assertEquals(1, controller.session.value.level)
        assertEquals(14, controller.session.value.startingNodeCount)
        assertEquals(14, controller.session.value.game.points.size)
    }

    private fun testController(): UntangleController = UntangleController(
        puzzleGenerator = PuzzleGenerator(),
        seedProvider = { 42L }
    )
}
