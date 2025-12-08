package mx.edu.utng.alertavecinal.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import com.google.android.gms.maps.model.LatLng
import mx.edu.utng.alertavecinal.data.model.ReportType
import mx.edu.utng.alertavecinal.ui.components.CustomButton
import mx.edu.utng.alertavecinal.ui.components.CustomTextField
import mx.edu.utng.alertavecinal.ui.components.ErrorMessage
import mx.edu.utng.alertavecinal.utils.Constants
import mx.edu.utng.alertavecinal.viewmodel.AuthViewModel
import mx.edu.utng.alertavecinal.viewmodel.MapViewModel
import mx.edu.utng.alertavecinal.viewmodel.ReportViewModel
import kotlin.String

// Estado local para el formulario de creación de reportes
data class CreateReportFormState(
    val adrees:String="",
    val title: String = "",
    val description: String = "",
    val selectedType: ReportType? = null,
    val currentLocation: String? = null,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val isLoadingLocation: Boolean = false,
    val locationError: String? = null,
    val isSubmitted: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateReportScreen(
    navController: NavController,
    reportViewModel: ReportViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    mapViewModel: MapViewModel = hiltViewModel()
) {
    val reportState by reportViewModel.reportState.collectAsState()
    val authState by authViewModel.authState.collectAsState()
    val mapState by mapViewModel.mapState.collectAsState()
    val context = LocalContext.current

    // Estado local del formulario
    var formState by remember {
        mutableStateOf(CreateReportFormState())
    }

    var isTypeExpanded by remember { mutableStateOf(false) }
    var showSuccessMessage by remember { mutableStateOf(false) }
    var locationAttempts by remember { mutableStateOf(0) }

    // ✅✅✅ SOLO CAMBIO: Recuperar datos del ViewModel al iniciar
    LaunchedEffect(Unit) {
        val savedState = reportViewModel.createReportState
        if (savedState.title.isNotEmpty() || savedState.description.isNotEmpty() || savedState.selectedType != null) {
            formState = formState.copy(

                title = savedState.title,
                description = savedState.description,
                selectedType = savedState.selectedType,
                currentLocation = savedState.currentLocation,
                latitude = savedState.latitude,
                longitude = savedState.longitude
            )
        }
    }

    // ✅✅✅ SOLUCIÓN EXTREMA: Función para verificar la ubicación seleccionada
    fun checkForSelectedLocation() {
        println("📍 DEBUG CreateReport: Verificando ubicación seleccionada...")

        // ✅ VERIFICAR EN MÚLTIPLES LUGARES
        val latLngFromPrevious = navController.previousBackStackEntry?.savedStateHandle?.get<LatLng>("selected_location")
        val latLngFromCurrent = navController.currentBackStackEntry?.savedStateHandle?.get<LatLng>("selected_location")

        val latLng = latLngFromPrevious ?: latLngFromCurrent

        if (latLng != null) {
            println("📍 DEBUG CreateReport: ¡UBICACIÓN ENCONTRADA! ${latLng.latitude}, ${latLng.longitude}")

            formState = formState.copy(
                currentLocation = "Ubicación seleccionada en el mapa",
                latitude = latLng.latitude,
                longitude = latLng.longitude,
                locationError = null,
                isLoadingLocation = false
            )

            // ✅✅✅ SOLO CAMBIO: Guardar también en ViewModel
            reportViewModel.updateCreateReportState(

                title = formState.title,
                description = formState.description,
                selectedType = formState.selectedType,
                currentLocation = "Ubicación seleccionada en el mapa",
                latitude = latLng.latitude,
                longitude = latLng.longitude
            )

            // Limpiar TODOS los lugares donde podría estar guardada
            navController.previousBackStackEntry?.savedStateHandle?.set("selected_location", null)
            navController.currentBackStackEntry?.savedStateHandle?.set("selected_location", null)

            println("📍 DEBUG CreateReport: Ubicación PROCESADA y LIMPIADA")
        } else {
            println("📍 DEBUG CreateReport: No se encontró ubicación seleccionada")
        }
    }

    // ✅✅✅ SOLUCIÓN EXTREMA: Verificar la ubicación CADA VEZ que la pantalla se enfoque
    LaunchedEffect(Unit) {
        // Verificar inmediatamente al cargar la pantalla
        checkForSelectedLocation()
    }

    // También verificar cuando la pantalla recibe foco
    LaunchedEffect(navController.currentBackStackEntry) {
        // Este se ejecuta cuando regresas de otra pantalla
        checkForSelectedLocation()
    }

    // VERIFICACIÓN ROBUSTA DEL USUARIO
    LaunchedEffect(Unit) {
        authViewModel.checkCurrentUser()
    }

    // Obtener ubicación cuando el usuario la solicita
    LaunchedEffect(locationAttempts) {
        if (locationAttempts > 0) {
            mapViewModel.getCurrentLocation()
        }
    }

    // Obtener usuario actual de manera segura
    val currentUser = authState.currentUser
    val userId = currentUser?.id ?: ""
    val userName = currentUser?.name ?: "Usuario"

    // Detectar problema de autenticación
    val hasAuthProblem = authState.isAuthenticated && currentUser == null

    // Usar ubicación real del MapViewModel
    LaunchedEffect(mapState.currentLocation) {
        mapState.currentLocation?.let { location ->
            // SOLO actualizar si no hay una ubicación manual seleccionada
            if (formState.currentLocation != "Ubicación seleccionada en el mapa") {
                formState = formState.copy(
                    currentLocation = "Ubicación actual obtenida",
                    latitude = location.latitude,
                    longitude = location.longitude,
                    locationError = null,
                    isLoadingLocation = false
                )

                // ✅✅✅ SOLO CAMBIO: Guardar también en ViewModel
                reportViewModel.updateCreateReportState(
                    title = formState.title,
                    description = formState.description,
                    selectedType = formState.selectedType,
                    currentLocation = "Ubicación actual obtenida",
                    latitude = location.latitude,
                    longitude = location.longitude
                )
            }
        }
    }

    // Manejar errores de ubicación mejor
    LaunchedEffect(mapState.error) {
        mapState.error?.let { error ->
            if (error.contains("ubicación", ignoreCase = true) ||
                error.contains("location", ignoreCase = true)) {
                formState = formState.copy(
                    locationError = "No se pudo obtener la ubicación. Verifica que tengas los permisos activados y el GPS encendido.",
                    isLoadingLocation = false
                )
            }
        }
    }

    // Navegar de regreso si el reporte se creó exitosamente
    LaunchedEffect(reportState.reports) {
        if (formState.isSubmitted && reportState.error == null && !reportState.isLoading) {
            showSuccessMessage = true
            kotlinx.coroutines.delay(1500)
            navController.popBackStack()
        }
    }

    // Limpiar error cuando se desmonta el componente
    LaunchedEffect(Unit) {
        reportViewModel.clearError()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reportar Incidente") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Título
                Text(
                    text = "Nuevo Reporte",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )

                Text(
                    text = "Completa la información del incidente",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // MOSTRAR ERROR DE AUTENTICACIÓN SI HAY PROBLEMA
                if (hasAuthProblem) {
                    ErrorMessage(message = authState.error ?: "Problema con tu sesión. Por favor, cierra sesión y vuelve a iniciar.")

                    CustomButton(
                        text = "Cerrar Sesión y Volver a Login",
                        onClick = {
                            authViewModel.logout()
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = MaterialTheme.colorScheme.error
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Solo mostrar el formulario si no hay problemas de autenticación
                if (!hasAuthProblem) {
                    // Campo de tipo de incidente
                    Text(
                        text = "Tipo de Incidente *",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    ExposedDropdownMenuBox(
                        expanded = isTypeExpanded,
                        onExpandedChange = { isTypeExpanded = !isTypeExpanded }
                    ) {
                        TextField(
                            value = formState.selectedType?.let { getReportTypeDisplayName(it) } ?: "Selecciona un tipo",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isTypeExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            leadingIcon = {
                                Icon(Icons.Default.Warning, contentDescription = "Tipo")
                            },
                            placeholder = {
                                Text("Selecciona el tipo de incidente")
                            },
                            isError = formState.selectedType == null && formState.isSubmitted
                        )

                        ExposedDropdownMenu(
                            expanded = isTypeExpanded,
                            onDismissRequest = { isTypeExpanded = false }
                        ) {
                            ReportType.values().forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(getReportTypeDisplayName(type)) },
                                    onClick = {
                                        formState = formState.copy(selectedType = type)
                                        // ✅✅✅ SOLO CAMBIO: Guardar en ViewModel
                                        reportViewModel.updateCreateReportState(
                                            title = formState.title,
                                            description = formState.description,
                                            selectedType = type,
                                            currentLocation = formState.currentLocation,
                                            latitude = formState.latitude,
                                            longitude = formState.longitude
                                        )
                                        isTypeExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Mostrar error si no se seleccionó tipo
                    if (formState.selectedType == null && formState.isSubmitted) {
                        Text(
                            text = "Debes seleccionar un tipo de incidente",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    // Campo de título
                    CustomTextField(
                        value = formState.title,
                        onValueChange = { newTitle ->
                            formState = formState.copy(title = newTitle)
                            // ✅✅✅ SOLO CAMBIO: Guardar en ViewModel
                            reportViewModel.updateCreateReportState(
                                title = newTitle,
                                description = formState.description,
                                selectedType = formState.selectedType,
                                currentLocation = formState.currentLocation,
                                latitude = formState.latitude,
                                longitude = formState.longitude
                            )
                        },
                        label = "Título del reporte *",
                        leadingIcon = Icons.Default.Description,
                        modifier = Modifier.fillMaxWidth(),
                        isError = formState.title.isEmpty() && formState.isSubmitted
                    )

                    // Mostrar error si el título está vacío
                    if (formState.title.isEmpty() && formState.isSubmitted) {
                        Text(
                            text = "El título es obligatorio",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    // Campo de descripción
                    CustomTextField(
                        value = formState.description,
                        onValueChange = { newDescription ->
                            formState = formState.copy(description = newDescription)
                            // ✅✅✅ SOLO CAMBIO: Guardar en ViewModel
                            reportViewModel.updateCreateReportState(
                                title = formState.title,
                                description = newDescription,
                                selectedType = formState.selectedType,
                                currentLocation = formState.currentLocation,
                                latitude = formState.latitude,
                                longitude = formState.longitude
                            )
                        },
                        label = "Descripción detallada *",
                        leadingIcon = Icons.Default.Description,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        isError = formState.description.isEmpty() && formState.isSubmitted
                    )

                    // Mostrar error si la descripción está vacía
                    if (formState.description.isEmpty() && formState.isSubmitted) {
                        Text(
                            text = "La descripción es obligatoria",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    // Sección de ubicación
                    Text(
                        text = "Ubicación *",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Botón de ubicación actual con mejor feedback
                    CustomButton(
                        text = when {
                            formState.isLoadingLocation -> "Obteniendo ubicación..."
                            formState.currentLocation != null -> "📍 ${formState.currentLocation}"
                            formState.locationError != null -> "⚠️ Reintentar ubicación automática"
                            else -> "Usar mi ubicación actual"
                        },
                        onClick = {
                            locationAttempts++
                        },
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = when {
                            formState.currentLocation == "Ubicación seleccionada en el mapa" -> MaterialTheme.colorScheme.tertiary
                            formState.currentLocation != null -> MaterialTheme.colorScheme.primary
                            formState.locationError != null -> MaterialTheme.colorScheme.errorContainer
                            else -> MaterialTheme.colorScheme.secondary
                        },
                        enabled = !formState.isLoadingLocation,
                        isLoading = formState.isLoadingLocation
                    )

                    // ✅✅✅ CORREGIDO: Botón para elegir ubicación en el mapa
                    CustomButton(
                        text = "🗺️ Elegir ubicación en el mapa",
                        onClick = {
                            // ✅ GUARDAR los datos ANTES de navegar
                            reportViewModel.updateCreateReportState(
                                title = formState.title,
                                description = formState.description,
                                selectedType = formState.selectedType,
                                currentLocation = formState.currentLocation,
                                latitude = formState.latitude,
                                longitude = formState.longitude
                            )
                            // ✅ NAVEGAR a la ruta SIMPLE (sin parámetros)
                            navController.navigate(Constants.ROUTE_SELECT_LOCATION)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = MaterialTheme.colorScheme.tertiary,
                        leadingIcon = Icons.Default.Map
                    )

                    // Mostrar ubicación actual si está disponible
                    formState.currentLocation?.let { location ->
                        Column(
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text(
                                text = "📍 $location",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )

                            // Mostrar coordenadas reales
                            Text(
                                text = "Coordenadas: ${String.format("%.6f", formState.latitude)}, ${String.format("%.6f", formState.longitude)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }

                    // Mostrar mensaje de error más específico
                    formState.locationError?.let { error ->
                        Column(
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text(
                                text = error,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )

                            TextButton(
                                onClick = {
                                    val intent = android.content.Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                                    context.startActivity(intent)
                                }
                            ) {
                                Text("Abrir configuración de ubicación")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Mostrar error del reporte si existe
                    reportState.error?.let { error ->
                        ErrorMessage(message = error)
                    }

                    // Botón de enviar reporte
                    CustomButton(
                        text = if (reportState.isLoading) "Enviando..." else "Enviar Reporte",
                        onClick = {
                            if (formState.title.isNotEmpty() &&
                                formState.description.isNotEmpty() &&
                                formState.selectedType != null &&
                                formState.currentLocation != null) {

                                formState = formState.copy(isSubmitted = true)
                                reportViewModel.createReport(
                                    title = formState.title,
                                    description = formState.description,
                                    reportType = formState.selectedType!!,
                                    latitude = formState.latitude,
                                    longitude = formState.longitude,
                                    address = formState.currentLocation,
                                    imageUrl = null,
                                    userId = userId,
                                    userName = userName
                                )
                            } else {
                                formState = formState.copy(isSubmitted = true)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = formState.title.isNotEmpty() &&
                                formState.description.isNotEmpty() &&
                                formState.selectedType != null &&
                                formState.currentLocation != null &&
                                !reportState.isLoading,
                        isLoading = reportState.isLoading
                    )

                    // Loading
                    if (reportState.isLoading) {
                        Spacer(modifier = Modifier.height(16.dp))
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(48.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }

            // Snackbar para éxito
            if (showSuccessMessage) {
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text("✅ Reporte creado exitosamente")
                }
            }
        }
    }
}

// Función auxiliar para obtener nombre legible del tipo de reporte
private fun getReportTypeDisplayName(reportType: ReportType): String {
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

@Preview(showBackground = true)
@Composable
fun CreateReportScreenPreview() {
    CreateReportScreen(navController = rememberNavController())
}