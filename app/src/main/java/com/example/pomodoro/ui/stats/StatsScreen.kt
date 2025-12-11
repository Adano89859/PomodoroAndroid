package com.example.pomodoro.ui.stats

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pomodoro.ui.stats.components.BarChart
import com.example.pomodoro.data.model.Achievement

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    onNavigateBack: () -> Unit
) {
    val statsViewModel: StatsViewModel = viewModel()

    val user by statsViewModel.user.collectAsState()
    val totalStats by statsViewModel.totalStats.collectAsState()
    val currentStreak by statsViewModel.currentStreak.collectAsState()
    val last7Days by statsViewModel.last7Days.collectAsState()
    val unlockedMusicCount by statsViewModel.unlockedMusicCount.collectAsState()
    val roomsStats by statsViewModel.roomsStats.collectAsState()
    val unlockedAchievements by statsViewModel.unlockedAchievements.collectAsState()  // ← NUEVO
    val weeklyComparison by statsViewModel.weeklyComparison.collectAsState()  // ← NUEVO
    val bestDay by statsViewModel.bestDay.collectAsState()  // ← NUEVO

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("📊 Estadísticas") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Sección de monedas
            user?.let {
                StatCard(
                    icon = "🪙",
                    title = "Monedas",
                    value = "${it.coins}",
                    subtitle = "Balance actual",
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Racha de días
            if (currentStreak > 0) {
                StatCard(
                    icon = "🔥",
                    title = "Racha Actual",
                    value = "$currentStreak",
                    subtitle = if (currentStreak == 1) "día trabajando" else "días trabajando",
                    color = MaterialTheme.colorScheme.tertiary
                )
            }

            // Sección de habitaciones
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Habitaciones",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "🏡",
                                style = MaterialTheme.typography.displaySmall
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${roomsStats.completedRooms}/${roomsStats.totalRooms}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Completadas",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "📦",
                                style = MaterialTheme.typography.displaySmall
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${roomsStats.purchasedItems}/${roomsStats.totalItems}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Objetos",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Progreso Total",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${roomsStats.totalPercentage}%",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = { roomsStats.totalPercentage / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            color = if (roomsStats.completedRooms == roomsStats.totalRooms) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.secondary
                            }
                        )

                        if (roomsStats.completedRooms == roomsStats.totalRooms) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("🎉", style = MaterialTheme.typography.headlineSmall)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "¡Todas las habitaciones completas!",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }

            // ← NUEVO: Gráfico de últimos 7 días
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Actividad Reciente",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Card {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Pomodoros por día",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    BarChart(
                        data = last7Days,
                        barColor = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // ← NUEVO: Mejor día
            bestDay?.let { best ->
                if (best.pomodorosCompleted > 0) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "🏆 Mejor Día",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = best.date,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${best.pomodorosCompleted} 🍅",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                                Text(
                                    text = statsViewModel.formatTime(best.timeWorkedInSeconds),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // ← NUEVO: Comparativa semanal
            weeklyComparison?.let { comparison ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "📈 Esta Semana",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${comparison.thisWeekPomodoros}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Pomodoros",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${comparison.thisWeekTasks}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Tareas",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${comparison.avgPomodorosPerDay}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Promedio/día",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }

            // ← NUEVO: Logros
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "🏆 Logros Desbloqueados",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            if (unlockedAchievements.isEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🎯", style = MaterialTheme.typography.displayMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "¡Empieza a desbloquear logros!",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            } else {
                Text(
                    text = "${unlockedAchievements.size} de ${com.example.pomodoro.data.model.AchievementCatalog.achievements.size} logros",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Grid de logros
                val chunkedAchievements = unlockedAchievements.chunked(3)
                chunkedAchievements.forEach { rowAchievements ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowAchievements.forEach { achievement ->
                            AchievementBadge(
                                achievement = achievement,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        // Rellenar espacios vacíos
                        repeat(3 - rowAchievements.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Título de totales
            Text(
                text = "Totales Acumulados",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            // Grid de estadísticas totales
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    icon = "🍅",
                    title = "Pomodoros",
                    value = "${totalStats.totalPomodoros}",
                    subtitle = "completados",
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    icon = "✅",
                    title = "Tareas",
                    value = "${totalStats.totalTasks}",
                    subtitle = "completadas",
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    icon = "📝",
                    title = "Notas",
                    value = "${totalStats.totalNotes}",
                    subtitle = "escritas",
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    icon = "🎵",
                    title = "Canciones",
                    value = "$unlockedMusicCount",
                    subtitle = "desbloqueadas",
                    modifier = Modifier.weight(1f)
                )
            }

            // Tiempo total trabajado
            StatCard(
                icon = "⏱️",
                title = "Tiempo Total",
                value = statsViewModel.formatTime(totalStats.totalTimeWorked),
                subtitle = "de trabajo enfocado",
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Título de actividad reciente
            Text(
                text = "Últimos 7 Días",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            // Lista de actividad de los últimos 7 días
            if (last7Days.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "📊",
                            style = MaterialTheme.typography.displayMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Sin actividad reciente",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "¡Completa tu primer pomodoro!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                last7Days.forEach { dayStat ->
                    DayStatCard(dayStat = dayStat, viewModel = statsViewModel)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun StatCard(
    icon: String,
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primaryContainer
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = icon,
                style = MaterialTheme.typography.displaySmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun DayStatCard(
    dayStat: com.example.pomodoro.data.model.DailyStats,
    viewModel: StatsViewModel
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = dayStat.date,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${dayStat.pomodorosCompleted} pomodoros • ${dayStat.tasksCompleted} tareas",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = viewModel.formatTime(dayStat.timeWorkedInSeconds),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "trabajado",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ← NUEVO: Componente de Badge de logro
@Composable
fun AchievementBadge(
    achievement: Achievement,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = achievement.emoji,
                style = MaterialTheme.typography.displaySmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = achievement.title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Text(
                text = achievement.description,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )
        }
    }
}