package com.hamidzar2002.untangle.model

/**
 * A point in board-relative coordinates. Both axes use the inclusive 0..1 range.
 */
data class GamePoint(
    val id: Int,
    val x: Float,
    val y: Float
)
