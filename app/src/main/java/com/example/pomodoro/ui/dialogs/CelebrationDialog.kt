package com.example.pomodoro.ui.dialogs

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pomodoro.data.model.SessionType
import kotlinx.coroutines.delay

@Composable
fun CelebrationDialog(
    sessionType: SessionType,
    onDismiss: () -> Unit
) {
    // Animación de escala para el emoji
    val infiniteTransition = rememberInfiniteTransition(label = "celebration")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    // Auto-cerrar después de 3 segundos
    LaunchedEffect(Unit) {
        delay(3000)
        onDismiss()
    }

    val (emoji, title, message) = when (sessionType) {
        SessionType.WORK -> Triple(
            "🎉",
            "¡Excelente trabajo!",
            "Completaste una sesión de concentración. ¡Sigue así!"
        )
        SessionType.SHORT_BREAK -> Triple(
            "☕",
            "¡Descanso completado!",
            "Recargaste energías. ¡Hora de volver al trabajo!"
        )
        SessionType.LONG_BREAK -> Triple(
            "🌟",
            "¡Descanso profundo completado!",
            "Estás listo para un nuevo ciclo. ¡Tú puedes!"
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = emoji,
                    fontSize = 72.sp,
                    modifier = Modifier.scale(scale)
                )

                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("¡Genial!")
            }
        }
    )
}