package com.example.logiks.fuzzy

import kotlin.math.exp
import kotlin.math.sqrt

data class FuzzyNumber(
    val mu: Double,
    val sigma: Double
) {
    init {
        require(sigma > 0.0) {
            "Sigma must be greater than zero"
        }
    }

    /**
     * Left boundary of alpha-cut.
     */
    fun alphaCutLeft(alpha: Double): Double {
        validateAlpha(alpha)

        val value = sigma * sqrt(-2.0 * kotlin.math.ln(alpha))
        return exp(mu - value)
    }

    /**
     * Right boundary of alpha-cut.
     */
    fun alphaCutRight(alpha: Double): Double {
        validateAlpha(alpha)

        val value = sigma * sqrt(-2.0 * kotlin.math.ln(alpha))
        return exp(mu + value)
    }

    /**
     * Returns alpha-cut as [left, right].
     */
    fun alphaCut(alpha: Double): ClosedFloatingPointRange<Double> {
        return alphaCutLeft(alpha)..alphaCutRight(alpha)
    }

    /**
     * Membership function of the fuzzy log-normal quantity.
     */
    fun membership(x: Double): Double {
        if (x <= 0.0) {
            return 0.0
        }

        val value = (kotlin.math.ln(x) - mu) / sigma

        return exp(-0.5 * value * value)
    }

    fun graphPoints(
        xMin: Double,
        xMax: Double,
        pointCount: Int = 100
    ): List<GraphPoint> {

        require(xMin > 0.0) {
            "xMin must be greater than zero"
        }

        require(xMax > xMin) {
            "xMax must be greater than xMin"
        }

        require(pointCount >= 2) {
            "Point count must be at least 2"
        }

        val step = (xMax - xMin) / (pointCount - 1)

        return List(pointCount) { index ->
            val x = xMin + index * step

            GraphPoint(
                x = x,
                y = membership(x)
            )
        }
    }

    private fun validateAlpha(alpha: Double) {
        require(alpha > 0.0 && alpha <= 1.0) {
            "Alpha must be in the range (0, 1]"
        }
    }
}