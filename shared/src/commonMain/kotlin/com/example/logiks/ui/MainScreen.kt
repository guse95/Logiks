package com.example.logiks.ui

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.logiks.clustering.Cluster
import com.example.logiks.fuzzy.FuzzyNumberGenerator
import com.example.logiks.fuzzy.FuzzyNumber
import com.example.logiks.clustering.FuzzyClusterer

@Composable
fun MainScreen() {

    var alphaText by remember {
        mutableStateOf("0.7")
    }

    var valuesCountText by remember {
        mutableStateOf("200")
    }

    var clustersText by remember {
        mutableStateOf("3")
    }

    var seedText by remember {
        mutableStateOf("12345")
    }

    var values by remember {
        mutableStateOf<List<FuzzyNumber>>(emptyList())
    }

    var clusters by remember {
        mutableStateOf<List<Cluster>>(emptyList())
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    var showGraph by remember {
        mutableStateOf(false)
    }

    var showClusterGraph by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(state = rememberScrollState()),
        verticalArrangement = Arrangement.Top
    ) {

        Text(
            text = "Logiks"
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        OutlinedTextField(
            value = valuesCountText,
            onValueChange = {
                valuesCountText = it
                errorMessage = ""
            },
            label = {
                Text("Number of values (100–300)")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        OutlinedTextField(
            value = seedText,
            onValueChange = {
                seedText = it
                errorMessage = ""
            },
            label = {
                Text("Seed")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        OutlinedTextField(
            value = alphaText,
            onValueChange = {
                alphaText = it
                errorMessage = ""
            },
            label = {
                Text("Alpha")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        OutlinedTextField(
            value = clustersText,
            onValueChange = {
                clustersText = it
                errorMessage = ""
            },
            label = {
                Text("Number of clusters")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Button(
                onClick = {
                    try {
                        val alpha = alphaText.toDouble()
                        val count = valuesCountText.toInt()
                        val seed = seedText.toLong()

                        require(alpha > 0.0 && alpha <= 1.0) {
                            "Alpha must be between 0 and 1"
                        }

//                        require(count in 100..300) {
//                            "Number of values must be between 100 and 300"
//                        }

                        values = FuzzyNumberGenerator.generate(
                            count = count,
                            seed = seed
                        )

                        clusters = emptyList()
                        errorMessage = ""
                        showGraph = true

                    } catch (e: Exception) {
                        errorMessage =
                            e.message ?: "Invalid input"
                    }
                }
            ) {
                Text("Generate values")
            }

            Button(
                onClick = {
                    try {
                        if (values.isEmpty()) {
                            errorMessage = "Сначала сгенерируйте значения"
                            return@Button
                        }

                        val alpha = alphaText.toDouble()
                        val clusterCount = clustersText.toInt()

                        require(alpha > 0.0 && alpha <= 1.0) {
                            "Alpha must be between 0 and 1"
                        }

                        require(clusterCount in 2..values.size) {
                            "Cluster count must be between 2 and number of values"
                        }

                        val clusterer = FuzzyClusterer()

                        clusters = clusterer.cluster(
                            values = values,
                            alpha = alpha,
                            clusterCount = clusterCount
                        )

                        errorMessage = ""
                        showClusterGraph = true

                    } catch (e: Exception) {
                        errorMessage = e.message ?: "Clustering error"
                    }
                }
            ) {
                Text("Start clustering")
            }
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        if (values.isNotEmpty()) {
            Text(
                text = "Generated values: ${values.size}"
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Alpha: $alphaText"
            )

            Text(
                text = "Clusters: $clustersText"
            )

//            for ((mu, sig) in fuzzyNumbers) {
//                Text(text = "Mu: $mu, sigma: $sig")
//            }
    }

        if (clusters.isNotEmpty()) {

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Text(
                text = "Clustering result"
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            clusters.forEach { cluster ->

                Text(
                    text = buildString {
                        append("Cluster ${cluster.id}: ")
                        append("${cluster.values.size} values, ")
                        append("μ = ")
                        append("%.3f".format(cluster.center.mu))
                        append(", σ = ")
                        append("%.3f".format(cluster.center.sigma))
                    }
                )
            }
        }

        if (errorMessage.isNotEmpty()) {
            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = errorMessage
            )
        }

        if (showGraph) {
            Dialog(
                onDismissRequest = {
                    showGraph = false
                }
            ) {
                Surface(
                    modifier = Modifier
                        .width(1000.dp)
                        .height(700.dp),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    start = 16.dp,
                                    top = 12.dp,
                                    end = 16.dp
                                ),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            Text(
                                text = "Fuzzy quantities graph"
                            )

                            Button(
                                onClick = {
                                    showGraph = false
                                }
                            ) {
                                Text("Close")
                            }
                        }

                        GraphScreen(
                            values = values,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )
                    }
                }
            }
        }

        if (showClusterGraph) {
            Dialog(
                onDismissRequest = {
                    showClusterGraph = false
                }
            ) {
                Surface(
                    modifier = Modifier
                        .width(1000.dp)
                        .height(700.dp),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    start = 16.dp,
                                    top = 12.dp,
                                    end = 16.dp
                                ),
                            horizontalArrangement =
                                Arrangement.SpaceBetween
                        ) {

                            Text(
                                text = "Clustered fuzzy quantities"
                            )

                            Button(
                                onClick = {
                                    showClusterGraph = false
                                }
                            ) {
                                Text("Close")
                            }
                        }

                        ClusterGraphScreen(
                            clusters = clusters,
                            alpha = alphaText.toDouble(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )
                    }
                }
            }
        }
    }
}