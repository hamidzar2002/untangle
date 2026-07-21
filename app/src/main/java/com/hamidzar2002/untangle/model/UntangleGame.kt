package com.hamidzar2002.untangle.model

data class UntangleGame(
    val points: List<GamePoint>,
    val edges: List<GameEdge>
) {
    private val pointsById = points.associateBy(GamePoint::id)

    init {
        require(pointsById.size == points.size) { "Point IDs must be unique." }
        require(
            points.all { point -> point.x in 0f..1f && point.y in 0f..1f }
        ) { "Point coordinates must be inside the board." }
        require(
            edges.all { edge ->
                edge.firstPointId != edge.secondPointId &&
                    edge.firstPointId in pointsById &&
                    edge.secondPointId in pointsById
            }
        ) { "Every edge must connect two different existing points." }
    }

    val crossingCount: Int
        get() = crossingPairs().size

    val isSolved: Boolean
        get() = crossingCount == 0

    fun crossingEdgeIndexes(): Set<Int> =
        crossingPairs().flatMapTo(mutableSetOf()) { pair ->
            listOf(pair.first, pair.second)
        }

    fun movePoint(pointId: Int, x: Float, y: Float): UntangleGame {
        require(pointId in pointsById) { "Unknown point ID: $pointId" }
        return copy(
            points = points.map { point ->
                if (point.id == pointId) {
                    point.copy(x = x.coerceIn(0f, 1f), y = y.coerceIn(0f, 1f))
                } else {
                    point
                }
            }
        )
    }

    private fun crossingPairs(): List<Pair<Int, Int>> = buildList {
        for (firstIndex in edges.indices) {
            val firstEdge = edges[firstIndex]
            for (secondIndex in (firstIndex + 1) until edges.size) {
                val secondEdge = edges[secondIndex]
                if (firstEdge.sharesPointWith(secondEdge)) {
                    continue
                }

                if (
                    SegmentGeometry.intersects(
                        firstStart = pointsById.getValue(firstEdge.firstPointId),
                        firstEnd = pointsById.getValue(firstEdge.secondPointId),
                        secondStart = pointsById.getValue(secondEdge.firstPointId),
                        secondEnd = pointsById.getValue(secondEdge.secondPointId)
                    )
                ) {
                    add(firstIndex to secondIndex)
                }
            }
        }
    }

}
