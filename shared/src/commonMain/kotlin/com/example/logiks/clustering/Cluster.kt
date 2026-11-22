package com.example.logiks.clustering

import com.example.logiks.fuzzy.FuzzyNumber

data class Cluster(
    val id: Int,
    val center: FuzzyNumber,
    val values: List<FuzzyNumber>
)