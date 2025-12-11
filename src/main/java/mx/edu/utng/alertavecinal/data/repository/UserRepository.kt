package mx.edu.utng.alertavecinal.data.repository

/*
Clase UserRepository: Esta clase es el repositorio encargado de gestionar
todas las operaciones relacionadas con usuarios en la aplicación. Maneja
la sincronización de datos de usuario entre Firebase Firestore (base de
datos en la nube) y la base de datos local Room, incluyendo actualización
de ubicación, preferencias de notificación, perfil de usuario y obtención
de información de múltiples usuarios.
*/

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import mx.edu.utng.alertavecinal.data.local.AppDatabase
import mx.edu.utng.alertavecinal.data.local.toEntity
import mx.edu.utng.alertavecinal.data.model.User
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val database: AppDatabase
) {

    fun getCurrentUser(userId: String): Flow<User?> {
        return database.userDao().getUser(userId).map { entity ->
            entity?.toDomain()
        }
    }

    suspend fun updateUserLocation(
        userId: String,
        latitude: Double,
        longitude: Double,
        address: String? = null
    ): Result<Boolean> {
        return try {
            // Actualizar en Firestore
            val updateData = mapOf(
                "latitude" to latitude,
                "longitude" to longitude,
                "address" to address
            )

            firestore.collection("users").document(userId)
                .update(updateData)
                .await()

            // Actualizar localmente
            val userEntity = database.userDao().getUser(userId)
            userEntity.collect { entity ->
                entity?.let {
                    val updatedEntity = it.copy(
                        latitude = latitude,
                        longitude = longitude,
                        address = address
                    )
                    database.userDao().updateUser(updatedEntity)
                }
            }

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateNotificationSettings(
        userId: String,
        radius: Int,
        enabled: Boolean
    ): Result<Boolean> {
        return try {
            val updateData = mapOf(
                "notificationRadius" to radius,
                "notificationsEnabled" to enabled
            )

            firestore.collection("users").document(userId)
                .update(updateData)
                .await()

            // Actualizar localmente
            database.userDao().updateNotificationRadius(userId, radius)
            database.userDao().updateNotificationsEnabled(userId, enabled)

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserById(userId: String): User? {
        return try {
            val document = firestore.collection("users").document(userId).get().await()
            if (document.exists()) {
                document.toObject(User::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateUserProfile(userId: String, name: String, phone: String?): Result<Boolean> {
        return try {
            val updateData = mutableMapOf<String, Any>(
                "name" to name
            )

            phone?.let {
                updateData["phone"] = it
            }

            firestore.collection("users").document(userId)
                .update(updateData)
                .await()

            // Actualizar localmente
            val userEntity = database.userDao().getUser(userId)
            userEntity.collect { entity ->
                entity?.let {
                    val updatedEntity = it.copy(
                        name = name,
                        phone = phone
                    )
                    database.userDao().updateUser(updatedEntity)
                }
            }

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllUsers(): List<User> {
        return try {
            val snapshot = firestore.collection("users").get().await()
            snapshot.toObjects(User::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun createOrUpdateUser(user: User): Result<Boolean> {
        return try {
            firestore.collection("users").document(user.id)
                .set(user)
                .await()

            // Guardar localmente
            database.userDao().insertUser(user.toEntity())

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}