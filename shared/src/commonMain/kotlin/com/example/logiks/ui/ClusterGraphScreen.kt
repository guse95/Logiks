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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.logiks.clustering.Cluster
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
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.round

import com.patrykandpatrick.vico.compose.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.Fill

import com.patrykandpatrick.vico.compose.cartesian.CartesianDrawingContext
import com.patrykandpatrick.vico.compose.cartesian.decoration.Decoration
import com.patrykandpatrick.vico.compose.common.Position
import com.patrykandpatrick.vico.compose.common.component.TextComponent

@Composable
fun ClusterGraphScreen(
    clusters: List<Cluster>,
    alpha: Double,
    modifier: Modifier = Modifier
) {
    if (clusters.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Нет данных кластеризации")
        }

        return
    }

    val modelProducer = remember {
        CartesianChartModelProducer()
    }

    val allValues = remember(clusters) {
        clusters.flatMap { it.values }
    }

    val globalXMin = remember(allValues) {
        allValues.minOf {
            exp(it.mu - 3.0 * it.sigma)
        }
    }

    val globalXMax = remember(allValues) {
        allValues.maxOf {
            exp(it.mu + 3.0 * it.sigma)
        }
    }

    val xValues = remember(
        allValues,
        globalXMin,
        globalXMax
    ) {
        createLogarithmicXValues(
            minX = globalXMin,
            maxX = globalXMax,
            count = 300
        )
    }

    val lineProvider = LineCartesianLayer.LineProvider.series(
        clusters.flatMap { cluster ->
            cluster.values.map {
                LineCartesianLayer.rememberLine(
                    fill = LineCartesianLayer.LineFill.single(
                        Fill(getClusterColor(cluster.id))
                    ),
                    interpolator = LineCartesianLayer.Interpolator.Sharp
                )
            }
        }
    )

    LaunchedEffect(clusters, xValues) {
        modelProducer.runTransaction {
            lineModel {

                clusters.forEach { cluster ->

                    cluster.values.forEachIndexed { index, fuzzyNumber ->

                        val yValues = xValues.map { x ->
                            fuzzyNumber.membership(x)
                        }

                        series(
                            x = xValues,
                            y = yValues,
                            key = cluster.id * 10_000 + index
                        )
                    }
                }
            }
        }
    }

    val lineLayer = rememberLineCartesianLayer(lineProvider = lineProvider)

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
        ),
        decorations = listOf(
            AlphaCutDecoration(
                clusters = clusters,
                alpha = alpha
            )
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
        chartAreaHeight = 600.dp
    )
}

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

        val t =
            index.toDouble() / (count - 1)

        val logX =
            minLog + (maxLog - minLog) * t

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

private val clusterColors = listOf(
    Color(0xFFE53935),
    Color(0xFF1E88E5),
    Color(0xFF43A047),
    Color(0xFFFB8C00),
    Color(0xFF8E24AA),
    Color(0xFF00ACC1),
    Color(0xFFD81B60),
    Color(0xFF6D4C41),
    Color(0xFF546E7A),
    Color(0xFF7CB342)
)

private fun getClusterColor(clusterId: Int): Color {
    return clusterColors[
        (clusterId - 1) % clusterColors.size
    ]
}

private class AlphaCutDecoration(
    private val clusters: List<Cluster>,
    private val alpha: Double
) : Decoration {

    private val labelComponent =
        TextComponent(
            textStyle = TextStyle(
                fontSize = 11.sp
            )
        )

    override fun drawOverLayers(
        context: CartesianDrawingContext
    ) {
        with(context) {

            val bounds = layerBounds

            fun xToPixel(x: Double): Float {

                val drawingStart =
                    bounds.left +
                            layoutDirectionMultiplier *
                            layerDimensions.startPadding -
                            scroll

                return drawingStart +
                        layoutDirectionMultiplier *
                        layerDimensions.xSpacing *
                        (
                                (x - ranges.minX) /
                                        ranges.xStep
                                ).toFloat()
            }

            if (clusters.isEmpty()) {
                return
            }

            /*
             * Нижняя область графика,
             * где будут располагаться alpha-cut линии.
             */
            val bottomAreaHeight =
                (clusters.size * 24).dp.pixels

            val clusterSpacing =
                bottomAreaHeight /
                        clusters.size.coerceAtLeast(1)

            val drawingContext = this

            clusters.forEachIndexed { index, cluster ->

                if (cluster.values.isEmpty()) {
                    return@forEachIndexed
                }

                /*
                 * Общий alpha-cut всего кластера:
                 *
                 * левая граница =
                 * минимальная левая граница
                 * всех элементов кластера.
                 *
                 * правая граница =
                 * максимальная правая граница
                 * всех элементов кластера.
                 */
                val clusterLeft =
                    cluster.values.minOf { fuzzyNumber ->
                        fuzzyNumber.alphaCutLeft(alpha)
                    }

                val clusterRight =
                    cluster.values.maxOf { fuzzyNumber ->
                        fuzzyNumber.alphaCutRight(alpha)
                    }

                val leftPixel =
                    xToPixel(clusterLeft)

                val rightPixel =
                    xToPixel(clusterRight)

                /*
                 * Если весь alpha-cut находится
                 * за пределами видимой области,
                 * ничего не рисуем.
                 */
                if (
                    rightPixel < bounds.left ||
                    leftPixel > bounds.right
                ) {
                    return@forEachIndexed
                }

                /*
                 * Обрезаем линию границами
                 * видимой области графика.
                 */
                val visibleLeft =
                    leftPixel.coerceIn(
                        bounds.left,
                        bounds.right
                    )

                val visibleRight =
                    rightPixel.coerceIn(
                        bounds.left,
                        bounds.right
                    )

                /*
                 * Каждому кластеру соответствует
                 * свой уровень Y.
                 *
                 * Чем больше cluster.id,
                 * тем ниже располагается линия.
                 */
                val y =
                    bounds.bottom -
                            bottomAreaHeight +
                            clusterSpacing *
                            (index + 1)

                with(mutableDrawScope) {

                    /*
                     * Alpha-cut линия кластера.
                     */
                    drawLine(
                        color = getClusterColor(cluster.id),
                        start = Offset(
                            x = visibleLeft,
                            y = y
                        ),
                        end = Offset(
                            x = visibleRight,
                            y = y
                        ),
                        strokeWidth = 8f,
                        cap = StrokeCap.Round
                    )

                    /*
                     * Подпись кластера.
                     */
                    labelComponent.draw(
                        context = drawingContext,
                        text = "Cluster ${cluster.id}",
                        x = visibleRight + 8.dp.pixels,
                        y = y,
                        horizontalPosition = Position.Horizontal.Start,
                        verticalPosition = Position.Vertical.Center
                    )
                }
            }
        }
    }
}