package mx.edu.utng.alertavecinal.data.local

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