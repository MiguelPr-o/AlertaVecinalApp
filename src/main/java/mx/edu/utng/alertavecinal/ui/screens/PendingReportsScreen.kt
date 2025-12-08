package mx.edu.utng.alertavecinal.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import mx.edu.utng.alertavecinal.ui.components.EmptyState
import mx.edu.utng.alertavecinal.ui.components.LoadingIndicator
import mx.edu.utng.alertavecinal.viewmodel.AuthViewModel
import mx.edu.utng.alertavecinal.viewmodel.ReportViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PendingReportsScreen(
    navController: NavController,
    reportViewModel: ReportViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel() // ← Agregar AuthViewModel
) {
    // Obtener estados
    val pendingReports by reportViewModel.pendingReportsState.collectAsState()
    val reportState by reportViewModel.reportState.collectAsState()
    val authState by authViewModel.authState.collectAsState()

    // Obtener userId del usuario autenticado
    val currentUserId = authState.currentUser?.id ?: ""
    val isUserAuthenticated = authState.isAuthenticated

    // Estados para UI
    var showMenuForReportId by remember { mutableStateOf<String?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var reportToDelete by remember { mutableStateOf<String?>(null) }

    // Cargar reportes pendientes al iniciar
    LaunchedEffect(Unit) {
        reportViewModel.loadPendingReports()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reportes Pendientes") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                reportState.isLoading && pendingReports.isEmpty() -> {
                    LoadingIndicator()
                }
                pendingReports.isEmpty() -> {
                    EmptyState(
                        icon = Icons.Default.PendingActions,
                        title = "No hay reportes pendientes",
                        message = "Todos los reportes han sido revisados. ¡Buen trabajo!"
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(pendingReports) { report ->
                            Card(
                                onClick = {
                                    reportViewModel.selectReport(report)
                                    // Opcional: navegar a detalles
                                    // navController.navigate("reportDetail/${report.id}")
                                },
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = report.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )

                                    Text(
                                        text = report.description,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )

                                    Text(
                                        text = "Tipo: ${report.reportType}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )

                                    Text(
                                        text = "Creado por: ${report.userName}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.secondary
                                    )

                                    // Mostrar opciones solo si el usuario está autenticado
                                    if (isUserAuthenticated) {
                                        Box(
                                            modifier = Modifier.fillMaxWidth(),
                                            contentAlignment = Alignment.CenterEnd
                                        ) {
                                            IconButton(
                                                onClick = {
                                                    showMenuForReportId = report.id
                                                }
                                            ) {
                                                Icon(
                                                    Icons.Default.Delete,
                                                    contentDescription = "Opciones",
                                                    tint = MaterialTheme.colorScheme.error
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Menú contextual para eliminar
            if (showMenuForReportId != null) {
                DropdownMenu(
                    expanded = true,
                    onDismissRequest = { showMenuForReportId = null }
                ) {
                    DropdownMenuItem(
                        text = { Text("Eliminar reporte") },
                        onClick = {
                            reportToDelete = showMenuForReportId
                            showDeleteDialog = true
                            showMenuForReportId = null
                        }
                    )
                }
            }

            // Diálogo de confirmación para eliminar
            if (showDeleteDialog && reportToDelete != null) {
                AlertDialog(
                    onDismissRequest = {
                        showDeleteDialog = false
                        reportToDelete = null
                    },
                    title = { Text("Confirmar eliminación") },
                    text = {
                        Text(
                            if (currentUserId.isEmpty()) {
                                "No estás autenticado. Inicia sesión para eliminar reportes."
                            } else {
                                "¿Estás seguro de eliminar este reporte? Esta acción no se puede deshacer."
                            }
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                if (currentUserId.isNotEmpty()) {
                                    reportViewModel.deleteReport(reportToDelete!!, currentUserId)
                                }
                                showDeleteDialog = false
                                reportToDelete = null
                            },
                            enabled = currentUserId.isNotEmpty()
                        ) {
                            Text("Eliminar")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                showDeleteDialog = false
                                reportToDelete = null
                            }
                        ) {
                            Text("Cancelar")
                        }
                    }
                )
            }

            // Mostrar error si no hay usuario autenticado
            LaunchedEffect(authState) {
                if (!isUserAuthenticated) {
                    // Opcional: Mostrar mensaje o redirigir al login
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PendingReportsScreenPreview() {
    PendingReportsScreen(navController = rememberNavController())
}