package com.example.logiks.fuzzy

import kotlin.random.Random

object FuzzyNumberGenerator {

    /**
     * Generates a random number of fuzzy quantities
     * from 100 to 300.
     */
    fun generate(): List<FuzzyNumber> {
//        val count = Random.nextInt(100, 301)
        val count = 200
        val seed = 12345L

        return generate(count, seed)
    }

    /**
     * Generates exactly [count] fuzzy quantities.
     */
    fun generate(
        count: Int,
        seed: Long = 67,
        muRange: ClosedFloatingPointRange<Double> = 0.5..5.0,
        sigmaRange: ClosedFloatingPointRange<Double> = 0.1..1.5
    ): List<FuzzyNumber> {

//        require(count in 100..300) {
//            "Count must be between 100 and 300"
//        }

        require(sigmaRange.start > 0.0) {
            "Sigma range must be greater than zero"
        }

        val random = Random(seed)

        return List(count) {
            FuzzyNumber(
                mu = random.nextDouble(
                    muRange.start,
                    muRange.endInclusive
                ),

                sigma = random.nextDouble(
                    sigmaRange.start,
                    sigmaRange.endInclusive
                )
            )
        }
    }
}