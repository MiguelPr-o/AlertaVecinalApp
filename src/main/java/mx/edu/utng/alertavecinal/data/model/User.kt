package mx.edu.utng.alertavecinal.data.model

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val role: UserRole = UserRole.USER,
    val address: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val phone: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val notificationRadius: Int = 1000, // Radio en metros (1km por defecto)
    val notificationsEnabled: Boolean = true
)

