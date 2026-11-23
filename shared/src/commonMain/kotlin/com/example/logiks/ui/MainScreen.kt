package com.example.logiks.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.logiks.clustering.Cluster
import com.example.logiks.clustering.FuzzyClusterer
import com.example.logiks.fuzzy.FuzzyNumber
import com.example.logiks.fuzzy.FuzzyNumberGenerator

@Composable
fun MainScreen(
    modifier: Modifier = Modifier
) {
    var selectedTab by remember {
        mutableStateOf(0)
    }

    var seedText by remember {
        mutableStateOf("12345")
    }

    var countText by remember {
        mutableStateOf("100")
    }

    var alphaText by remember {
        mutableStateOf("0.7")
    }

    var clusterCountText by remember {
        mutableStateOf("3")
    }

    var generatedValues by remember {
        mutableStateOf(
            FuzzyNumberGenerator.generate(
                count = 100,
                seed = 12345L
            )
        )
    }

    var clusters by remember {
        mutableStateOf<List<Cluster>>(
            emptyList()
        )
    }

    var generationError by remember {
        mutableStateOf<String?>(null)
    }

    var clusteringError by remember {
        mutableStateOf<String?>(null)
    }

    var alpha: Double

    val tabs = listOf(
        "График",
        "Кластеры",
        "Операции"
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {

            Column {

                TopAppBar(
                    title = {
                        Text("Logiks")
                    }
                )

                TabRow(
                    selectedTabIndex = selectedTab
                ) {

                    tabs.forEachIndexed {
                            index,
                            title ->

                        Tab(
                            selected = selectedTab == index,
                            onClick = {
                                selectedTab = index
                            },
                            text = {
                                Text(title)
                            }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->

        when (selectedTab) {

            0 -> {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                ) {

                    OutlinedTextField(
                        value = seedText,
                        onValueChange = {
                            seedText = it
                            generationError = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text("Seed")
                        },
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = countText,
                        onValueChange = {
                            countText = it
                            generationError = null
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        label = {
                            Text("Количество величин")
                        },
                        singleLine = true
                    )

                    Button(
                        onClick = {

                            val seed =
                                seedText.toLongOrNull()

                            val count =
                                countText.toIntOrNull()

                            if (seed == null) {

                                generationError =
                                    "Seed должен быть целым числом"

                                return@Button
                            }

                            if (
                                count == null ||
                                count !in 100..300
                            ) {

                                generationError =
                                    "Количество должно быть от 100 до 300"

                                return@Button
                            }

                            generatedValues =
                                FuzzyNumberGenerator.generate(
                                    count = count,
                                    seed = seed
                                )

                            clusters =
                                emptyList()

                            generationError =
                                null
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                    ) {
                        Text("Сгенерировать")
                    }

                    generationError?.let {

                        Text(
                            text = it,
                            color =
                                MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(
                                top = 8.dp
                            )
                        )
                    }

                    GraphScreen(
                        values = generatedValues,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                }
            }

            1 -> {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                ) {

                    OutlinedTextField(
                        value = alphaText,
                        onValueChange = {
                            alphaText = it
                            clusteringError = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text("Alpha")
                        },
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = clusterCountText,
                        onValueChange = {
                            clusterCountText = it
                            clusteringError = null
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        label = {
                            Text("Количество кластеров")
                        },
                        singleLine = true
                    )

                    Button(
                        onClick = {

                            alpha = alphaText.toDoubleOrNull() ?: 0.7

                            val clusterCount =
                                clusterCountText.toIntOrNull()

                            if (
                                alpha == null ||
                                alpha <= 0.0 ||
                                alpha > 1.0
                            ) {

                                clusteringError =
                                    "Alpha должна быть в диапазоне (0, 1]"

                                return@Button
                            }

                            if (
                                clusterCount == null ||
                                clusterCount < 2 ||
                                clusterCount > generatedValues.size
                            ) {

                                clusteringError =
                                    "Количество кластеров должно быть от 2 до ${generatedValues.size}"

                                return@Button
                            }

                            clusters =
                                FuzzyClusterer().cluster(
                                    values =
                                        generatedValues,
                                    alpha = alpha,
                                    clusterCount =
                                        clusterCount
                                )

                            clusteringError =
                                null
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                    ) {
                        Text("Разбить на кластеры")
                    }

                    clusteringError?.let {

                        Text(
                            text = it,
                            color =
                                MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(
                                top = 8.dp
                            )
                        )
                    }

                    ClusterGraphScreen(
                        clusters = clusters,
                        alpha =
                            alphaText.toDoubleOrNull()
                                ?: 0.5,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                }
            }

            2 -> {

                FuzzyOperationsScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
        }
    }
}