package mx.edu.utng.alertavecinal.data.local

/*
Clase UserDao: Es una interfaz DAO que define las operaciones de
base de datos local relacionadas con los usuarios. Maneja el almacenamiento
y recuperación de información de usuario en el caché local, permitiendo
acceso rápido a datos del perfil y configuración sin necesidad de consultar
constantemente Firebase.
*/

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUser(userId: String): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUser(userId: String)

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("UPDATE users SET notificationRadius = :radius WHERE id = :userId")
    suspend fun updateNotificationRadius(userId: String, radius: Int)

    @Query("UPDATE users SET notificationsEnabled = :enabled WHERE id = :userId")
    suspend fun updateNotificationsEnabled(userId: String, enabled: Boolean)

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<UserEntity>
}