package mx.edu.utng.alertavecinal.data.local

/*
Clase NotificationEntity: Esta clase define la estructura de una
notificación que se almacena en la base de datos local del dispositivo.
Representa la tabla "notifications" en SQLite y contiene todos los datos
necesarios para mostrar y gestionar notificaciones dentro de la aplicación,
incluyendo su tipo, estado de lectura y metadatos asociados.
 */

import androidx.room.Entity
import androidx.room.PrimaryKey
import mx.edu.utng.alertavecinal.data.model.NotificationType

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val title: String,
    val message: String,
    val type: NotificationType,
    val reportId: String?,
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val data: String? = null
)