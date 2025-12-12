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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pomodoro.data.model.MusicTrack
import com.example.pomodoro.data.model.SessionType
import com.example.pomodoro.ui.shop.ShopViewModel
import com.example.pomodoro.utils.MusicCatalog
import com.example.pomodoro.ui.importedmusic.ImportedMusicViewModel

@Composable
fun MusicSelectorDialog(
    sessionType: SessionType,
    currentTrackId: Int,
    onDismiss: () -> Unit,
    onTrackSelected: (Int) -> Unit,
    onPreviewTrack: (Int) -> Unit,
    onNavigateToShop: (() -> Unit)? = null
) {
    val shopViewModel: ShopViewModel = viewModel()
    val importedMusicViewModel: ImportedMusicViewModel = viewModel()  // ← NUEVO

    val unlockedMusicIds by shopViewModel.unlockedMusicIds.collectAsState(initial = emptyList())
    val previewingTrackId by shopViewModel.previewingTrackId.collectAsState()

    // ← NUEVO: Obtener música importada comprada del tipo correspondiente
    val allImportedMusic by importedMusicViewModel.allImportedMusic.collectAsState()
    val purchasedImportedMusic = allImportedMusic.filter {
        it.isPurchased && it.sessionType == sessionType
    }

    val allTracks = MusicCatalog.getTracksByType(sessionType)
    val unlockedTracks = allTracks.filter { it.id in unlockedMusicIds }

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
            if (unlockedTracks.isEmpty() && purchasedImportedMusic.isEmpty()) {  // ← ACTUALIZADO
                // Mensaje si no hay canciones desbloqueadas
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "No tienes canciones desbloqueadas",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Visita la tienda para desbloquear más música",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    onNavigateToShop?.let { navigate ->
                        Button(
                            onClick = {
                                onDismiss()
                                navigate()
                            }
                        ) {
                            Icon(Icons.Default.ShoppingCart, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Ir a la Tienda")
                        }
                    }
                }
            } else {
                // Lista de canciones desbloqueadas
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // ← NUEVO: Mostrar música importada primero
                    if (purchasedImportedMusic.isNotEmpty()) {
                        item {
                            Text(
                                text = "🎵 Mis Canciones",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }

                    items(purchasedImportedMusic) { importedMusic ->
                        ImportedMusicTrackItem(
                            music = importedMusic,
                            isSelected = currentTrackId == -importedMusic.id, // Negativo para distinguir
                            onSelect = {
                                onTrackSelected(-importedMusic.id)
                                onDismiss()
                            },
                            onPreview = {
                                importedMusicViewModel.playPreview(importedMusic)
                            }
                        )
                    }

                    // Separador si hay ambos tipos
                    if (purchasedImportedMusic.isNotEmpty() && unlockedTracks.isNotEmpty()) {
                        item {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                            Text(
                                text = "🎼 Música de la Tienda",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }

                    items(unlockedTracks) { track ->
                        MusicTrackItem(
                            track = track,
                            isSelected = track.id == currentTrackId,
                            isPreviewing = track.id == previewingTrackId,
                            onSelect = {
                                onTrackSelected(track.id)
                                onDismiss()
                            },
                            onPreview = {
                                shopViewModel.playPreview(track.id)
                            }
                        )
                    }

                    // Botón para ver más en la tienda
                    item {
                        onNavigateToShop?.let { navigate ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onDismiss()
                                        navigate()
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.ShoppingCart,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "Ver más en la tienda",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
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

// ← NUEVO: Item para música importada
@Composable
fun ImportedMusicTrackItem(
    music: com.example.pomodoro.data.model.ImportedMusic,
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
                MaterialTheme.colorScheme.tertiaryContainer
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
                        MaterialTheme.colorScheme.tertiary
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )

                Column {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = music.displayName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                        Text(
                            text = "🎵",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    Text(
                        text = "Mi música",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }

            IconButton(onClick = onPreview) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = "Vista previa",
                    tint = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}

@Composable
fun MusicTrackItem(
    track: MusicTrack,
    isSelected: Boolean,
    isPreviewing: Boolean,  // ← NUEVO
    onSelect: () -> Unit,
    onPreview: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isPreviewing -> MaterialTheme.colorScheme.tertiaryContainer  // ← NUEVO
                isSelected -> MaterialTheme.colorScheme.primaryContainer
                else -> MaterialTheme.colorScheme.surface
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
                    if (isPreviewing) Icons.Default.Stop else Icons.Default.PlayArrow,  // ← NUEVO
                    contentDescription = if (isPreviewing) "Detener" else "Vista previa",
                    tint = if (isPreviewing) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}