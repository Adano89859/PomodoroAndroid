package com.example.pomodoro.ui.stats.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pomodoro.data.model.DailyStats

@Composable
fun BarChart(
    data: List<DailyStats>,
    modifier: Modifier = Modifier,
    barColor: Color = MaterialTheme.colorScheme.primary
) {
    if (data.isEmpty()) {
        Box(
            modifier = modifier.height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Sin datos para mostrar")
        }
        return
    }

    val maxValue = data.maxOfOrNull { it.pomodorosCompleted } ?: 1
    val chartHeight = 180f

    Column(modifier = modifier.fillMaxWidth()) {
        // Gráfico
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(horizontal = 16.dp)
        ) {
            val barWidth = (size.width / data.size) * 0.6f
            val spacing = (size.width / data.size) * 0.4f

            data.forEachIndexed { index, stat ->
                val barHeight = if (maxValue > 0) {
                    (stat.pomodorosCompleted.toFloat() / maxValue) * chartHeight
                } else 0f

                val x = index * (barWidth + spacing) + spacing / 2
                val y = size.height - barHeight - 40f

                // Dibujar barra
                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(8f, 8f)
                )

                // Valor encima de la barra
                if (stat.pomodorosCompleted > 0) {
                    val paint = android.graphics.Paint().apply {
                        color = barColor.toArgb()
                        textSize = 32f
                        textAlign = android.graphics.Paint.Align.CENTER
                        isFakeBoldText = true
                    }

                    drawContext.canvas.nativeCanvas.drawText(
                        "${stat.pomodorosCompleted}",
                        x + barWidth / 2,
                        y - 10f,
                        paint
                    )
                }
            }
        }

        // Labels de fechas
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            data.forEach { stat ->
                Text(
                    text = stat.date.takeLast(5), // MM-DD
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}