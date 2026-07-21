package com.hamidzar2002.untangle.model

data class GameSession(
    val game: UntangleGame,
    val initialGame: UntangleGame,
    val level: Int,
    val startingNodeCount: Int,
    val moves: Int = 0,
    val showCompletion: Boolean = false
)
