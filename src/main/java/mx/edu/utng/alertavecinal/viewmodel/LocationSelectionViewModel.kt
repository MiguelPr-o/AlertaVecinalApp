package mx.edu.utng.alertavecinal.viewmodel

/*
Clase LocationSelectionViewModel: ViewModel especializado en la gestión y
selección de ubicaciones dentro de la aplicación. Controla la obtención de
la ubicación actual del dispositivo, permite la selección manual de ubicaciones
en el mapa, y gestiona la geocodificación inversa para obtener direcciones
amigables. Expone un estado observable que refleja la ubicación seleccionada,
la ubicación actual, el estado de carga y posibles errores, proporcionando
una API limpia para cualquier pantalla que requiera selección de ubicación.
*/

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mx.edu.utng.alertavecinal.data.model.LocationSelectionState
import mx.edu.utng.alertavecinal.data.repository.MapRepository
import javax.inject.Inject

@HiltViewModel
class LocationSelectionViewModel @Inject constructor(
    private val mapRepository: MapRepository
) : ViewModel() {

    private val _locationState = MutableStateFlow(LocationSelectionState())
    val locationState: StateFlow<LocationSelectionState> = _locationState.asStateFlow()

    private var _selectedLocation: LatLng? = null

    fun getCurrentLocation() {
        _locationState.value = _locationState.value.copy(isLoading = true)

        viewModelScope.launch {
            try {
                val locationData = mapRepository.getCurrentLocation()
                val latLng = locationData?.let {
                    LatLng(it.latitude, it.longitude)
                }

                _locationState.value = _locationState.value.copy(
                    currentLocation = latLng,
                    isLocationEnabled = locationData != null,
                    isLoading = false,
                    error = null
                )

                // Si no hay ubicación seleccionada, usar la actual por defecto
                if (_selectedLocation == null) {
                    _selectedLocation = latLng
                    _locationState.value = _locationState.value.copy(
                        selectedLocation = latLng
                    )
                }
            } catch (e: Exception) {
                _locationState.value = _locationState.value.copy(
                    isLoading = false,
                    error = "Error al obtener ubicación: ${e.message ?: "Desconocido"}"
                )
            }
        }
    }

    fun setSelectedLocation(location: LatLng) {
        _selectedLocation = location
        _locationState.value = _locationState.value.copy(
            selectedLocation = location,
            error = null
        )

        getAddressForLocation(location)
    }

    fun getSelectedLocation(): LatLng? {
        return _selectedLocation
    }

    fun getSelectedLocationWithAddress(): Pair<LatLng, String>? {
        return _selectedLocation?.let { location ->
            val address = _locationState.value.address ?:
            "Ubicación: ${String.format("%.6f", location.latitude)}, ${String.format("%.6f", location.longitude)}"
            Pair(location, address)
        }
    }

    fun clearError() {
        _locationState.value = _locationState.value.copy(error = null)
    }

    fun setLoading(loading: Boolean) {
        _locationState.value = _locationState.value.copy(isLoading = loading)
    }

    fun resetSelection() {
        _selectedLocation = null
        _locationState.value = _locationState.value.copy(
            selectedLocation = null,
            address = null
        )
    }

    fun useCurrentLocation() {
        _locationState.value.currentLocation?.let { currentLocation ->
            setSelectedLocation(currentLocation)
        }
    }

    private fun getAddressForLocation(location: LatLng) {
        viewModelScope.launch {
            try {
                val address = mapRepository.getAddressFromLocation(
                    location.latitude,
                    location.longitude
                )
                _locationState.value = _locationState.value.copy(
                    address = address
                )
            } catch (e: Exception) {
                // Si falla la geocodificación, no es crítico
                _locationState.value = _locationState.value.copy(
                    address = "Ubicación seleccionada en el mapa"
                )
            }
        }
    }

    /**
     * Verifica si hay una ubicación válida seleccionada
     */
    fun hasValidSelectedLocation(): Boolean {
        return _locationState.value.hasValidSelectedLocation()
    }

    /**
     * Obtiene las coordenadas formateadas de la ubicación seleccionada
     */
    fun getFormattedCoordinates(): String {
        return _locationState.value.getSelectedCoordinates()
    }

    /**
     * Obtiene el estado actual de la ubicación como texto
     */
    fun getLocationStatus(): String {
        return _locationState.value.getLocationStatus()
    }
}