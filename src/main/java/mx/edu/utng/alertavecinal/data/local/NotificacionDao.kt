package mx.edu.utng.alertavecinal.data.local

/*
Clase NotificationDao: Es una interfaz DAO (Data Access Object) que define
todas las operaciones de base de datos relacionadas con las notificaciones.
Proporciona métodos para insertar, actualizar, eliminar y consultar
notificaciones, permitiendo gestionar el estado de lectura y obtener conteos
de notificaciones no leídas para cada usuario.
 */


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {

    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY createdAt DESC")
    fun getUserNotifications(userId: String): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notifications WHERE userId = :userId AND isRead = 0 ORDER BY createdAt DESC")
    fun getUnreadNotifications(userId: String): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Update
    suspend fun updateNotification(notification: NotificationEntity)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :notificationId")
    suspend fun markAsRead(notificationId: String)

    @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId")
    suspend fun markAllAsRead(userId: String)

    @Query("DELETE FROM notifications WHERE id = :notificationId")
    suspend fun deleteNotification(notificationId: String)

    @Query("DELETE FROM notifications WHERE userId = :userId")
    suspend fun deleteUserNotifications(userId: String)

    @Query("SELECT COUNT(*) FROM notifications WHERE userId = :userId AND isRead = 0")
    fun getUnreadCount(userId: String): Flow<Int>
}