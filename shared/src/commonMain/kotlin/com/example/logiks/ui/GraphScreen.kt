package com.example.logiks.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.logiks.fuzzy.FuzzyNumber
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.Zoom
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisGuidelineComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.lineModel
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.round

@Composable
fun GraphScreen(
    values: List<FuzzyNumber>,
    modifier: Modifier = Modifier
) {
    if (values.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Нет данных для отображения")
        }

        return
    }

    val modelProducer = remember {
        CartesianChartModelProducer()
    }

    /*
     * Находим общий диапазон X.
     */
    val globalXMin = remember(values) {
        values.minOf {
            exp(it.mu - 3.0 * it.sigma)
        }
    }

    val globalXMax = remember(values) {
        values.maxOf {
            exp(it.mu + 3.0 * it.sigma)
        }
    }

    /*
     * Создаём X в логарифмическом масштабе.
     *
     * Это важно для наших функций:
     * exp(mu - 3*sigma) ... exp(mu + 3*sigma)
     */
    val xValues = remember(
        values,
        globalXMin,
        globalXMax
    ) {
        createLogarithmicXValues(
            minX = globalXMin,
            maxX = globalXMax,
            count = 300
        )
    }

    LaunchedEffect(values, xValues) {

        modelProducer.runTransaction {

            lineModel {

                values.forEachIndexed { index, fuzzyNumber ->

                    val yValues = xValues.map { x ->
                        fuzzyNumber.membership(x)
                    }

                    series(
                        x = xValues,
                        y = yValues,
                        key = index
                    )
                }
            }
        }
    }

    val lineLayer = rememberLineCartesianLayer()

    /*
     * Координатные оси Vico 3.3.1.
     */
    val chart = rememberCartesianChart(
        lineLayer,
        startAxis = VerticalAxis.rememberStart(
            label = rememberAxisLabelComponent(
                style = TextStyle(
                    fontSize = 12.sp
                )
            ),
            guideline = rememberAxisGuidelineComponent()
        ),
        bottomAxis = HorizontalAxis.rememberBottom(
            label = rememberAxisLabelComponent(
                style = TextStyle(
                    fontSize = 12.sp
                )
            ),
            guideline = rememberAxisGuidelineComponent()
        )
    )

    val scrollState = rememberVicoScrollState(
        scrollEnabled = true
    )

    val zoomState = rememberVicoZoomState(
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

        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),

        chartAreaHeight = 450.dp
    )
}

/**
 * Создаёт точки X с равномерным шагом
 * в логарифмическом пространстве.
 *
 * Например:
 *
 * 1
 * 1.5
 * 2.25
 * 3.37
 * 5.06
 * ...
 */
private fun createLogarithmicXValues(
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

        val t = index.toDouble() / (count - 1)

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
return round(value * 10_000.0) / 10_000.0
}