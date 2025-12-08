package mx.edu.utng.alertavecinal.data.model

import com.google.android.gms.maps.model.LatLng

data class MapState(
    val currentLocation: LatLng? = null,
    val reports: List<Report> = emptyList(),
    val selectedReport: Report? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val filterType: String? = null,
    val isLocationEnabled: Boolean = false
)