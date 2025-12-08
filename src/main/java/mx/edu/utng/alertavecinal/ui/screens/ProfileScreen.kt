package mx.edu.utng.alertavecinal.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Report
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Switch
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import mx.edu.utng.alertavecinal.data.model.ReportStatus
import mx.edu.utng.alertavecinal.ui.components.CustomButton
import mx.edu.utng.alertavecinal.viewmodel.AuthViewModel
import mx.edu.utng.alertavecinal.viewmodel.ProfileViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val profileState by profileViewModel.userProfile.collectAsState()
    val authState by authViewModel.authState.collectAsState()
    val isLoading by profileViewModel.isLoading.collectAsState()
    val error by profileViewModel.error.collectAsState()
    val userReports by profileViewModel.userReports.collectAsState()
    val deleteState by profileViewModel.deleteState.collectAsState()

    // ✅ Obtener userId actual
    val currentUserId = authState.currentUser?.id ?: ""

    var notificationsEnabled by remember { mutableStateOf(true) }
    var notificationRadius by remember { mutableStateOf(1000) }
    var showReportHistory by remember { mutableStateOf(false) }

    // ✅✅✅ CORREGIDO: Observar cambios en authState.currentUser en lugar de Unit
    LaunchedEffect(authState.currentUser) {
        println("🔍 DEBUG ProfileScreen - authState.currentUser cambiado: ${authState.currentUser}")
        val currentUser = authState.currentUser
        if (currentUser != null) {
            println("🔍 DEBUG ProfileScreen - Cargando perfil para usuario: ${currentUser.id}")
            profileViewModel.loadUserProfile(currentUser.id)
        } else {
            println("🔍 DEBUG ProfileScreen - No hay usuario autenticado")
        }
    }

    // ✅✅✅ AGREGADO: También cargar cuando la pantalla se enfoca por primera vez
    LaunchedEffect(Unit) {
        // Verificar si ya hay un usuario pero no se ha cargado el perfil
        if (authState.currentUser != null && profileState == null) {
            println("🔍 DEBUG ProfileScreen - Cargando perfil en inicio")
            profileViewModel.loadUserProfile(authState.currentUser!!.id)
        }
    }

    // ✅✅✅ AGREGADO: Manejar estado de eliminación
    LaunchedEffect(deleteState) {
        when (deleteState) {
            is ProfileViewModel.DeleteState.Success -> {
                // Se eliminó exitosamente, ya se recargaron los datos
            }
            is ProfileViewModel.DeleteState.Error -> {
                // Podrías mostrar el error en un snackbar
                println("❌ Error al eliminar: ${(deleteState as ProfileViewModel.DeleteState.Error).message}")
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (showReportHistory) "Mi Historial" else "Mi Perfil")
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (showReportHistory) {
                            showReportHistory = false
                        } else {
                            navController.popBackStack()
                        }
                    }) {
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
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (showReportHistory) {
                // ✅✅✅ ACTUALIZADO: Pasar ProfileViewModel y userId
                ReportHistorySection(
                    userReports = userReports,
                    onReportClick = { report ->
                        navController.navigate("report_detail/${report.id}")
                    },
                    onBackClick = { showReportHistory = false },
                    profileViewModel = profileViewModel,
                    currentUserId = currentUserId
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Información del usuario
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Usuario",
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.size(12.dp))
                                Text(
                                    text = profileState?.name ?: "Usuario",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = "Email",
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.size(12.dp))
                                Text(
                                    text = profileState?.email ?: "email@ejemplo.com",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            profileState?.phone?.let { phone ->
                                if (phone.isNotEmpty()) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Phone,
                                            contentDescription = "Teléfono",
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.size(12.dp))
                                        Text(
                                            text = phone,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Configuración de notificaciones
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Configuración de Notificaciones",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Notificaciones activadas",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Switch(
                                    checked = notificationsEnabled,
                                    onCheckedChange = {
                                        notificationsEnabled = it
                                        profileState?.let { user ->
                                            profileViewModel.updateNotificationSettings(
                                                userId = user.id,
                                                radius = notificationRadius,
                                                enabled = it
                                            )
                                        }
                                    }
                                )
                            }

                            Divider()

                            Text(
                                text = "Radio de notificaciones: ${notificationRadius}m",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    // Estadísticas - ✅✅✅ CORREGIDO: Usar userReports del ProfileViewModel
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Mis Reportes",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                            )

                            val totalCount = userReports.size
                            val approvedCount = userReports.count { it.status == ReportStatus.APPROVED }
                            val pendingCount = userReports.count { it.status == ReportStatus.PENDING }
                            val rejectedCount = userReports.count { it.status == ReportStatus.REJECTED }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Total de reportes:",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = totalCount.toString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "✅ Aprobados:",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = approvedCount.toString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "⏳ Pendientes:",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = pendingCount.toString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "❌ Rechazados:",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = rejectedCount.toString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Botones de acción
                    CustomButton(
                        text = "Ver Historial de Reportes",
                        onClick = {
                            showReportHistory = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = MaterialTheme.colorScheme.secondary,
                        leadingIcon = Icons.Default.History
                    )

                    CustomButton(
                        text = "Cerrar Sesión",
                        onClick = {
                            authViewModel.logout()
                            navController.navigate("welcome") {
                                popUpTo(0)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = MaterialTheme.colorScheme.error
                    )
                }
            }

            // Mostrar errores si existen
            error?.let { errorMessage ->
                Snackbar(
                    action = {
                        TextButton(
                            onClick = { profileViewModel.clearError() }
                        ) {
                            Text("OK")
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text(errorMessage)
                }
            }
        }
    }
}

// Sección de historial de reportes CON FUNCIONALIDAD DE ELIMINAR
@OptIn(ExperimentalMaterial3Api::class) // ✅ AGREGADO: Esta línea resuelve el error
@Composable
fun ReportHistorySection(
    userReports: List<mx.edu.utng.alertavecinal.data.model.Report>,
    onReportClick: (mx.edu.utng.alertavecinal.data.model.Report) -> Unit,
    onBackClick: () -> Unit,
    profileViewModel: ProfileViewModel,
    currentUserId: String
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var reportToDelete by remember { mutableStateOf<mx.edu.utng.alertavecinal.data.model.Report?>(null) }

    val deleteState by profileViewModel.deleteState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Encabezado del historial
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
            }
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = "Mis Reportes (${userReports.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
        }

        // ✅ AGREGADO: Manejar estados de eliminación
        LaunchedEffect(deleteState) {
            when (deleteState) {
                is ProfileViewModel.DeleteState.Success -> {
                    // Cerrar diálogo y limpiar
                    showDeleteDialog = false
                    reportToDelete = null
                    profileViewModel.resetDeleteState()
                }
                is ProfileViewModel.DeleteState.Error -> {
                    // Mantener el diálogo abierto para mostrar error
                    profileViewModel.resetDeleteState()
                }
                else -> {}
            }
        }

        if (userReports.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Report,
                        contentDescription = "Sin reportes",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Text(
                        text = "No tienes reportes aún",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    CustomButton(
                        text = "Crear Primer Reporte",
                        onClick = onBackClick,
                        backgroundColor = MaterialTheme.colorScheme.primary
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(userReports) { report ->
                    // ✅ NUEVO: Card personalizada con opción de eliminar
                    Card(
                        onClick = { onReportClick(report) },
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = report.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )

                                    Text(
                                        text = report.description,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(top = 4.dp),
                                        maxLines = 2
                                    )

                                    Text(
                                        text = "Estado: ${report.status.name}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = when (report.status) {
                                            ReportStatus.APPROVED -> MaterialTheme.colorScheme.primary
                                            ReportStatus.PENDING -> MaterialTheme.colorScheme.secondary
                                            ReportStatus.REJECTED -> MaterialTheme.colorScheme.error
                                        },
                                        modifier = Modifier.padding(top = 8.dp)
                                    )

                                    Text(
                                        text = "Fecha: ${report.createdAt?.let {
                                            SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                                                .format(Date(it))
                                        } ?: "N/A"}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }

                                // ✅ NUEVO: Botón de eliminar (solo para reportes pendientes)
                                if (report.status == ReportStatus.PENDING && report.userId == currentUserId) {
                                    IconButton(
                                        onClick = {
                                            reportToDelete = report
                                            showDeleteDialog = true
                                        }
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Eliminar",
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

        // ✅ NUEVO: Diálogo de confirmación para eliminar
        if (showDeleteDialog && reportToDelete != null) {
            AlertDialog(
                onDismissRequest = {
                    if (deleteState !is ProfileViewModel.DeleteState.Loading) {
                        showDeleteDialog = false
                        reportToDelete = null
                        profileViewModel.resetDeleteState()
                    }
                },
                title = { Text("Confirmar eliminación") },
                text = {
                    Column {
                        Text("¿Estás seguro de eliminar este reporte?")
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = "Título: ${reportToDelete?.title}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        Text(
                            "Esta acción no se puede deshacer.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (reportToDelete != null && currentUserId.isNotEmpty()) {
                                profileViewModel.deleteReport(reportToDelete!!.id, currentUserId)
                            }
                        },
                        enabled = deleteState !is ProfileViewModel.DeleteState.Loading
                    ) {
                        if (deleteState is ProfileViewModel.DeleteState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Eliminar")
                        }
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            if (deleteState !is ProfileViewModel.DeleteState.Loading) {
                                showDeleteDialog = false
                                reportToDelete = null
                                profileViewModel.resetDeleteState()
                            }
                        },
                        enabled = deleteState !is ProfileViewModel.DeleteState.Loading
                    ) {
                        Text("Cancelar")
                    }
                }
            )
        }

        // Mostrar loading global al eliminar
        if (deleteState is ProfileViewModel.DeleteState.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    MaterialTheme {
        ProfileScreen(navController = rememberNavController())
    }
}