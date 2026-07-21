package com.hamidzar2002.untangle.model

import java.util.Random
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

data class GeneratedPuzzle(
    val game: UntangleGame,
    val solution: UntangleGame
)

/**
 * Builds a planar graph in a known crossing-free layout, then permutes the
 * point positions until the player-facing layout contains crossings.
 */
class PuzzleGenerator {
    fun generate(nodeCount: Int, level: Int, seed: Long): GeneratedPuzzle {
        require(nodeCount in MIN_NODE_COUNT..MAX_NODE_COUNT)
        require(level >= 1)

        repeat(GRAPH_ATTEMPTS) { graphAttempt ->
            val random = Random(seed + (graphAttempt * SEED_STEP))
            val solutionPoints = createSolutionPoints(nodeCount, random)
            val edges = createPlanarEdges(nodeCount, random)
            val solution = UntangleGame(points = solutionPoints, edges = edges)
            val scrambled = scramble(
                solution = solution,
                random = random,
                attempts = SCRAMBLE_ATTEMPTS + (level * 12)
            )

            if (!scrambled.isSolved) {
                return GeneratedPuzzle(game = scrambled, solution = solution)
            }
        }

        error("Unable to generate a tangled puzzle for $nodeCount nodes.")
    }

    private fun createSolutionPoints(nodeCount: Int, random: Random): List<GamePoint> {
        val rotation = random.nextDouble() * PI * 2.0
        val angleStep = (PI * 2.0) / nodeCount
        return List(nodeCount) { id ->
            val jitter = (random.nextDouble() - 0.5) * angleStep * 0.18
            val angle = rotation + (id * angleStep) + jitter
            GamePoint(
                id = id,
                x = (0.5 + (cos(angle) * 0.41)).toFloat(),
                y = (0.5 + (sin(angle) * 0.41)).toFloat()
            )
        }
    }

    private fun createPlanarEdges(nodeCount: Int, random: Random): List<GameEdge> {
        val edges = linkedSetOf<EdgeKey>()

        for (id in 0 until nodeCount) {
            edges += edgeKey(id, (id + 1) % nodeCount)
        }

        // Random ear removal triangulates a convex polygon without crossings.
        val polygon = (0 until nodeCount).toMutableList()
        while (polygon.size > 3) {
            val earIndex = random.nextInt(polygon.size)
            val previous = polygon[(earIndex - 1 + polygon.size) % polygon.size]
            val next = polygon[(earIndex + 1) % polygon.size]
            edges += edgeKey(previous, next)
            polygon.removeAt(earIndex)
        }

        return edges.map { key ->
            GameEdge(firstPointId = key.first, secondPointId = key.second)
        }
    }

    private fun scramble(
        solution: UntangleGame,
        random: Random,
        attempts: Int
    ): UntangleGame {
        var best = solution
        var bestCrossings = 0
        val coordinates = solution.points.map { point -> point.x to point.y }

        repeat(attempts) {
            val shuffled = coordinates.toMutableList()
            for (index in shuffled.lastIndex downTo 1) {
                val swapIndex = random.nextInt(index + 1)
                val temporary = shuffled[index]
                shuffled[index] = shuffled[swapIndex]
                shuffled[swapIndex] = temporary
            }

            val candidate = solution.copy(
                points = solution.points.mapIndexed { index, point ->
                    point.copy(
                        x = shuffled[index].first,
                        y = shuffled[index].second
                    )
                }
            )
            if (candidate.crossingCount > bestCrossings) {
                best = candidate
                bestCrossings = candidate.crossingCount
            }
        }

        return best
    }

    private fun edgeKey(a: Int, b: Int): EdgeKey =
        EdgeKey(first = minOf(a, b), second = maxOf(a, b))

    private data class EdgeKey(val first: Int, val second: Int)

    companion object {
        const val MIN_NODE_COUNT = 4
        const val MAX_STARTING_NODE_COUNT = 24
        const val MAX_NODE_COUNT = 30

        private const val GRAPH_ATTEMPTS = 8
        private const val SCRAMBLE_ATTEMPTS = 120
        private const val SEED_STEP = 104_729L
    }
}
