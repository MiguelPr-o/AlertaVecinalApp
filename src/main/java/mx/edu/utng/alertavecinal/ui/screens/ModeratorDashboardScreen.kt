// ModeratorDashboardScreen.kt (VERSIÓN ACTUALIZADA)
package mx.edu.utng.alertavecinal.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import mx.edu.utng.alertavecinal.R
import mx.edu.utng.alertavecinal.data.model.Report
import mx.edu.utng.alertavecinal.data.model.ReportType
import mx.edu.utng.alertavecinal.ui.components.EmptyState
import mx.edu.utng.alertavecinal.ui.components.ErrorMessage
import mx.edu.utng.alertavecinal.ui.components.LoadingIndicator
import mx.edu.utng.alertavecinal.ui.components.ModeratorReportCard
import mx.edu.utng.alertavecinal.utils.Constants
import mx.edu.utng.alertavecinal.viewmodel.AuthViewModel
import mx.edu.utng.alertavecinal.viewmodel.ModeratorStats
import mx.edu.utng.alertavecinal.viewmodel.ModeratorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModeratorDashboardScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    onNavigateBack: () -> Unit = {}
) {
    val viewModel: ModeratorViewModel = hiltViewModel()
    val pendingReports by viewModel.pendingReports.collectAsStateWithLifecycle()
    val approvedReports by viewModel.approvedReports.collectAsStateWithLifecycle()
    val rejectedReports by viewModel.rejectedReports.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val moderatorStats by viewModel.moderatorStats.collectAsStateWithLifecycle()

    val authState by authViewModel.authState.collectAsState()
    val context = LocalContext.current

    var selectedFilter by remember { mutableStateOf<String?>(null) }
    var showFilterMenu by remember { mutableStateOf(false) }
    var activeTab by remember { mutableStateOf(ModeratorTab.PENDING) }

    // Función para manejar la navegación al Login
    fun navigateToLogin() {
        try {
            Log.d("ModeratorDashboard", "Navegando al Login desde ModeratorDashboard...")

            // Navegar al Login y limpiar el back stack
            navController.navigate(Constants.ROUTE_LOGIN) {
                // Remover todas las pantallas del stack incluyendo esta
                popUpTo(0) { inclusive = true }
                // Evitar múltiples instancias de Login
                launchSingleTop = true
            }

            Log.d("ModeratorDashboard", "Navegación al Login exitosa")
        } catch (e: Exception) {
            Log.e("ModeratorDashboard", "Error al navegar al Login: ${e.message}")
        }
    }

    // Cargar datos al iniciar
    LaunchedEffect(Unit) {
        Log.d("ModeratorDashboard", "Iniciando dashboard...")
        viewModel.loadAllModeratorData()

        // Debug: verificar estado de autenticación
        Log.d("ModeratorDashboard", "Auth State: ${authState.isAuthenticated}")
        Log.d("ModeratorDashboard", "User: ${authState.currentUser?.name}")
        Log.d("ModeratorDashboard", "Role: ${authState.currentUser?.role?.name}")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Panel del Moderador")
                        if (authState.isAuthenticated) {
                            Text(
                                text = "(${authState.currentUser?.name ?: "Moderador"})",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        // Navegar al Login cuando se presiona el botón de volver
                        navigateToLogin()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver al Login")
                    }
                },
                actions = {
                    // Filtro por tipo de reporte
                    IconButton(onClick = { showFilterMenu = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filtrar")
                    }

                    // Ordenar
                    IconButton(onClick = { /* TODO: Implementar ordenación */ }) {
                        Icon(Icons.Default.Sort, contentDescription = "Ordenar")
                    }

                    // Refrescar
                    IconButton(
                        onClick = {
                            viewModel.loadAllModeratorData()
                            Log.d("ModeratorDashboard", "Refrescando datos...")
                        },
                        enabled = !isLoading
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refrescar")
                    }
                }
            )
        },
        floatingActionButton = {
            if (activeTab == ModeratorTab.PENDING && pendingReports.isNotEmpty()) {
                FloatingActionButton(
                    onClick = {
                        // Acción rápida: aprobar el primer reporte pendiente
                        if (pendingReports.isNotEmpty()) {
                            val firstReport = pendingReports.first()
                            val moderatorId = authState.currentUser?.id ?: "unknown"
                            val moderatorName = authState.currentUser?.name ?: "Moderador"

                            viewModel.approveReport(
                                reportId = firstReport.id,
                                moderatorId = moderatorId,
                                moderatorName = moderatorName,
                                comment = "Aprobado desde acción rápida"
                            )
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = "Revisar todos")
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                // Mostrar mensajes de error
                errorMessage?.let { message ->
                    ErrorMessage(
                        message = message,
                        onDismiss = { viewModel.clearError() }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Estadísticas
                StatisticsSection(stats = moderatorStats)

                Spacer(modifier = Modifier.height(16.dp))

                // Tabs de navegación
                ModeratorTabs(
                    activeTab = activeTab,
                    onTabSelected = { tab ->
                        activeTab = tab
                        Log.d("ModeratorDashboard", "Cambiando a tab: $tab")
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Contenido según pestaña activa
                when (activeTab) {
                    ModeratorTab.PENDING -> {
                        if (isLoading) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                LoadingIndicator()
                            }
                        } else {
                            ReportsListSection(
                                reports = pendingReports,
                                emptyMessage = "No hay reportes pendientes de revisión",
                                emptyIcon = Icons.Default.CheckCircle,
                                onReportSelected = { reportId ->
                                    Log.d("ModeratorDashboard", "Click en reporte pendiente: $reportId")

                                    // Verificar que tenemos datos del moderador
                                    val moderatorId = authState.currentUser?.id ?: run {
                                        Log.e("ModeratorDashboard", "No hay ID de moderador disponible")
                                        "unknown_moderator_id"
                                    }

                                    val moderatorName = authState.currentUser?.name ?: run {
                                        Log.e("ModeratorDashboard", "No hay nombre de moderador disponible")
                                        "Moderador"
                                    }

                                    Log.d("ModeratorDashboard", "Navegando con:")
                                    Log.d("ModeratorDashboard", "  - Report ID: $reportId")
                                    Log.d("ModeratorDashboard", "  - Moderator ID: $moderatorId")
                                    Log.d("ModeratorDashboard", "  - Moderator Name: $moderatorName")

                                    // Navegar a la pantalla de revisión del moderador
                                    try {
                                        navController.navigate(
                                            "${Constants.ROUTE_MODERATOR_REVIEW}/$reportId/$moderatorId/$moderatorName"
                                        ) {
                                            launchSingleTop = true
                                        }
                                        Log.d("ModeratorDashboard", "Navegación exitosa")
                                    } catch (e: Exception) {
                                        Log.e("ModeratorDashboard", "Error en navegación: ${e.message}")
                                    }
                                }
                            )
                        }
                    }

                    ModeratorTab.APPROVED -> {
                        ReportsListSection(
                            reports = approvedReports,
                            emptyMessage = "No hay reportes aprobados",
                            emptyIcon = Icons.Default.CheckCircle,
                            onReportSelected = { reportId ->
                                Log.d("ModeratorDashboard", "Click en reporte aprobado: $reportId")
                                // Navegar a la pantalla de detalle normal
                                navController.navigate("${Constants.ROUTE_REPORT_DETAIL}/$reportId") {
                                    launchSingleTop = true
                                }
                            }
                        )
                    }

                    ModeratorTab.REJECTED -> {
                        ReportsListSection(
                            reports = rejectedReports,
                            emptyMessage = "No hay reportes rechazados",
                            emptyIcon = Icons.Default.Close,
                            onReportSelected = { reportId ->
                                Log.d("ModeratorDashboard", "Click en reporte rechazado: $reportId")
                                // Navegar a la pantalla de detalle normal
                                navController.navigate("${Constants.ROUTE_REPORT_DETAIL}/$reportId") {
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                }
            }
        }

        // Menú de filtros
        if (showFilterMenu) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 56.dp), // Debajo del TopAppBar
                contentAlignment = Alignment.TopEnd
            ) {
                FilterDropdownMenu(
                    selectedFilter = selectedFilter,
                    onFilterSelected = { filter ->
                        selectedFilter = filter
                        viewModel.filterReportsByType(filter)
                        showFilterMenu = false
                        Log.d("ModeratorDashboard", "Filtro aplicado: $filter")
                    },
                    onClearFilter = {
                        selectedFilter = null
                        viewModel.filterReportsByType(null)
                        showFilterMenu = false
                        Log.d("ModeratorDashboard", "Filtro limpiado")
                    },
                    onDismiss = {
                        showFilterMenu = false
                        Log.d("ModeratorDashboard", "Menú de filtros cerrado")
                    }
                )
            }
        }
    }
}


@Composable
private fun StatisticsSection(stats: ModeratorStats) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Estadísticas del Día",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    icon = Icons.Default.Pending,
                    value = stats.pendingCount.toString(),
                    label = "Pendientes",
                    color = MaterialTheme.colorScheme.secondary
                )

                StatItem(
                    icon = Icons.Default.CheckCircle,
                    value = stats.approvedCount.toString(),
                    label = "Aprobados",
                    color = MaterialTheme.colorScheme.primary
                )

                StatItem(
                    icon = Icons.Default.Close,
                    value = stats.rejectedCount.toString(),
                    label = "Rechazados",
                    color = MaterialTheme.colorScheme.error
                )

                StatItem(
                    icon = Icons.Default.Warning,
                    value = "${stats.approvalRate}%",
                    label = "Tasa Aprob.",
                    color = MaterialTheme.colorScheme.tertiary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tiempo promedio de respuesta: ${stats.averageResponseTime} min",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun StatItem(
    icon: ImageVector,
    value: String,
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(70.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(color.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )
    }
}

@Composable
private fun ModeratorTabs(
    activeTab: ModeratorTab,
    onTabSelected: (ModeratorTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ModeratorTab.values().forEach { tab ->
            val isActive = activeTab == tab

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (isActive) MaterialTheme.colorScheme.primary
                        else Color.Transparent
                    )
                    .clickable {
                        onTabSelected(tab)
                        Log.d("ModeratorTabs", "Tab seleccionado: ${tab.title}")
                    }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.title,
                        tint = if (isActive) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )

                    Text(
                        text = tab.title,
                        color = if (isActive) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ReportsListSection(
    reports: List<Report>,
    emptyMessage: String,
    emptyIcon: ImageVector,
    onReportSelected: (String) -> Unit
) {
    if (reports.isEmpty()) {
        EmptyState(
            title = emptyMessage,
            message = "Cuando haya nuevos reportes, aparecerán aquí",
            icon = emptyIcon
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(reports, key = { it.id }) { report ->
                ModeratorReportCard(
                    report = report,
                    onClick = {
                        Log.d("ReportsListSection", "Click en reporte: ${report.id}")
                        onReportSelected(report.id)
                    }
                )
            }
        }
    }
}

@Composable
private fun FilterDropdownMenu(
    selectedFilter: String?,
    onFilterSelected: (String?) -> Unit,
    onClearFilter: () -> Unit,
    onDismiss: () -> Unit
) {
    DropdownMenu(
        expanded = true,
        onDismissRequest = onDismiss,
        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
    ) {
        // Título
        Text(
            text = "Filtrar por tipo",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        Divider()

        // Opción para limpiar filtro
        if (selectedFilter != null) {
            DropdownMenuItem(
                text = {
                    Text(
                        "Mostrar todos",
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                onClick = onClearFilter,
                modifier = Modifier.background(
                    if (selectedFilter == null) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    else Color.Transparent
                )
            )

            Divider()
        }

        // Todos los tipos de reporte
        ReportType.values().forEach { reportType ->
            DropdownMenuItem(
                text = {
                    Text(
                        text = reportType.name.replace("_", " "),
                        color = if (selectedFilter == reportType.name)
                            MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface
                    )
                },
                onClick = { onFilterSelected(reportType.name) },
                modifier = Modifier.background(
                    if (selectedFilter == reportType.name)
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    else Color.Transparent
                )
            )
        }
    }
}

// En ModeratorDashboardScreen.kt, usa esto:
@Composable
private fun ErrorMessage(
    message: String,
    onDismiss: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Error",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.weight(1f)
            )

            if (onDismiss != null) {
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar",
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }
}

// Enums para las pestañas
enum class ModeratorTab(
    val title: String,
    val icon: ImageVector
) {
    PENDING("Pendientes", Icons.Default.Pending),
    APPROVED("Aprobados", Icons.Default.CheckCircle),
    REJECTED("Rechazados", Icons.Default.Close)
}