package mx.edu.utng.alertavecinal.data.model

/*
Clase LocationData: Esta clase representa una ubicación geográfica
dentro de la aplicación, almacenando coordenadas de latitud y longitud
junto con una dirección opcional. Proporciona métodos para convertir
entre formatos de cadena de texto y objetos, facilitando el manejo
de ubicaciones en toda la aplicación.
 */

data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val address: String? = null,
    val timestamp: Long = System.currentTimeMillis()
) {
    companion object {
        fun fromString(locationString: String): LocationData? {
            return try {
                val parts = locationString.split(",")
                if (parts.size == 2) {
                    LocationData(
                        latitude = parts[0].toDouble(),
                        longitude = parts[1].toDouble()
                    )
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    fun toStringFormat(): String {
        return "$latitude,$longitude"
    }
}