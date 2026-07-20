package com.hamidzar2002.untangle.model

internal object SegmentGeometry {
    private const val EPSILON = 0.000001f

    fun intersects(
        firstStart: GamePoint,
        firstEnd: GamePoint,
        secondStart: GamePoint,
        secondEnd: GamePoint
    ): Boolean {
        val firstOrientation = orientation(firstStart, firstEnd, secondStart)
        val secondOrientation = orientation(firstStart, firstEnd, secondEnd)
        val thirdOrientation = orientation(secondStart, secondEnd, firstStart)
        val fourthOrientation = orientation(secondStart, secondEnd, firstEnd)

        if (oppositeSigns(firstOrientation, secondOrientation) &&
            oppositeSigns(thirdOrientation, fourthOrientation)
        ) {
            return true
        }

        return (isZero(firstOrientation) && isOnSegment(firstStart, secondStart, firstEnd)) ||
            (isZero(secondOrientation) && isOnSegment(firstStart, secondEnd, firstEnd)) ||
            (isZero(thirdOrientation) && isOnSegment(secondStart, firstStart, secondEnd)) ||
            (isZero(fourthOrientation) && isOnSegment(secondStart, firstEnd, secondEnd))
    }

    private fun orientation(start: GamePoint, end: GamePoint, point: GamePoint): Float =
        ((end.x - start.x) * (point.y - start.y)) -
            ((end.y - start.y) * (point.x - start.x))

    private fun oppositeSigns(first: Float, second: Float): Boolean =
        (first > EPSILON && second < -EPSILON) ||
            (first < -EPSILON && second > EPSILON)

    private fun isZero(value: Float): Boolean = value in -EPSILON..EPSILON

    private fun isOnSegment(start: GamePoint, point: GamePoint, end: GamePoint): Boolean =
        point.x >= minOf(start.x, end.x) - EPSILON &&
            point.x <= maxOf(start.x, end.x) + EPSILON &&
            point.y >= minOf(start.y, end.y) - EPSILON &&
            point.y <= maxOf(start.y, end.y) + EPSILON
}
