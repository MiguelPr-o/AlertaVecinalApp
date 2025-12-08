package mx.edu.utng.alertavecinal.data.model

import com.google.android.gms.maps.model.LatLng

/**
 * Estado para la pantalla de selección de ubicación
 * Maneja la ubicación actual, la seleccionada por el usuario, y estados de carga/error
 */
data class LocationSelectionState(
    val currentLocation: LatLng? = null,
    val selectedLocation: LatLng? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLocationEnabled: Boolean = false,
    val address: String? = null
) {
    /**
     * Verifica si hay una ubicación seleccionada válida
     */
    fun hasValidSelectedLocation(): Boolean {
        return selectedLocation != null &&
                selectedLocation.latitude != 0.0 &&
                selectedLocation.longitude != 0.0
    }

    /**
     * Verifica si hay una ubicación actual válida
     */
    fun hasValidCurrentLocation(): Boolean {
        return currentLocation != null &&
                currentLocation.latitude != 0.0 &&
                currentLocation.longitude != 0.0
    }

    /**
     * Obtiene las coordenadas de la ubicación seleccionada como string
     */
    fun getSelectedCoordinates(): String {
        return selectedLocation?.let { location ->
            "${String.format("%.6f", location.latitude)}, ${String.format("%.6f", location.longitude)}"
        } ?: "No seleccionada"
    }

    /**
     * Obtiene las coordenadas de la ubicación actual como string
     */
    fun getCurrentCoordinates(): String {
        return currentLocation?.let { location ->
            "${String.format("%.6f", location.latitude)}, ${String.format("%.6f", location.longitude)}"
        } ?: "No disponible"
    }

    /**
     * Calcula la distancia entre la ubicación actual y la seleccionada en metros
     */
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

    /**
     * Obtiene una descripción amigable de la distancia
     */
    fun getDistanceDescription(): String {
        val distance = calculateDistanceFromCurrent()
        return when {
            distance == 0f -> "Misma ubicación"
            distance < 1000 -> "${String.format("%.0f", distance)} metros"
            else -> "${String.format("%.1f", distance / 1000)} km"
        }
    }

    /**
     * Verifica si la ubicación seleccionada está cerca de la actual (menos de 100m)
     */
    fun isSelectedLocationNearCurrent(): Boolean {
        return calculateDistanceFromCurrent() < 100f
    }

    /**
     * Obtiene el estado de la ubicación como texto descriptivo
     */
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