package com.example.logiks.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.logiks.fuzzy.FuzzyNumber
import com.example.logiks.fuzzy.FuzzyOperations
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.Zoom
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisGuidelineComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.lineModel
import com.patrykandpatrick.vico.compose.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.Fill
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.round

private enum class FuzzyOperation(
    val symbol: String,
    val nameOp: String
) {
    ADD("+", "Сложение"),
    SUBTRACT("-", "Вычитание"),
    MULTIPLY("×", "Умножение"),
    DIVIDE("÷", "Деление")
}

@Composable
fun FuzzyOperationsScreen(
    modifier: Modifier = Modifier
) {
    var mu1Text by remember { mutableStateOf("2.0") }
    var sigma1Text by remember { mutableStateOf("0.5") }

    var mu2Text by remember { mutableStateOf("3.0") }
    var sigma2Text by remember { mutableStateOf("0.7") }

    var operationIndex by remember {
        mutableStateOf(0)
    }

    var firstNumber by remember {
        mutableStateOf(
            FuzzyNumber(
                mu = 2.0,
                sigma = 0.5
            )
        )
    }

    var secondNumber by remember {
        mutableStateOf(
            FuzzyNumber(
                mu = 3.0,
                sigma = 0.7
            )
        )
    }

    var resultNumber by remember {
        mutableStateOf(
            FuzzyOperations.add(
                firstNumber,
                secondNumber
            )
        )
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    val operation =
        FuzzyOperation.entries[
            operationIndex.toInt()
        ]

    val tabs = listOf(
        FuzzyOperation.ADD,
        FuzzyOperation.SUBTRACT,
        FuzzyOperation.MULTIPLY,
        FuzzyOperation.DIVIDE
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement =
            Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "Операции над нечеткими величинами",
            style =
                MaterialTheme.typography.headlineSmall
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {

            OutlinedTextField(
                value = mu1Text,
                onValueChange = {
                    mu1Text = it
                    errorMessage = null
                },
                modifier = Modifier.weight(1f),
                label = {
                    Text("μ₁")
                },
                keyboardOptions =
                    KeyboardOptions(
                        keyboardType =
                            KeyboardType.Decimal
                    ),
                singleLine = true
            )

            OutlinedTextField(
                value = sigma1Text,
                onValueChange = {
                    sigma1Text = it
                    errorMessage = null
                },
                modifier = Modifier.weight(1f),
                label = {
                    Text("σ₁")
                },
                keyboardOptions =
                    KeyboardOptions(
                        keyboardType =
                            KeyboardType.Decimal
                    ),
                singleLine = true
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {

            OutlinedTextField(
                value = mu2Text,
                onValueChange = {
                    mu2Text = it
                    errorMessage = null
                },
                modifier = Modifier.weight(1f),
                label = {
                    Text("μ₂")
                },
                keyboardOptions =
                    KeyboardOptions(
                        keyboardType =
                            KeyboardType.Decimal
                    ),
                singleLine = true
            )

            OutlinedTextField(
                value = sigma2Text,
                onValueChange = {
                    sigma2Text = it
                    errorMessage = null
                },
                modifier = Modifier.weight(1f),
                label = {
                    Text("σ₂")
                },
                keyboardOptions =
                    KeyboardOptions(
                        keyboardType =
                            KeyboardType.Decimal
                    ),
                singleLine = true
            )
        }

        Text(
            text =
                "Операция: ${tabs[operationIndex]}",
            style =
                MaterialTheme.typography.titleMedium
        )

        TabRow(
            selectedTabIndex = operationIndex
        ) {

            tabs.forEachIndexed {
                    index,
                    title ->

                Tab(
                    selected = operationIndex == index,
                    onClick = {
                        operationIndex = index
                    },
                    text = {
                        Text(title.nameOp)
                    }
                )
            }
    }

        Button(
            onClick = {

                val mu1 =
                    mu1Text.toDoubleOrNull()

                val sigma1 =
                    sigma1Text.toDoubleOrNull()

                val mu2 =
                    mu2Text.toDoubleOrNull()

                val sigma2 =
                    sigma2Text.toDoubleOrNull()

                if (
                    mu1 == null ||
                    sigma1 == null ||
                    mu2 == null ||
                    sigma2 == null
                ) {
                    errorMessage =
                        "Введите корректные числовые значения"
                    return@Button
                }

                if (
                    sigma1 <= 0.0 ||
                    sigma2 <= 0.0
                ) {
                    errorMessage =
                        "σ должна быть больше нуля"
                    return@Button
                }

                val newFirst =
                    FuzzyNumber(
                        mu = mu1,
                        sigma = sigma1
                    )

                val newSecond =
                    FuzzyNumber(
                        mu = mu2,
                        sigma = sigma2
                    )

                val newResult =
                    when (operation) {

                        FuzzyOperation.ADD ->
                            FuzzyOperations.add(
                                newFirst,
                                newSecond
                            )

                        FuzzyOperation.SUBTRACT ->
                            FuzzyOperations.subtract(
                                newFirst,
                                newSecond
                            )

                        FuzzyOperation.MULTIPLY ->
                            FuzzyOperations.multiply(
                                newFirst,
                                newSecond
                            )

                        FuzzyOperation.DIVIDE ->
                            FuzzyOperations.divide(
                                newFirst,
                                newSecond
                            )
                    }

                firstNumber = newFirst
                secondNumber = newSecond
                resultNumber = newResult
                errorMessage = null
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Выполнить операцию"
            )
        }

        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error
            )
        }

        Text(
            text =
                "Результат: μ = ${
                    String.format(
                        "%.4f",
                        resultNumber.mu
                    )
                }, σ = ${
                    String.format(
                        "%.4f",
                        resultNumber.sigma
                    )
                }",
            style =
                MaterialTheme.typography.titleMedium
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        FuzzyOperationChart(
            first = firstNumber,
            second = secondNumber,
            result = resultNumber,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun FuzzyOperationChart(
    first: FuzzyNumber,
    second: FuzzyNumber,
    result: FuzzyNumber,
    modifier: Modifier = Modifier
) {
    val modelProducer =
        remember {
            CartesianChartModelProducer()
        }

    val allValues =
        remember(first, second, result) {
            listOf(
                first,
                second,
                result
            )
        }

    val minX =
        remember(allValues) {
            allValues.minOf {
                exp(
                    it.mu -
                            3.0 * it.sigma
                )
            }
        }

    val maxX =
        remember(allValues) {
            allValues.maxOf {
                exp(
                    it.mu +
                            3.0 * it.sigma
                )
            }
        }

    val xValues =
        remember(
            minX,
            maxX
        ) {
            createOperationXValues(
                minX = minX,
                maxX = maxX,
                count = 300
            )
        }

    val lineProvider =
        LineCartesianLayer.LineProvider.series(
            listOf(
                LineCartesianLayer.rememberLine(
                    fill =
                        LineCartesianLayer.LineFill.single(
                            Fill(
                                Color(0xFFE53935)
                            )
                        ),
                    interpolator =
                        LineCartesianLayer.Interpolator.Sharp
                ),
                LineCartesianLayer.rememberLine(
                    fill =
                        LineCartesianLayer.LineFill.single(
                            Fill(
                                Color(0xFF1E88E5)
                            )
                        ),
                    interpolator =
                        LineCartesianLayer.Interpolator.Sharp
                ),
                LineCartesianLayer.rememberLine(
                    fill =
                        LineCartesianLayer.LineFill.single(
                            Fill(
                                Color(0xFF43A047)
                            )
                        ),
                    interpolator =
                        LineCartesianLayer.Interpolator.Sharp
                )
            )
        )

    LaunchedEffect(
        first,
        second,
        result,
        xValues
    ) {
        modelProducer.runTransaction {
            lineModel {

                series(
                    x = xValues,
                    y =
                        xValues.map {
                            first.membership(it)
                        },
                    key = 1
                )

                series(
                    x = xValues,
                    y =
                        xValues.map {
                            second.membership(it)
                        },
                    key = 2
                )

                series(
                    x = xValues,
                    y =
                        xValues.map {
                            result.membership(it)
                        },
                    key = 3
                )
            }
        }
    }

    val lineLayer =
        rememberLineCartesianLayer(
            lineProvider =
                lineProvider
        )

    val chart =
        rememberCartesianChart(
            lineLayer,

            startAxis =
                VerticalAxis.rememberStart(
                    label =
                        rememberAxisLabelComponent(
                            style =
                                TextStyle(
                                    fontSize = 12.sp
                                )
                        ),
                    guideline =
                        rememberAxisGuidelineComponent()
                ),

            bottomAxis =
                HorizontalAxis.rememberBottom(
                    label =
                        rememberAxisLabelComponent(
                            style =
                                TextStyle(
                                    fontSize = 12.sp
                                )
                        ),
                    guideline =
                        rememberAxisGuidelineComponent()
                )
        )

    val scrollState =
        rememberVicoScrollState(
            scrollEnabled = true
        )

    val zoomState =
        rememberVicoZoomState(
            zoomEnabled = true,
            initialZoom = Zoom.Content,
            minZoom = Zoom.Content,
            maxZoom = Zoom.fixed(20f)
        )

    CartesianChartHost(
        chart = chart,
        modelProducer = modelProducer,
        scrollState = scrollState,
        zoomState = zoomState,
        modifier = modifier,
        chartAreaHeight = 450.dp
    )
}

private fun createOperationXValues(
    minX: Double,
    maxX: Double,
    count: Int
): List<Double> {
    require(minX > 0.0)
    require(maxX > minX)
    require(count >= 2)

    val minLog = ln(minX)
    val maxLog = ln(maxX)

    return List(count) { index ->

        val t =
            index.toDouble() /
                    (count - 1)

        val logX =
            minLog +
                    (maxLog - minLog) * t

        roundToFourDecimals(
            exp(logX)
        )
    }
}

private fun roundToFourDecimals(
    value: Double
): Double {
    return round(
        value * 10_000.0
    ) / 10_000.0
}