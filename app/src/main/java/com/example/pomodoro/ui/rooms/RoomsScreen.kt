package com.example.pomodoro.ui.rooms

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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pomodoro.data.model.RoomCatalog
import com.example.pomodoro.data.model.RoomItem
import com.example.pomodoro.data.model.RoomType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomsScreen(
    onNavigateBack: () -> Unit
) {
    val roomsViewModel: RoomsViewModel = viewModel()
    val userCoins by roomsViewModel.userCoins.collectAsState()
    val purchasedItemIds by roomsViewModel.purchasedItemIds.collectAsState()
    val purchaseState by roomsViewModel.purchaseState.collectAsState()
    val roomProgressMap by roomsViewModel.roomProgressMap.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf(
        RoomType.GARDEN,
        RoomType.OFFICE,
        RoomType.BEDROOM
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🏡 Mis Habitaciones") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                actions = {
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
            // Tabs para habitaciones
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, roomType ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${roomType.emoji} ${roomType.displayName}")
                                val progress = roomProgressMap[roomType]
                                progress?.let {
                                    Text(
                                        text = "${it.percentage}%",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (it.isComplete) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    )
                }
            }

            val currentRoomType = tabs[selectedTab]
            val currentProgress = roomProgressMap[currentRoomType]

            // Barra de progreso
            currentProgress?.let { progress ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Progreso: ${progress.purchasedCount}/${progress.totalCount}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${progress.percentage}%",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = progress.percentage / 100f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = if (progress.isComplete) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                    )

                    if (progress.isComplete) {
                        Spacer(modifier = Modifier.height(8.dp))
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
                                Text("🎉", style = MaterialTheme.typography.headlineSmall)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "¡Habitación completada!",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }

            // Lista de objetos
            val items = RoomCatalog.getItemsByRoom(currentRoomType)

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items) { item ->
                    RoomItemCard(
                        item = item,
                        isPurchased = item.id in purchasedItemIds,
                        userCoins = userCoins,
                        onPurchase = {
                            roomsViewModel.purchaseItem(item.id)
                        }
                    )
                }
            }
        }
    }

    // Diálogos de compra
    when (val state = purchaseState) {
        is PurchaseState.Success -> {
            AlertDialog(
                onDismissRequest = { roomsViewModel.dismissPurchaseState() },
                icon = {
                    Text("🎉", style = MaterialTheme.typography.displayMedium)
                },
                title = { Text("¡Compra exitosa!") },
                text = { Text("Has adquirido: ${state.itemName}") },
                confirmButton = {
                    TextButton(onClick = { roomsViewModel.dismissPurchaseState() }) {
                        Text("Genial")
                    }
                }
            )
        }
        is PurchaseState.Error -> {
            AlertDialog(
                onDismissRequest = { roomsViewModel.dismissPurchaseState() },
                icon = {
                    Icon(Icons.Default.Error, null, tint = MaterialTheme.colorScheme.error)
                },
                title = { Text("Error") },
                text = { Text(state.message) },
                confirmButton = {
                    TextButton(onClick = { roomsViewModel.dismissPurchaseState() }) {
                        Text("Entendido")
                    }
                }
            )
        }
        else -> { /* Idle o Loading */ }
    }
}

@Composable
fun RoomItemCard(
    item: RoomItem,
    isPurchased: Boolean,
    userCoins: Int,
    onPurchase: () -> Unit
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
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.emoji,
                    style = MaterialTheme.typography.displaySmall
                )

                Column {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (isPurchased) {
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
                            text = "Tuyo",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            } else {
                Button(
                    onClick = onPurchase,
                    enabled = userCoins >= item.price
                ) {
                    Text("🪙 ${item.price}")
                }
            }
        }
    }
}