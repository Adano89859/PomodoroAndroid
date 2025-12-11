package com.example.pomodoro.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pomodoro.data.model.AppTheme
import com.example.pomodoro.data.model.PomodoroSettings
import com.example.pomodoro.data.model.SessionType
import com.example.pomodoro.ui.timer.PomodoroViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: PomodoroViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToShop: () -> Unit,
    onNavigateToStats: () -> Unit
) {
    val settings by viewModel.settings.collectAsState()
    var localSettings by remember { mutableStateOf(settings) }

    LaunchedEffect(settings) {
        localSettings = settings
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuración") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            viewModel.updateSettings(localSettings)
                            onNavigateBack()
                        }
                    ) {
                        Text("Guardar")
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
            // Sección de Apariencia
            Text(
                text = "Apariencia",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            var showThemeDialog by remember { mutableStateOf(false) }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showThemeDialog = true }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = localSettings.appTheme.emoji,
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                        Column {
                            Text(
                                text = "Tema de la aplicación",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = localSettings.appTheme.displayName,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = "Cambiar tema",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (showThemeDialog) {
                ThemeSelectorDialog(
                    currentTheme = localSettings.appTheme,
                    onDismiss = { showThemeDialog = false },
                    onThemeSelected = { theme ->
                        localSettings = localSettings.copy(appTheme = theme)
                        viewModel.updateSettings(localSettings)
                        showThemeDialog = false
                    }
                )
            }

            // Sección de duraciones
            Text(
                text = "Duraciones",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Card {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DurationSetting(
                        label = "Trabajo",
                        value = localSettings.workDuration,
                        onValueChange = { localSettings = localSettings.copy(workDuration = it) },
                        icon = Icons.Default.Work
                    )

                    HorizontalDivider()

                    DurationSetting(
                        label = "Descanso Corto",
                        value = localSettings.shortBreakDuration,
                        onValueChange = { localSettings = localSettings.copy(shortBreakDuration = it) },
                        icon = Icons.Default.Coffee
                    )

                    HorizontalDivider()

                    DurationSetting(
                        label = "Descanso Largo",
                        value = localSettings.longBreakDuration,
                        onValueChange = { localSettings = localSettings.copy(longBreakDuration = it) },
                        icon = Icons.Default.Weekend
                    )
                }
            }

            // Sección de pomodoros
            Text(
                text = "Ciclo de Pomodoros",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Card {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Pomodoros hasta descanso largo",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Después de ${localSettings.pomodorosUntilLongBreak} pomodoros",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    if (localSettings.pomodorosUntilLongBreak > 2) {
                                        localSettings = localSettings.copy(
                                            pomodorosUntilLongBreak = localSettings.pomodorosUntilLongBreak - 1
                                        )
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Remove, "Reducir")
                            }

                            Text(
                                text = "${localSettings.pomodorosUntilLongBreak}",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )

                            IconButton(
                                onClick = {
                                    if (localSettings.pomodorosUntilLongBreak < 8) {
                                        localSettings = localSettings.copy(
                                            pomodorosUntilLongBreak = localSettings.pomodorosUntilLongBreak + 1
                                        )
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Add, "Aumentar")
                            }
                        }
                    }
                }
            }

            // Sección de automatización
            Text(
                text = "Automatización",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Card {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SwitchSetting(
                        label = "Auto-iniciar descansos",
                        description = "Los descansos empiezan automáticamente",
                        checked = localSettings.autoStartBreaks,
                        onCheckedChange = { localSettings = localSettings.copy(autoStartBreaks = it) },
                        icon = Icons.Default.PlayArrow
                    )

                    HorizontalDivider()

                    SwitchSetting(
                        label = "Auto-iniciar pomodoros",
                        description = "Los pomodoros empiezan automáticamente",
                        checked = localSettings.autoStartPomodoros,
                        onCheckedChange = { localSettings = localSettings.copy(autoStartPomodoros = it) },
                        icon = Icons.Default.PlayArrow
                    )
                }
            }

            // Sección de notificaciones
            Text(
                text = "Notificaciones y Sonido",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Card {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SwitchSetting(
                        label = "Sonido",
                        description = "Reproducir música de fondo",
                        checked = localSettings.soundEnabled,
                        onCheckedChange = { localSettings = localSettings.copy(soundEnabled = it) },
                        icon = Icons.Default.VolumeUp
                    )

                    HorizontalDivider()

                    SwitchSetting(
                        label = "Vibración",
                        description = "Vibrar al terminar sesiones",
                        checked = localSettings.vibrationEnabled,
                        onCheckedChange = { localSettings = localSettings.copy(vibrationEnabled = it) },
                        icon = Icons.Default.Vibration
                    )
                }
            }

            // Sección de selección de música
            Text(
                text = "Música de Fondo",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            var showWorkMusicDialog by remember { mutableStateOf(false) }
            var showShortBreakMusicDialog by remember { mutableStateOf(false) }
            var showLongBreakMusicDialog by remember { mutableStateOf(false) }

            Card {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MusicSelectionRow(
                        label = "Música para Trabajo",
                        currentTrackId = localSettings.workMusicTrackId,
                        onClick = { showWorkMusicDialog = true }
                    )

                    HorizontalDivider()

                    MusicSelectionRow(
                        label = "Música para Descanso Corto",
                        currentTrackId = localSettings.shortBreakMusicTrackId,
                        onClick = { showShortBreakMusicDialog = true }
                    )

                    HorizontalDivider()

                    MusicSelectionRow(
                        label = "Música para Descanso Largo",
                        currentTrackId = localSettings.longBreakMusicTrackId,
                        onClick = { showLongBreakMusicDialog = true }
                    )
                }
            }

            // Diálogos de selección de música
            if (showWorkMusicDialog) {
                MusicSelectorDialog(
                    sessionType = SessionType.WORK,
                    currentTrackId = localSettings.workMusicTrackId,
                    onDismiss = { showWorkMusicDialog = false },
                    onTrackSelected = { trackId ->
                        localSettings = localSettings.copy(workMusicTrackId = trackId)
                    },
                    onPreviewTrack = { trackId ->
                        // TODO: Implementar preview
                    },
                    onNavigateToShop = onNavigateToShop  // ← NUEVO
                )
            }

            if (showShortBreakMusicDialog) {
                MusicSelectorDialog(
                    sessionType = SessionType.SHORT_BREAK,
                    currentTrackId = localSettings.shortBreakMusicTrackId,
                    onDismiss = { showShortBreakMusicDialog = false },
                    onTrackSelected = { trackId ->
                        localSettings = localSettings.copy(shortBreakMusicTrackId = trackId)
                    },
                    onPreviewTrack = { trackId ->
                        // TODO: Implementar preview
                    },
                    onNavigateToShop = onNavigateToShop  // ← NUEVO
                )
            }

            if (showLongBreakMusicDialog) {
                MusicSelectorDialog(
                    sessionType = SessionType.LONG_BREAK,
                    currentTrackId = localSettings.longBreakMusicTrackId,
                    onDismiss = { showLongBreakMusicDialog = false },
                    onTrackSelected = { trackId ->
                        localSettings = localSettings.copy(longBreakMusicTrackId = trackId)
                    },
                    onPreviewTrack = { trackId ->
                        // TODO: Implementar preview
                    },
                    onNavigateToShop = onNavigateToShop  // ← NUEVO
                )
            }

            // Sección de Progreso
            Text(
                text = "Progreso",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToStats() }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "📊",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Column {
                            Text(
                                text = "Ver Estadísticas",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Tu progreso y logros",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = "Ver estadísticas",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun DurationSetting(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    if (value > 1) onValueChange(value - 1)
                }
            ) {
                Icon(Icons.Default.Remove, "Reducir")
            }

            Text(
                text = "$value min",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            IconButton(
                onClick = {
                    if (value < 60) onValueChange(value + 1)
                }
            ) {
                Icon(Icons.Default.Add, "Aumentar")
            }
        }
    }
}

@Composable
fun SwitchSetting(
    label: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun MusicSelectionRow(
    label: String,
    currentTrackId: Int,  // ← CAMBIADO de String a Int
    onClick: () -> Unit
) {
    val track = com.example.pomodoro.utils.MusicCatalog.getTrackById(currentTrackId)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.MusicNote,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge
                )
                track?.let {
                    Text(
                        text = it.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Icon(
            Icons.Default.ChevronRight,
            contentDescription = "Seleccionar",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}