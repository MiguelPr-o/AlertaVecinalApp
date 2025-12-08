package mx.edu.utng.alertavecinal.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import mx.edu.utng.alertavecinal.data.model.UserRole

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val email: String,
    val role: UserRole,
    val address: String?,
    val latitude: Double?,
    val longitude: Double?,
    val phone: String?,
    val createdAt: Long,
    val notificationRadius: Int,
    val notificationsEnabled: Boolean,
    val lastSync: Long = System.currentTimeMillis()
) {
    fun toDomain(): mx.edu.utng.alertavecinal.data.model.User {
        return mx.edu.utng.alertavecinal.data.model.User(
            id = id,
            name = name,
            email = email,
            role = role,
            address = address,
            latitude = latitude,
            longitude = longitude,
            phone = phone,
            createdAt = createdAt,
            notificationRadius = notificationRadius,
            notificationsEnabled = notificationsEnabled
        )
    }
}

// Función de extensión para convertir de dominio a entidad
fun mx.edu.utng.alertavecinal.data.model.User.toEntity(): UserEntity {
    return UserEntity(
        id = id,
        name = name,
        email = email,
        role = role,
        address = address,
        latitude = latitude,
        longitude = longitude,
        phone = phone,
        createdAt = createdAt,
        notificationRadius = notificationRadius,
        notificationsEnabled = notificationsEnabled
    )
}