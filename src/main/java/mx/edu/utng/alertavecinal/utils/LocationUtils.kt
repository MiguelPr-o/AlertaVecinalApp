package mx.edu.utng.alertavecinal.utils

/*
Clase LocationUtils: Este objeto proporciona funciones especializadas
para el cálculo y procesamiento de ubicaciones geográficas en la aplicación,
incluyendo cálculo de distancias, verificación de radios, conversión entre
formatos de ubicación, validación de coordenadas y cálculos de bounding boxes.
Sirve como capa de utilidad para todas las operaciones relacionadas con
geolocalización en el sistema.
*/

import android.location.Location
import com.google.android.gms.maps.model.LatLng

object LocationUtils {

    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]
    }

    fun isLocationInRadius(
        userLat: Double,
        userLon: Double,
        targetLat: Double,
        targetLon: Double,
        radiusMeters: Int
    ): Boolean {
        val distance = calculateDistance(userLat, userLon, targetLat, targetLon)
        return distance <= radiusMeters
    }

    fun calculateCenter(locations: List<LatLng>): LatLng {
        if (locations.isEmpty()) return LatLng(0.0, 0.0)

        var sumLat = 0.0
        var sumLng = 0.0

        locations.forEach { location ->
            sumLat += location.latitude
            sumLng += location.longitude
        }

        return LatLng(sumLat / locations.size, sumLng / locations.size)
    }

    fun locationToLatLng(location: Location): LatLng {
        return LatLng(location.latitude, location.longitude)
    }

    fun latLngToLocation(latLng: LatLng): Location {
        return Location("").apply {
            latitude = latLng.latitude
            longitude = latLng.longitude
        }
    }

    fun formatCoordinates(latitude: Double, longitude: Double): String {
        val latDirection = if (latitude >= 0) "N" else "S"
        val lonDirection = if (longitude >= 0) "E" else "O"

        return "%.6f°$latDirection, %.6f°$lonDirection".format(
            Math.abs(latitude),
            Math.abs(longitude)
        )
    }

    fun isValidCoordinates(latitude: Double, longitude: Double): Boolean {
        return latitude in -90.0..90.0 && longitude in -180.0..180.0
    }

    fun calculateZoomLevel(radiusMeters: Int): Float {
        return when {
            radiusMeters <= 500 -> 16f
            radiusMeters <= 1000 -> 15f
            radiusMeters <= 2000 -> 14f
            radiusMeters <= 5000 -> 13f
            else -> 12f
        }
    }

    fun getRadiusForAreaType(areaType: String): Int {
        return when (areaType) {
            "street" -> 200
            "neighborhood" -> 1000
            "district" -> 3000
            "city" -> 10000
            else -> 1000 // Por defecto
        }
    }

    fun calculateBoundingBox(
        centerLat: Double,
        centerLon: Double,
        radiusMeters: Int
    ): Pair<LatLng, LatLng> {
        val latDelta = (radiusMeters / 111320.0) // 1 grado ≈ 111.32 km
        val lonDelta = (radiusMeters / (111320.0 * Math.cos(Math.toRadians(centerLat))))

        val southwest = LatLng(centerLat - latDelta, centerLon - lonDelta)
        val northeast = LatLng(centerLat + latDelta, centerLon + lonDelta)

        return Pair(southwest, northeast)
    }
}