package mx.edu.utng.alertavecinal.data.model

import com.google.android.gms.maps.model.LatLng

/*
Clase LocationSelectionState: Esta clase representa el estado
de selección de ubicación en la aplicación, gestionando tanto
la ubicación actual del usuario como una ubicación seleccionada
manualmente en el mapa. Proporciona métodos para validar ubicaciones,
calcular distancias y generar descripciones amigables para mostrar al usuario.
 */

data class LocationSelectionState(
    val currentLocation: LatLng? = null,
    val selectedLocation: LatLng? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLocationEnabled: Boolean = false,
    val address: String? = null
) {

    fun hasValidSelectedLocation(): Boolean {
        return selectedLocation != null &&
                selectedLocation.latitude != 0.0 &&
                selectedLocation.longitude != 0.0
    }

    fun hasValidCurrentLocation(): Boolean {
        return currentLocation != null &&
                currentLocation.latitude != 0.0 &&
                currentLocation.longitude != 0.0
    }

    fun getSelectedCoordinates(): String {
        return selectedLocation?.let { location ->
            "${String.format("%.6f", location.latitude)}, ${String.format("%.6f", location.longitude)}"
        } ?: "No seleccionada"
    }

    fun getCurrentCoordinates(): String {
        return currentLocation?.let { location ->
            "${String.format("%.6f", location.latitude)}, ${String.format("%.6f", location.longitude)}"
        } ?: "No disponible"
    }

    fun calculateDistanceFromCurrent(): Float {
        if (!hasValidCurrentLocation() || !hasValidSelectedLocation()) {
            return 0f
        }

        val current = currentLocation!!
        val selected = selectedLocation!!

        val results = FloatArray(1)
        android.location.Location.distanceBetween(
            current.latitude,
            current.longitude,
            selected.latitude,
            selected.longitude,
            results
        )

        return results[0]
    }

    fun getDistanceDescription(): String {
        val distance = calculateDistanceFromCurrent()
        return when {
            distance == 0f -> "Misma ubicación"
            distance < 1000 -> "${String.format("%.0f", distance)} metros"
            else -> "${String.format("%.1f", distance / 1000)} km"
        }
    }

    fun isSelectedLocationNearCurrent(): Boolean {
        return calculateDistanceFromCurrent() < 100f
    }

    fun getLocationStatus(): String {
        return when {
            isLoading -> "Obteniendo ubicación..."
            error != null -> "Error: $error"
            hasValidSelectedLocation() -> {
                if (isSelectedLocationNearCurrent() && hasValidCurrentLocation()) {
                    "📍 Cerca de tu ubicación actual"
                } else {
                    "📍 Ubicación seleccionada"
                }
            }
            hasValidCurrentLocation() -> "📍 Usando ubicación actual"
            else -> "🌍 Selecciona una ubicación en el mapa"
        }
    }
}