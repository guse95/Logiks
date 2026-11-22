package com.example.logiks.clustering

import com.example.logiks.fuzzy.FuzzyNumber
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.sqrt
import kotlin.random.Random

class FuzzyClusterer {

    fun cluster(
        values: List<FuzzyNumber>,
        alpha: Double,
        clusterCount: Int
    ): List<Cluster> {

        require(values.isNotEmpty()) {
            "Values list must not be empty"
        }

        require(alpha > 0.0 && alpha <= 1.0) {
            "Alpha must be in the range (0, 1]"
        }

        require(clusterCount in 2..values.size) {
            "Cluster count must be between 2 and number of values"
        }

        val alphaCuts = values.map {
            toAlphaCut(it, alpha)
        }

        var centers = initializeCenters(
            alphaCuts,
            clusterCount
        )

        repeat(100) {

            val assignments = assignToClusters(
                alphaCuts,
                centers
            )

            val newCenters = calculateCenters(
                alphaCuts,
                assignments,
                clusterCount
            )

            if (centers == newCenters) {
                return buildClusters(
                    values,
                    assignments,
                    newCenters,
                    alpha
                )
            }

            centers = newCenters
        }

        val assignments = assignToClusters(
            alphaCuts,
            centers
        )

        return buildClusters(
            values,
            assignments,
            centers,
            alpha
        )
    }

    private fun toAlphaCut(
        value: FuzzyNumber,
        alpha: Double
    ): AlphaCut {

        return AlphaCut(
            left = value.alphaCutLeft(alpha),
            right = value.alphaCutRight(alpha)
        )
    }

    private fun initializeCenters(
        alphaCuts: List<AlphaCut>,
        clusterCount: Int
    ): List<AlphaCut> {

        return alphaCuts
            .shuffled(Random)
            .take(clusterCount)
    }

    private fun assignToClusters(
        values: List<AlphaCut>,
        centers: List<AlphaCut>
    ): List<Int> {

        return values.map { value ->

            centers.indices.minByOrNull { index ->
                value.distanceTo(centers[index])
            } ?: 0
        }
    }

    private fun calculateCenters(
        values: List<AlphaCut>,
        assignments: List<Int>,
        clusterCount: Int
    ): List<AlphaCut> {

        return List(clusterCount) { clusterIndex ->

            val clusterValues = values.filterIndexed { index, _ ->
                assignments[index] == clusterIndex
            }

            if (clusterValues.isEmpty()) {

                values.random()

            } else {

                AlphaCut(
                    left = clusterValues.map { it.left }.average(),
                    right = clusterValues.map { it.right }.average()
                )
            }
        }
    }

    private fun buildClusters(
        values: List<FuzzyNumber>,
        assignments: List<Int>,
        centers: List<AlphaCut>,
        alpha: Double
    ): List<Cluster> {

        return centers.indices.map { clusterIndex ->

            val clusterValues =
                values.filterIndexed { index, _ ->
                    assignments[index] == clusterIndex
                }

            Cluster(
                id = clusterIndex + 1,
                center = alphaCutToFuzzyNumber(
                    centers[clusterIndex],
                    alpha
                ),
                values = clusterValues
            )
        }
    }

    private fun alphaCutToFuzzyNumber(
        alphaCut: AlphaCut,
        alpha: Double
    ): FuzzyNumber {

        if (alpha == 1.0) {
            return FuzzyNumber(
                mu = ln(alphaCut.left),
                sigma = 0.0001
            )
        }

        val sqrtTerm =
            sqrt(-2.0 * ln(alpha))

        val mu =
            (ln(alphaCut.left) + ln(alphaCut.right)) / 2.0

        val sigma =
            (ln(alphaCut.right) - ln(alphaCut.left)) /
                    (2.0 * sqrtTerm)

        return FuzzyNumber(
            mu = mu,
            sigma = sigma.coerceAtLeast(0.0001)
        )
    }
}