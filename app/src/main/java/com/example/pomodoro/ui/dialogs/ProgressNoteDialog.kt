package com.example.pomodoro.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ProgressNoteDialog(
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var noteText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text("¡Sesión completada!")
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "¿Qué lograste en esta sesión?",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "Cuéntanos tu progreso. Mientras más detalles, más tiempo bonus en tu descanso 🎁",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    placeholder = { Text("Ej: Completé el diseño de la interfaz principal...") },
                    maxLines = 5
                )

                // Indicador de bonus
                if (noteText.isNotEmpty()) {
                    val bonusTime = calculateBonusTime(noteText.length)
                    if (bonusTime > 0) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "⏰ +$bonusTime segundos de descanso bonus",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(noteText)
                },
                enabled = noteText.isNotBlank()
            ) {
                Text("Guardar progreso")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onSave("") // Guardar vacío si salta
            }) {
                Text("Saltar")
            }
        }
    )
}

// Función para calcular bonus de tiempo
private fun calculateBonusTime(textLength: Int): Int {
    return when {
        textLength < 20 -> 0 // Muy poco texto, sin bonus
        textLength < 50 -> 30 // Texto corto, 30 segundos
        textLength < 100 -> 60 // Texto medio, 1 minuto
        textLength < 200 -> 90 // Texto largo, 1.5 minutos
        else -> 120 // Texto muy detallado, 2 minutos
    }
}