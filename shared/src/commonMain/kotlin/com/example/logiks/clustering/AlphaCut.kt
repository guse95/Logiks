package com.example.logiks.clustering

data class AlphaCut(
    val left: Double,
    val right: Double
) {
    fun distanceTo(other: AlphaCut): Double {
        val leftDifference = left - other.left
        val rightDifference = right - other.right

        return kotlin.math.sqrt(
            leftDifference * leftDifference +
                    rightDifference * rightDifference
        )
    }

    fun midpoint(): Double {
        return (left + right) / 2.0
    }
}