package com.example.pomodoro.ui.shop

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
import com.example.pomodoro.ui.timer.PomodoroViewModel
import com.example.pomodoro.utils.MusicCatalog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopScreen(
    viewModel: PomodoroViewModel,
    onNavigateBack: () -> Unit
) {
    val userCoins by viewModel.userCoins.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("🎯 Trabajo", "☁️ Descanso Corto", "😌 Descanso Largo")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🛒 Tienda de Música") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                actions = {
                    // Mostrar monedas disponibles
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "🪙",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "$userCoins",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Tabs para categorías
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            // Lista de canciones según la categoría
            val sessionType = when (selectedTab) {
                0 -> SessionType.WORK
                1 -> SessionType.SHORT_BREAK
                else -> SessionType.LONG_BREAK
            }

            val tracks = MusicCatalog.getTracksByType(sessionType)

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(tracks) { track ->
                    MusicTrackShopItem(
                        track = track,
                        userCoins = userCoins,
                        isUnlocked = track.price == 0, // TODO: Verificar con BD
                        onPurchase = {
                            // TODO: Implementar compra
                        },
                        onPreview = {
                            // TODO: Implementar preview
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MusicTrackShopItem(
    track: MusicTrack,
    userCoins: Int,
    isUnlocked: Boolean,
    onPurchase: () -> Unit,
    onPreview: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Info de la canción
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = track.emoji,
                    style = MaterialTheme.typography.headlineMedium
                )

                Column {
                    Text(
                        text = track.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = track.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Botón de preview
            IconButton(onClick = onPreview) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = "Preview",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // Estado: Gratis/Comprar/Desbloqueada
            if (isUnlocked) {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = MaterialTheme.shapes.small
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = "Desbloqueada",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            } else if (track.price == 0) {
                Text(
                    text = "GRATIS",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Button(
                    onClick = onPurchase,
                    enabled = userCoins >= track.price
                ) {
                    Text("🪙 ${track.price}")
                }
            }
        }
    }
}