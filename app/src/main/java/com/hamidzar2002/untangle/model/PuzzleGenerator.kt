package com.hamidzar2002.untangle.model

import java.util.Random
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin
import kotlin.math.sqrt

data class GeneratedPuzzle(
    val game: UntangleGame,
    val solution: UntangleGame
)

/**
 * Builds a planar graph in a known crossing-free layout, then scatters the
 * point positions randomly until the player-facing layout contains crossings.
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

        repeat(attempts) {
            val candidate = solution.copy(
                points = createRandomLayout(solution.points.size, random)
                    .mapIndexed { index, coordinates ->
                        GamePoint(
                            id = solution.points[index].id,
                            x = coordinates.first,
                            y = coordinates.second
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

    private fun createRandomLayout(
        nodeCount: Int,
        random: Random
    ): List<Pair<Float, Float>> {
        val points = mutableListOf<Pair<Float, Float>>()
        val minimumDistance = (0.44 / sqrt(nodeCount.toDouble())).toFloat()

        repeat(nodeCount) {
            var selected: Pair<Float, Float>? = null
            var remainingAttempts = PLACEMENT_ATTEMPTS
            while (selected == null && remainingAttempts > 0) {
                val candidate = randomCoordinate(random)
                if (points.all { point ->
                        hypot(
                            candidate.first - point.first,
                            candidate.second - point.second
                        ) >= minimumDistance
                    }
                ) {
                    selected = candidate
                }
                remainingAttempts--
            }

            // Rejection sampling almost always succeeds. If it does not, keep
            // the best of another random batch instead of forming a pattern.
            val fallback = selected ?: List(PLACEMENT_ATTEMPTS) {
                randomCoordinate(random)
            }.maxBy { candidate ->
                points.minOfOrNull { point ->
                    hypot(
                        candidate.first - point.first,
                        candidate.second - point.second
                    )
                } ?: Float.MAX_VALUE
            }
            points += fallback
        }

        return points
    }

    private fun randomCoordinate(random: Random): Pair<Float, Float> =
        (BOARD_MARGIN + (random.nextFloat() * BOARD_RANGE)) to
            (BOARD_MARGIN + (random.nextFloat() * BOARD_RANGE))

    private fun edgeKey(a: Int, b: Int): EdgeKey =
        EdgeKey(first = minOf(a, b), second = maxOf(a, b))

    private data class EdgeKey(val first: Int, val second: Int)

    companion object {
        const val MIN_NODE_COUNT = 4
        const val MAX_STARTING_NODE_COUNT = 24
        const val MAX_NODE_COUNT = 30

        private const val GRAPH_ATTEMPTS = 8
        private const val SCRAMBLE_ATTEMPTS = 120
        private const val PLACEMENT_ATTEMPTS = 160
        private const val SEED_STEP = 104_729L
        private const val BOARD_MARGIN = 0.07f
        private const val BOARD_RANGE = 1f - (BOARD_MARGIN * 2f)
    }
}
