package mx.edu.utng.alertavecinal.data.model

/*
Clase User: Esta clase representa a un usuario del sistema en el
dominio de la aplicación. Contiene todos los datos del perfil del
usuario incluyendo información personal, ubicación, preferencias
de notificación y rol dentro de la aplicación.
*/

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

