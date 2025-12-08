package mx.edu.utng.alertavecinal.data.model

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