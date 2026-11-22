package com.example.logiks

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.sqrt

@Composable
fun GraphScreen() {
    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        // Размер Canvas в пикселях
        val width = size.width
        val height = size.height

        // Отступы от краёв
        val left = 60f
        val right = 20f
        val top = 20f
        val bottom = 40f

        // Область, в которой рисуем график
        val graphWidth = width - left - right
        val graphHeight = height - top - bottom

        // Диапазон X
        val xMin = 0.05
        val xMax = 8.0

        // Максимальная высота графика
        val yMax = 1.2

        // --------------------------------------------------
        // Оси
        // --------------------------------------------------

        drawLine(
            color = Color.Black,
            start = Offset(left, top),
            end = Offset(left, height - bottom),
            strokeWidth = 2f
        )

        drawLine(
            color = Color.Black,
            start = Offset(left, height - bottom),
            end = Offset(width - right, height - bottom),
            strokeWidth = 2f
        )

        // --------------------------------------------------
        // 100 логнормальных распределений
        // --------------------------------------------------

        val curveCount = 100

        for (curveIndex in 0 until curveCount) {

            // Немного меняем параметры каждой кривой
            val mu = -0.2 + curveIndex * 0.004
            val sigma = 0.35 + curveIndex * 0.002

            var previousPoint: Offset? = null

            // Количество точек на одной кривой
            val pointCount = 500

            for (i in 0..pointCount) {

                // Математическая координата X
                val x = xMin + (xMax - xMin) * i / pointCount

                // PDF логнормального распределения
                val y =
                    1.0 / (x * sigma * sqrt(2.0 * Math.PI)) *
                            exp(
                                -((ln(x) - mu) * (ln(x) - mu)) /
                                        (2.0 * sigma * sigma)
                            )

                // Перевод математических координат
                // в координаты Canvas
                val screenX =
                    left + ((x - xMin) / (xMax - xMin) * graphWidth).toFloat()

                val screenY =
                    height - bottom -
                            ((y / yMax) * graphHeight).toFloat()

                val currentPoint = Offset(
                    screenX,
                    screenY
                )

                // Соединяем текущую точку с предыдущей
                if (previousPoint != null) {
                    drawLine(
                        color = Color(
                            red = 0.2f,
                            green = 0.4f,
                            blue = 0.9f,
                            alpha = 1f
                        ),
                        start = previousPoint,
                        end = currentPoint,
                        strokeWidth = 1.5f
                    )
                }

                previousPoint = currentPoint
            }
        }
    }
}