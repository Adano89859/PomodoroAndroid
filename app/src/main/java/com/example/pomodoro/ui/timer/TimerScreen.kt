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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pomodoro.data.model.SessionType
import com.example.pomodoro.data.model.TimerState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen(
    viewModel: PomodoroViewModel,
    onNavigateToTasks: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val timerState by viewModel.timerState.collectAsState()
    val timeRemaining by viewModel.timeRemaining.collectAsState()
    val sessionType by viewModel.sessionType.collectAsState()
    val completedPomodoros by viewModel.completedPomodoros.collectAsState()
    val currentTask by viewModel.currentTask.collectAsState()
    val settings by viewModel.settings.collectAsState()

    // Calcular el progreso
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
            // Indicador de sesión
            SessionIndicator(
                sessionType = sessionType,
                completedPomodoros = completedPomodoros,
                totalPomodoros = settings.pomodorosUntilLongBreak
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Timer circular
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
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Tarea actual
            currentTask?.let { task ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Timer,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = task.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${task.pomodorosCompleted} pomodoros completados",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            } ?: run {
                TextButton(onClick = onNavigateToTasks) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Seleccionar tarea")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Controles del timer
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón Skip
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

                // Botón principal (Play/Pause)
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

                // Botón Reset
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
        val radius = diameter / 2f
        val topLeft = Offset(
            x = (size.width - diameter) / 2f,
            y = (size.height - diameter) / 2f
        )

        // Círculo de fondo
        drawArc(
            color = color.copy(alpha = 0.1f),
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = topLeft,
            size = Size(diameter, diameter),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // Arco de progreso
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
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Indicadores de pomodoros
        if (sessionType == SessionType.WORK) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(totalPomodoros) { index ->
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .then(
                                if (index < completedPomodoros) {
                                    Modifier
                                } else {
                                    Modifier
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
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
}

@Composable
fun getSessionColor(sessionType: SessionType): Color {
    return when (sessionType) {
        SessionType.WORK -> Color(0xFFE53935) // Rojo
        SessionType.SHORT_BREAK -> Color(0xFF43A047) // Verde
        SessionType.LONG_BREAK -> Color(0xFF1E88E5) // Azul
    }
}

fun getSessionLabel(sessionType: SessionType): String {
    return when (sessionType) {
        SessionType.WORK -> "Tiempo de concentración"
        SessionType.SHORT_BREAK -> "Relájate un poco"
        SessionType.LONG_BREAK -> "Buen descanso"
    }
}