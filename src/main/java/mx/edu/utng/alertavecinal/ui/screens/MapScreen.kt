package mx.edu.utng.alertavecinal.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import mx.edu.utng.alertavecinal.data.model.ReportStatus
import mx.edu.utng.alertavecinal.data.model.ReportType
import mx.edu.utng.alertavecinal.ui.components.LoadingIndicator
import mx.edu.utng.alertavecinal.utils.NotificationUtils
import mx.edu.utng.alertavecinal.viewmodel.AuthViewModel
import mx.edu.utng.alertavecinal.viewmodel.MapViewModel

// Función para obtener color del marcador según el tipo de reporte
fun getMarkerColor(reportType: ReportType): Float {
    return when (reportType) {
        ReportType.ROBBERY -> BitmapDescriptorFactory.HUE_RED
        ReportType.FIRE -> BitmapDescriptorFactory.HUE_ORANGE
        ReportType.ACCIDENT -> BitmapDescriptorFactory.HUE_YELLOW
        ReportType.SUSPICIOUS_PERSON -> BitmapDescriptorFactory.HUE_VIOLET
        ReportType.FIGHT -> BitmapDescriptorFactory.HUE_ROSE
        ReportType.VANDALISM -> BitmapDescriptorFactory.HUE_MAGENTA
        ReportType.NOISE -> BitmapDescriptorFactory.HUE_BLUE
        ReportType.LOST_PET -> BitmapDescriptorFactory.HUE_CYAN
        ReportType.OTHER -> BitmapDescriptorFactory.HUE_GREEN
    }
}

// ✅ NUEVA FUNCIÓN: Obtener color según estado del reporte
fun getMarkerColorByStatus(status: ReportStatus): Float {
    return when (status) {
        ReportStatus.APPROVED -> BitmapDescriptorFactory.HUE_GREEN
        ReportStatus.PENDING -> BitmapDescriptorFactory.HUE_ORANGE
        ReportStatus.REJECTED -> BitmapDescriptorFactory.HUE_RED
    }
}

// Función para obtener el nombre legible del tipo de reporte
fun getReportTypeName(reportType: ReportType): String {
    return when (reportType) {
        ReportType.ROBBERY -> "Robo"
        ReportType.FIRE -> "Incendio"
        ReportType.ACCIDENT -> "Accidente"
        ReportType.SUSPICIOUS_PERSON -> "Persona Sospechosa"
        ReportType.FIGHT -> "Pelea"
        ReportType.VANDALISM -> "Vandalismo"
        ReportType.NOISE -> "Ruido"
        ReportType.LOST_PET -> "Mascota Perdida"
        ReportType.OTHER -> "Otro"
    }
}

