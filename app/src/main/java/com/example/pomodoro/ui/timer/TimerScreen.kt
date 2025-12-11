package com.example.pomodoro.ui.timer

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pomodoro.data.model.SessionType
import com.example.pomodoro.data.model.TimerState
import com.example.pomodoro.ui.dialogs.ProgressNoteDialog
import com.example.pomodoro.ui.dialogs.CelebrationDialog
import com.example.pomodoro.ui.dialogs.CoinRewardDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen(
    viewModel: PomodoroViewModel,
    onNavigateToTasks: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToShop: () -> Unit,
    onNavigateToRooms: () -> Unit  // ← NUEVO
) {
    val timerState by viewModel.timerState.collectAsState()
    val timeRemaining by viewModel.timeRemaining.collectAsState()
    val sessionType by viewModel.sessionType.collectAsState()
    val completedPomodoros by viewModel.completedPomodoros.collectAsState()
    val currentTask by viewModel.currentTask.collectAsState()
    val settings by viewModel.settings.collectAsState()

    val userCoins by viewModel.userCoins.collectAsState()
    val showCoinRewardDialog by viewModel.showCoinRewardDialog.collectAsState()
    val lastCoinReward by viewModel.lastCoinReward.collectAsState()

    val showProgressDialog by viewModel.showProgressDialog.collectAsState()
    val showCelebrationDialog by viewModel.showCelebrationDialog.collectAsState()
    val celebrationSessionType by viewModel.celebrationSessionType.collectAsState()

    val totalTime = when (sessionType) {
        SessionType.WORK -> settings.workDuration * 60
        SessionType.SHORT_BREAK -> settings.shortBreakDuration * 60
        SessionType.LONG_BREAK -> settings.longBreakDuration * 60
    }
    val progress = if (totalTime > 0) {
        1f - (timeRemaining.toFloat() / totalTime)
    } else 0f

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pomodoro Timer") },
                actions = {
                    // Mostrar monedas
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Text(
                            text = "🪙 $userCoins",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // ← NUEVO: Icono de habitaciones
                    IconButton(onClick = onNavigateToRooms) {
                        Icon(Icons.Default.Home, "Habitaciones")
                    }

                    IconButton(onClick = onNavigateToShop) {
                        Icon(Icons.Default.ShoppingCart, "Tienda")
                    }
                    IconButton(onClick = onNavigateToTasks) {
                        Icon(Icons.Default.CheckCircle, "Tareas")
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, "Configuración")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            SessionIndicator(
                sessionType = sessionType,
                completedPomodoros = completedPomodoros,
                totalPomodoros = settings.pomodorosUntilLongBreak
            )

            Spacer(modifier = Modifier.height(24.dp))

            currentTask?.let { task ->
                CurrentTaskCard(
                    task = task,
                    viewModel = viewModel
                )
            } ?: run {
                Card(
                    modifier = Modifier.fillMaxWidth(),
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
                        Icon(
                            Icons.Default.Assignment,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Sin tarea seleccionada",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = onNavigateToTasks) {
                            Text("Seleccionar tarea")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(280.dp)
            ) {
                CircularProgressIndicator(
                    progress = progress,
                    sessionType = sessionType
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = viewModel.formatTime(timeRemaining),
                        fontSize = 56.sp,
                        fontWeight = FontWeight.Bold,
                        color = getSessionColor(sessionType)
                    )

                    Text(
                        text = getSessionLabel(sessionType),
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.skipSession() },
                    enabled = timerState != TimerState.IDLE
                ) {
                    Icon(
                        Icons.Default.SkipNext,
                        contentDescription = "Saltar",
                        modifier = Modifier.size(32.dp)
                    )
                }

                FloatingActionButton(
                    onClick = {
                        when (timerState) {
                            TimerState.IDLE, TimerState.PAUSED -> viewModel.startTimer()
                            TimerState.RUNNING -> viewModel.pauseTimer()
                        }
                    },
                    modifier = Modifier.size(72.dp),
                    containerColor = getSessionColor(sessionType)
                ) {
                    Icon(
                        imageVector = when (timerState) {
                            TimerState.RUNNING -> Icons.Default.Pause
                            else -> Icons.Default.PlayArrow
                        },
                        contentDescription = when (timerState) {
                            TimerState.RUNNING -> "Pausar"
                            else -> "Iniciar"
                        },
                        modifier = Modifier.size(36.dp),
                        tint = Color.White
                    )
                }

                IconButton(
                    onClick = { viewModel.resetTimer() },
                    enabled = timerState != TimerState.IDLE
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Reiniciar",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showProgressDialog) {
        ProgressNoteDialog(
            onDismiss = { viewModel.saveProgressNote("") },
            onSave = { note -> viewModel.saveProgressNote(note) }
        )
    }

    if (showCelebrationDialog && celebrationSessionType != null) {
        CelebrationDialog(
            sessionType = celebrationSessionType!!,
            onDismiss = { viewModel.dismissCelebration() }
        )
    }

    if (showCoinRewardDialog && lastCoinReward != null) {
        CoinRewardDialog(
            reward = lastCoinReward!!,
            onDismiss = { viewModel.dismissCoinReward() }
        )
    }
}

@Composable
fun CurrentTaskCard(
    task: com.example.pomodoro.data.model.PomodoroTask,
    viewModel: PomodoroViewModel
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    Icons.Default.Work,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (task.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "${task.pomodorosCompleted}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Text(
                        text = "Pomodoros",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }

                Divider(
                    modifier = Modifier
                        .height(40.dp)
                        .width(1.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.AccessTime,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = viewModel.formatWorkTime(task.timeSpentInSeconds),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Text(
                        text = "Tiempo trabajado",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun CircularProgressIndicator(
    progress: Float,
    sessionType: SessionType,
    modifier: Modifier = Modifier
) {
    val color = getSessionColor(sessionType)

    Canvas(modifier = modifier.fillMaxSize()) {
        val strokeWidth = 12.dp.toPx()
        val diameter = size.minDimension - strokeWidth
        val topLeft = Offset(
            x = (size.width - diameter) / 2f,
            y = (size.height - diameter) / 2f
        )

        drawArc(
            color = color.copy(alpha = 0.1f),
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = topLeft,
            size = Size(diameter, diameter),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        drawArc(
            color = color,
            startAngle = -90f,
            sweepAngle = 360f * progress,
            useCenter = false,
            topLeft = topLeft,
            size = Size(diameter, diameter),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun SessionIndicator(
    sessionType: SessionType,
    completedPomodoros: Int,
    totalPomodoros: Int
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = when (sessionType) {
                SessionType.WORK -> "Sesión de Trabajo"
                SessionType.SHORT_BREAK -> "Descanso Corto"
                SessionType.LONG_BREAK -> "Descanso Largo"
            },
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (sessionType == SessionType.WORK) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(totalPomodoros) { index ->
                    Icon(
                        imageVector = if (index < completedPomodoros) {
                            Icons.Default.CheckCircle
                        } else {
                            Icons.Default.RadioButtonUnchecked
                        },
                        contentDescription = null,
                        tint = if (index < completedPomodoros) {
                            getSessionColor(SessionType.WORK)
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                        },
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun getSessionColor(sessionType: SessionType): Color {
    return when (sessionType) {
        SessionType.WORK -> Color(0xFFE53935)
        SessionType.SHORT_BREAK -> Color(0xFF43A047)
        SessionType.LONG_BREAK -> Color(0xFF1E88E5)
    }
}

fun getSessionLabel(sessionType: SessionType): String {
    return when (sessionType) {
        SessionType.WORK -> "Tiempo de concentración"
        SessionType.SHORT_BREAK -> "Relájate un poco"
        SessionType.LONG_BREAK -> "Buen descanso"
    }
}