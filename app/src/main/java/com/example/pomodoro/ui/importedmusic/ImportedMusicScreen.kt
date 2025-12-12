package com.example.pomodoro.ui.importedmusic

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.example.pomodoro.data.model.ImportedMusic
import com.example.pomodoro.data.model.SessionType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportedMusicScreen(
    onNavigateBack: () -> Unit
) {
    val viewModel: ImportedMusicViewModel = viewModel()

    val userCoins by viewModel.userCoins.collectAsState()
    val allMusic by viewModel.allImportedMusic.collectAsState()
    val importState by viewModel.importState.collectAsState()
    val purchaseState by viewModel.purchaseState.collectAsState()
    val previewingMusicId by viewModel.previewingMusicId.collectAsState()
    val canImportMore by viewModel.canImportMore.collectAsState()

    var showImportDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf<ImportedMusic?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🎵 Mis Canciones") },
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
                            Text("🪙", style = MaterialTheme.typography.titleMedium)
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
        },
        floatingActionButton = {
            if (canImportMore) {
                ExtendedFloatingActionButton(
                    onClick = { showImportDialog = true },
                    icon = { Icon(Icons.Default.Add, "Importar") },
                    text = { Text("Importar Música") }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Info card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "💡 Consejo",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Puedes importar tu propia música desde tu dispositivo. Asegúrate de tener los derechos necesarios para usar las canciones.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Canciones importadas: ${allMusic.size}/15",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Precio por canción: 🪙 150",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Lista de canciones
            if (allMusic.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "🎵",
                            style = MaterialTheme.typography.displayLarge
                        )
                        Text(
                            text = "No tienes canciones importadas",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Toca el botón + para empezar",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(allMusic) { music ->
                        ImportedMusicCard(
                            music = music,
                            userCoins = userCoins,
                            isPreviewing = music.id == previewingMusicId,
                            onPurchase = { viewModel.purchaseMusic(music.id) },
                            onPreview = { viewModel.playPreview(music) },
                            onDelete = { showDeleteDialog = music }
                        )
                    }
                }
            }
        }
    }

    // Diálogo de importar
    if (showImportDialog) {
        ImportMusicDialog(
            onDismiss = { showImportDialog = false },
            onImport = { uri, name, sessionType ->
                viewModel.importMusic(uri, name, sessionType)
                showImportDialog = false
            }
        )
    }

    // Diálogo de eliminar
    showDeleteDialog?.let { music ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            icon = { Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("¿Eliminar canción?") },
            text = { Text("Se eliminará '${music.displayName}' permanentemente.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteMusic(music)
                        showDeleteDialog = null
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Diálogos de estado
    when (val state = importState) {
        is ImportState.Success -> {
            AlertDialog(
                onDismissRequest = { viewModel.dismissImportState() },
                icon = { Text("✅", style = MaterialTheme.typography.displayMedium) },
                title = { Text("¡Música importada!") },
                text = { Text("'${state.musicName}' se ha importado correctamente. Cómprala por 🪙150 para usarla.") },
                confirmButton = {
                    TextButton(onClick = { viewModel.dismissImportState() }) {
                        Text("Entendido")
                    }
                }
            )
        }
        is ImportState.Error -> {
            AlertDialog(
                onDismissRequest = { viewModel.dismissImportState() },
                icon = { Icon(Icons.Default.Error, null, tint = MaterialTheme.colorScheme.error) },
                title = { Text("Error") },
                text = { Text(state.message) },
                confirmButton = {
                    TextButton(onClick = { viewModel.dismissImportState() }) {
                        Text("Entendido")
                    }
                }
            )
        }
        else -> {}
    }

    when (val state = purchaseState) {
        is PurchaseState.Success -> {
            AlertDialog(
                onDismissRequest = { viewModel.dismissPurchaseState() },
                icon = { Text("🎉", style = MaterialTheme.typography.displayMedium) },
                title = { Text("¡Compra exitosa!") },
                text = { Text("Has desbloqueado: ${state.musicName}") },
                confirmButton = {
                    TextButton(onClick = { viewModel.dismissPurchaseState() }) {
                        Text("Genial")
                    }
                }
            )
        }
        is PurchaseState.Error -> {
            AlertDialog(
                onDismissRequest = { viewModel.dismissPurchaseState() },
                icon = { Icon(Icons.Default.Error, null, tint = MaterialTheme.colorScheme.error) },
                title = { Text("Error") },
                text = { Text(state.message) },
                confirmButton = {
                    TextButton(onClick = { viewModel.dismissPurchaseState() }) {
                        Text("Entendido")
                    }
                }
            )
        }
        else -> {}
    }
}

@Composable
fun ImportedMusicCard(
    music: ImportedMusic,
    userCoins: Int,
    isPreviewing: Boolean,
    onPurchase: () -> Unit,
    onPreview: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPreviewing) {
                MaterialTheme.colorScheme.tertiaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = music.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = when (music.sessionType) {
                            SessionType.WORK -> "💼 Para: Trabajo"
                            SessionType.SHORT_BREAK -> "☁️ Para: Descanso Corto"
                            SessionType.LONG_BREAK -> "😌 Para: Descanso Largo"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (isPreviewing) {
                        Text(
                            text = "Reproduciendo preview...",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onPreview) {
                        Icon(
                            if (isPreviewing) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = if (isPreviewing) "Detener" else "Preview",
                            tint = if (isPreviewing) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (music.isPurchased) {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Desbloqueada - Lista para usar",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                Button(
                    onClick = onPurchase,
                    enabled = userCoins >= 150,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("🪙 150 - Desbloquear")
                }
            }
        }
    }
}

@Composable
fun ImportMusicDialog(
    onDismiss: () -> Unit,
    onImport: (Uri, String, SessionType) -> Unit
) {
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var displayName by remember { mutableStateOf("") }
    var selectedSessionType by remember { mutableStateOf(SessionType.WORK) }
    var showSessionTypeMenu by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedUri = uri
        uri?.let {
            // Extraer nombre del archivo
            displayName = it.lastPathSegment?.substringAfterLast("/") ?: "Mi canción"
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Importar Música") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { launcher.launch("audio/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.FileOpen, "Seleccionar archivo")
                    Spacer(Modifier.width(8.dp))
                    Text(if (selectedUri == null) "Seleccionar archivo" else "Cambiar archivo")
                }

                selectedUri?.let {
                    Text(
                        text = "✅ Archivo seleccionado",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                OutlinedTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    label = { Text("Nombre de la canción") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Selector de tipo de sesión
                Box {
                    OutlinedTextField(
                        value = when (selectedSessionType) {
                            SessionType.WORK -> "💼 Trabajo"
                            SessionType.SHORT_BREAK -> "☁️ Descanso Corto"
                            SessionType.LONG_BREAK -> "😌 Descanso Largo"
                        },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo de sesión") },
                        trailingIcon = {
                            IconButton(onClick = { showSessionTypeMenu = !showSessionTypeMenu }) {
                                Icon(
                                    if (showSessionTypeMenu) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                    "Expandir"
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    DropdownMenu(
                        expanded = showSessionTypeMenu,
                        onDismissRequest = { showSessionTypeMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("💼 Trabajo") },
                            onClick = {
                                selectedSessionType = SessionType.WORK
                                showSessionTypeMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("☁️ Descanso Corto") },
                            onClick = {
                                selectedSessionType = SessionType.SHORT_BREAK
                                showSessionTypeMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("😌 Descanso Largo") },
                            onClick = {
                                selectedSessionType = SessionType.LONG_BREAK
                                showSessionTypeMenu = false
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    selectedUri?.let { uri ->
                        if (displayName.isNotBlank()) {
                            onImport(uri, displayName, selectedSessionType)
                        }
                    }
                },
                enabled = selectedUri != null && displayName.isNotBlank()
            ) {
                Text("Importar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}