// ✅ NUEVA FUNCIÓN: Obtener emoji según estado
fun getStatusEmoji(status: ReportStatus): String {
    return when (status) {
        ReportStatus.APPROVED -> "✅"
        ReportStatus.PENDING -> "⏳"
        ReportStatus.REJECTED -> "❌"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    navController: NavController,
    viewModel: MapViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val mapState by viewModel.mapState.collectAsState()
    val context = LocalContext.current

    var showMenu by remember { mutableStateOf(false) }

    // ✅ CORREGIDO: Sin CDMX por defecto
    var cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            mapState.currentLocation ?: LatLng(0.0, 0.0), // Ubicación neutral
            2f // Zoom global si no hay ubicación
        )
    }

    // ✅ NUEVO: Crear canales de notificación al iniciar
    LaunchedEffect(Unit) {
        NotificationUtils.createNotificationChannels(context)
    }

    // Actualizar cámara cuando cambia la ubicación
    LaunchedEffect(mapState.currentLocation) {
        mapState.currentLocation?.let { location ->
            cameraPositionState.position = CameraPosition.fromLatLngZoom(location, 12f)
        }
    }

    // ✅ CORREGIDO: Cargar TODOS los reportes al iniciar
    LaunchedEffect(Unit) {
        viewModel.loadAllReports() // ✅ Esto carga TODOS los reportes, no solo los del usuario
        viewModel.getCurrentLocation()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (mapState.filterType != null) {
                            "Mapa - ${mapState.filterType}"
                        } else {
                            "Mapa de Alertas Vecinales"
                        }
                    )
                },
                actions = {
                    // Botón de actualizar
                    IconButton(
                        onClick = {
                            viewModel.refreshData()
                            // ✅ NUEVO: Notificación de actualización
                            NotificationUtils.showSimpleNotification(
                                context,
                                "Mapa Actualizado",
                                "Reportes sincronizados correctamente"
                            )
                        }
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualizar")
                    }

                    // Botón de menú
                    IconButton(
                        onClick = { showMenu = true }
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "Menú")
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Perfil") },
                            onClick = {
                                navController.navigate("profile")
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Cerrar Sesión") },
                            onClick = {
                                authViewModel.logout()
                                navController.navigate("welcome") {
                                    popUpTo(0) { inclusive = true }
                                }
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(Icons.Default.LocationOn, contentDescription = null)
                            }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            Box {
                // FAB para crear reporte
                ExtendedFloatingActionButton(
                    onClick = {
                        navController.navigate("create_report")
                    },
                    icon = { Icon(Icons.Default.Add, contentDescription = "Crear reporte") },
                    text = { Text("Reportar") }
                )

                // FAB pequeño para centrar en ubicación actual (solo si hay ubicación)
                if (mapState.currentLocation != null) {
                    FloatingActionButton(
                        onClick = {
                            mapState.currentLocation?.let { location ->
                                cameraPositionState.position = CameraPosition.fromLatLngZoom(location, 15f)
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(bottom = 72.dp)
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Mi ubicación")
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (mapState.isLoading) {
                LoadingIndicator()
            } else {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(
                        isMyLocationEnabled = mapState.isLocationEnabled,
                        isTrafficEnabled = false
                    ),
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = true,
                        compassEnabled = true,
                        myLocationButtonEnabled = false,
                        mapToolbarEnabled = true
                    )
                ) {
                    // Marcador de ubicación actual (solo si existe)
                    mapState.currentLocation?.let { location ->
                        Marker(
                            state = MarkerState(position = location),
                            title = "Mi ubicación",
                            snippet = "Estás aquí",
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                        )
                    }

                    // ✅ CORREGIDO: Mostrar TODOS los reportes APPROVED y PENDING (de todos los usuarios)
                    mapState.reports.forEach { report ->
                        val reportLocation = LatLng(
                            report.latitude,
                            report.longitude
                        )

                        // ✅ SOLO mostrar reportes APPROVED y PENDING (no mostrar REJECTED)
                        if (report.status == ReportStatus.APPROVED || report.status == ReportStatus.PENDING) {
                            Marker(
                                state = MarkerState(position = reportLocation),
                                title = "${getStatusEmoji(report.status)} ${report.title}",
                                snippet = "Por: ${report.userName} - ${getReportTypeName(report.reportType)}",
                                icon = BitmapDescriptorFactory.defaultMarker(
                                    getMarkerColorByStatus(report.status) // ✅ Color por estado
                                ),
                                onInfoWindowClick = {
                                    navController.navigate("report_detail/${report.id}")
                                }
                            )
                        }
                    }
                }

                // ✅ NUEVO: Mostrar mensaje si no hay ubicación
                if (mapState.currentLocation == null && !mapState.isLoading) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "📍 Ubicación no disponible",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                // ✅ NUEVO: Mostrar estadísticas de reportes (de TODOS los usuarios)
                val approvedCount = mapState.reports.count { it.status == ReportStatus.APPROVED }
                val pendingCount = mapState.reports.count { it.status == ReportStatus.PENDING }
                val totalCount = mapState.reports.size

                if ((approvedCount > 0 || pendingCount > 0) && !mapState.isLoading) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "📊 Total: $totalCount | ✅ $approvedCount | ⏳ $pendingCount",
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

                // ✅ NUEVO: Mostrar mensaje si no hay reportes
                if (mapState.reports.isEmpty() && !mapState.isLoading) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "No hay reportes en el mapa\n¡Sé el primero en reportar!",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }

            // Mostrar error si existe
            mapState.error?.let { error ->
                Snackbar(
                    action = {
                        TextButton(
                            onClick = { viewModel.clearError() }
                        ) {
                            Text("OK")
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text(error)
                }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun MapScreenPreview() {
    // Para preview, necesitarías un NavController mock
    // MapScreen(navController = rememberNavController())
}