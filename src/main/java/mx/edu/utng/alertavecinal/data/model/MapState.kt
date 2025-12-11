package mx.edu.utng.alertavecinal.data.model

import com.google.android.gms.maps.model.LatLng

/*
Clase MapState: Esta clase representa el estado del mapa en la aplicación,
almacenando la ubicación actual del usuario, los reportes visibles en
el mapa, el reporte seleccionado, y el estado de carga o error. Sirve
como contenedor de datos para gestionar y actualizar la interfaz del
mapa de manera reactiva.
 */


data class MapState(
    val currentLocation: LatLng? = null,
    val reports: List<Report> = emptyList(),
    val selectedReport: Report? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val filterType: String? = null,
    val isLocationEnabled: Boolean = false
)