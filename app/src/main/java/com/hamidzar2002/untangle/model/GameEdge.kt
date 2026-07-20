package com.hamidzar2002.untangle.model

data class GameEdge(
    val firstPointId: Int,
    val secondPointId: Int
) {
    fun sharesPointWith(other: GameEdge): Boolean =
        firstPointId == other.firstPointId ||
            firstPointId == other.secondPointId ||
            secondPointId == other.firstPointId ||
            secondPointId == other.secondPointId
}
