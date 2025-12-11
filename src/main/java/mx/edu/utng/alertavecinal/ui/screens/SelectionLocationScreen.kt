package mx.edu.utng.alertavecinal.ui.screens

/*
Clase SelectLocationScreen: Esta pantalla proporciona un mapa interactivo
que permite a los usuarios seleccionar manualmente una ubicación específica
para sus reportes. Los usuarios pueden tocar cualquier punto del mapa para
establecer una ubicación, ver las coordenadas seleccionadas y confirmar la
selección para utilizarla en sus reportes de incidentes.
*/

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
import androidx.compose.runtime.getValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectLocationScreen(
    navController: NavController,
    onLocationSelected: (LatLng) -> Unit
) {
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    val defaultLocation = LatLng(19.4326, -99.1332)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 12f)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Seleccionar Ubicación") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        },
        floatingActionButton = {
            Box {
                if (selectedLocation != null) {
                    ExtendedFloatingActionButton(
                        onClick = {
                            println("📍 DEBUG SelectLocation: Botón Confirmar PRESIONADO")
                            selectedLocation?.let { location ->
                                println("📍 DEBUG SelectLocation: Ubicación seleccionada: $location")

                                navController.previousBackStackEntry?.savedStateHandle?.set(
                                    "selected_location",
                                    location
                                )

                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    "selected_location",
                                    location
                                )

                                println("📍 DEBUG SelectLocation: Ubicación GUARDADA en savedStateHandle")

                                navController.popBackStack()
                                println("📍 DEBUG SelectLocation: Navegación completada")
                            } ?: run {
                                error = "Primero selecciona una ubicación en el mapa"
                            }
                        },
                        icon = { Icon(Icons.Default.Check, contentDescription = "Confirmar") },
                        text = { Text("Confirmar Ubicación") },
                        modifier = Modifier.padding(bottom = 72.dp)
                    )
                }

                FloatingActionButton(
                    onClick = {
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(defaultLocation, 15f)
                        selectedLocation = defaultLocation
                        error = null
                    },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(Icons.Default.MyLocation, contentDescription = "Centrar mapa")
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    isMyLocationEnabled = false,
                    isTrafficEnabled = false
                ),
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = true,
                    compassEnabled = true,
                    myLocationButtonEnabled = false,
                    mapToolbarEnabled = true
                ),
                onMapClick = { latLng ->
                    selectedLocation = latLng
                    error = null
                    println("📍 DEBUG SelectLocation: Mapa clickeado en: $latLng")
                }
            ) {
                if (selectedLocation != null) {
                    Marker(
                        state = MarkerState(position = selectedLocation!!),
                        title = "Ubicación seleccionada",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                    )
                }
            }

            Card(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (selectedLocation != null) {
                            "📍 Ubicación seleccionada"
                        } else {
                            "Toca el mapa para seleccionar ubicación"
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )

                    if (selectedLocation != null) {
                        Text(
                            text = "Coordenadas: ${String.format("%.6f", selectedLocation!!.latitude)}, ${String.format("%.6f", selectedLocation!!.longitude)}",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            if (error != null) {
                Snackbar(
                    action = {
                        TextButton(onClick = { error = null }) {
                            Text("OK")
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text(error!!)
                }
            }
        }
    }
}