package com.example.pomodoro.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pomodoro.data.model.MusicTrack
import com.example.pomodoro.data.model.SessionType
import com.example.pomodoro.utils.MusicCatalog

@Composable
fun MusicSelectorDialog(
    sessionType: SessionType,
    currentTrackId: Int,  // ← CAMBIADO de String a Int
    onDismiss: () -> Unit,
    onTrackSelected: (Int) -> Unit,  // ← CAMBIADO de String a Int
    onPreviewTrack: (Int) -> Unit  // ← CAMBIADO de String a Int
) {
    val tracks = MusicCatalog.getTracksByType(sessionType)  // ← CAMBIADO nombre del método

    val title = when (sessionType) {
        SessionType.WORK -> "Música para Trabajo"
        SessionType.SHORT_BREAK -> "Música para Descanso Corto"
        SessionType.LONG_BREAK -> "Música para Descanso Largo"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tracks) { track ->
                    MusicTrackItem(
                        track = track,
                        isSelected = track.id == currentTrackId,
                        onSelect = {
                            onTrackSelected(track.id)
                            onDismiss()
                        },
                        onPreview = { onPreviewTrack(track.id) }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

@Composable
fun MusicTrackItem(
    track: MusicTrack,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onPreview: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = if (isSelected) {
                        Icons.Default.CheckCircle
                    } else {
                        Icons.Default.MusicNote
                    },
                    contentDescription = null,
                    tint = if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )

                Column {
                    Text(
                        text = track.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                    Text(
                        text = track.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(onClick = onPreview) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = "Vista previa",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}