package com.hamidzar2002.untangle.controller

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class UntangleControllerTest {
    @Test
    fun moveActionUpdatesModelThroughController() {
        val controller = UntangleController()

        controller.movePoint(pointId = 0, x = 0.4f, y = 0.6f)

        val movedPoint = controller.game.value.points.first { it.id == 0 }
        assertEquals(0.4f, movedPoint.x)
        assertEquals(0.6f, movedPoint.y)
    }

    @Test
    fun restartRestoresStarterPuzzle() {
        val controller = UntangleController()
        controller.movePoint(pointId = 0, x = 0.4f, y = 0.6f)

        controller.restartPuzzle()

        val restartedPoint = controller.game.value.points.first { it.id == 0 }
        assertEquals(0.15f, restartedPoint.x)
        assertEquals(0.15f, restartedPoint.y)
        assertFalse(controller.game.value.isSolved)
    }
}
