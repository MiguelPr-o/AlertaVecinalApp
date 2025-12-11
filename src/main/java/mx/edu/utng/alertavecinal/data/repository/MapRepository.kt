package mx.edu.utng.alertavecinal.data.repository

/*
Clase MapRepository: Esta clase es el repositorio encargado de toda
la lógica relacionada con ubicación y mapas en la aplicación. Gestiona
la obtención de la ubicación actual del dispositivo usando los servicios
de Google Play, verifica permisos, calcula distancias entre puntos y
proporciona funciones para validar y formatear ubicaciones para mostrar al usuario.
*/

import android.Manifest
import android.content.Context
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await
import mx.edu.utng.alertavecinal.data.model.LocationData
import mx.edu.utng.alertavecinal.utils.PermissionUtils
import javax.inject.Inject

class MapRepository @Inject constructor(
    private val context: Context
) {

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    suspend fun getCurrentLocation(): LocationData? {
        return try {
            if (!hasLocationPermission()) {
                return null
            }

            if (!PermissionUtils.hasLocationPermissions(context)) {
                throw SecurityException("Los permisos de ubicación no fueron concedidos")
            }

            getLocationWithTimeout()

        } catch (securityEx: SecurityException) {
            securityEx.printStackTrace()
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private suspend fun getLocationWithTimeout(): LocationData? {
        return try {
            // checkSelfPermission ANTES de llamar a lastLocation
            val fineLocationPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            val coarseLocationPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )

            // Verificar si al menos un permiso fue concedido
            val hasPermission = fineLocationPermission == android.content.pm.PackageManager.PERMISSION_GRANTED ||
                    coarseLocationPermission == android.content.pm.PackageManager.PERMISSION_GRANTED

            if (!hasPermission) {
                throw SecurityException("Permisos de ubicación requeridos no concedidos")
            }

            // Ahora sí obtener la ubicación
            val location = fusedLocationClient.lastLocation.await()
            location?.toLocationData()

        } catch (securityEx: SecurityException) {
            // Manejo explícito de SecurityException
            securityEx.printStackTrace()
            null
        } catch (e: Exception) {
            // Si lastLocation falla, retornar null
            e.printStackTrace()
            null
        }
    }

    private fun Location.toLocationData(): LocationData {
        return LocationData(
            latitude = this.latitude,
            longitude = this.longitude,
            // Podrías agregar aquí lógica para obtener la dirección si quieres
            address = null, // Por defecto null, puedes obtenerlo después
            timestamp = System.currentTimeMillis()
        )
    }

    private fun hasLocationPermission(): Boolean {
        return PermissionUtils.hasLocationPermissions(context)
    }

    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]
    }

    // Obtener dirección desde coordenadas
    suspend fun getAddressFromLocation(latitude: Double, longitude: Double): String? {
        return try {
            // Implementación simplificada - puedes integrar Geocoder aquí
            // Por ahora retornamos las coordenadas formateadas
            "Ubicación seleccionada: ${String.format("%.6f", latitude)}, ${String.format("%.6f", longitude)}"
        } catch (e: Exception) {
            e.printStackTrace()
            "Ubicación: ${String.format("%.6f", latitude)}, ${String.format("%.6f", longitude)}"
        }
    }

    // Verificar permisos de ubicación de forma segura
    fun checkLocationPermissions(): Boolean {
        return try {
            PermissionUtils.hasLocationPermissions(context)
        } catch (e: SecurityException) {
            false
        }
    }

    // Obtener ubicación con callback para manejar falta de permisos
    suspend fun getCurrentLocationWithCallback(
        onPermissionDenied: () -> Unit = {},
        onLocationUnavailable: () -> Unit = {}
    ): LocationData? {
        return try {
            if (!hasLocationPermission()) {
                onPermissionDenied()
                return null
            }

            val location = getLocationWithTimeout()
            if (location == null) {
                onLocationUnavailable()
            }
            location

        } catch (securityEx: SecurityException) {
            onPermissionDenied()
            null
        } catch (e: Exception) {
            onLocationUnavailable()
            null
        }
    }

    // Obtener ubicación con dirección
    suspend fun getCurrentLocationWithAddress(): LocationData? {
        return try {
            val location = getCurrentLocation()
            location?.let { loc ->
                val address = getAddressFromLocation(loc.latitude, loc.longitude)
                loc.copy(address = address)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Calcular distancia entre dos LocationData
    fun calculateDistanceBetween(location1: LocationData, location2: LocationData): Float {
        return calculateDistance(
            location1.latitude,
            location1.longitude,
            location2.latitude,
            location2.longitude
        )
    }

    // Formatear distancia para mostrar al usuario
    fun formatDistance(distanceInMeters: Float): String {
        return when {
            distanceInMeters < 1000 -> "${String.format("%.0f", distanceInMeters)} m"
            else -> "${String.format("%.1f", distanceInMeters / 1000)} km"
        }
    }

    // Verificar si una ubicación es válida
    fun isValidLocation(latitude: Double, longitude: Double): Boolean {
        return latitude != 0.0 && longitude != 0.0 &&
                latitude >= -90 && latitude <= 90 &&
                longitude >= -180 && longitude <= 180
    }

    // Verificar si un LocationData es válido
    fun isValidLocationData(locationData: LocationData?): Boolean {
        return locationData != null && isValidLocation(locationData.latitude, locationData.longitude)
    }
}