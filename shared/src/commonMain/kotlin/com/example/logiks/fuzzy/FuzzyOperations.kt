package com.example.logiks.fuzzy

object FuzzyOperations {

    fun add(
        a: FuzzyNumber,
        b: FuzzyNumber
    ): FuzzyNumber {
        return FuzzyNumber(
            mu = a.mu + b.mu,
            sigma = a.sigma + b.sigma
        )
    }

    fun subtract(
        a: FuzzyNumber,
        b: FuzzyNumber
    ): FuzzyNumber {
        return FuzzyNumber(
            mu = a.mu - b.mu,
            sigma = kotlin.math.abs(a.sigma - b.sigma)
                .coerceAtLeast(0.0001)
        )
    }

    fun multiply(
        a: FuzzyNumber,
        b: FuzzyNumber
    ): FuzzyNumber {
        return FuzzyNumber(
            mu = a.mu + b.mu,
            sigma = kotlin.math.sqrt(
                a.sigma * a.sigma +
                        b.sigma * b.sigma
            )
        )
    }

    fun divide(
        a: FuzzyNumber,
        b: FuzzyNumber
    ): FuzzyNumber {
        return FuzzyNumber(
            mu = a.mu - b.mu,
            sigma = kotlin.math.sqrt(
                a.sigma * a.sigma +
                        b.sigma * b.sigma
            )
        )
    }
}