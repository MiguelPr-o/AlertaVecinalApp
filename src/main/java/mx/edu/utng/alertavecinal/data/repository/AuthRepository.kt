package mx.edu.utng.alertavecinal.data.repository

/*
Clase AuthRepository: Esta clase es el repositorio central que
maneja todas las operaciones de autenticación y gestión de usuarios
de la aplicación. Se encarga de comunicarse con Firebase Authentication
para el login y registro, con Firestore para almacenar y recuperar
datos de usuario, y con la base de datos local para caché, sincronizando
los tres sistemas para mantener la consistencia de datos del usuario.
*/

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import mx.edu.utng.alertavecinal.data.local.AppDatabase
import mx.edu.utng.alertavecinal.data.local.toEntity
import mx.edu.utng.alertavecinal.data.model.User
import mx.edu.utng.alertavecinal.data.model.UserRole
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val database: AppDatabase
) {

    suspend fun login(email: String, password: String): Result<User> {
        return try {
            println("🔍 DEBUG AuthRepository - Iniciando login para: $email")
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user

            if (firebaseUser != null) {
                println("🔍 DEBUG AuthRepository - Usuario autenticado en Firebase Auth: ${firebaseUser.uid}")
                val user = getUserFromFirestore(firebaseUser.uid)

                // Si el usuario no existe en Firestore, FALLAR con mensaje claro
                if (user == null) {
                    println("🔴 DEBUG AuthRepository - ERROR: Usuario autenticado pero no existe en Firestore")
                    println("🔴 DEBUG AuthRepository - UserId: ${firebaseUser.uid}, Email: $email")

                    // Cerrar sesión para limpiar estado inconsistente
                    auth.signOut()

                    return Result.failure(Exception(
                        "Tu cuenta fue autenticada pero no tiene perfil completo. " +
                                "Esto puede pasar si la cuenta fue creada directamente en Firebase Console. " +
                                "Por favor, regístrate nuevamente o contacta al administrador."
                    ))
                }

                // Usuario existe - guardar localmente
                println("🟢 DEBUG AuthRepository - Login exitoso: ${user.name} (${user.email})")
                database.userDao().insertUser(user.toEntity())

                Result.success(user)
            } else {
                println("🔴 DEBUG AuthRepository - Error: firebaseUser es null")
                Result.failure(Exception("Error en autenticación - usuario no encontrado"))
            }
        } catch (e: Exception) {
            println("🔴 DEBUG AuthRepository - Error en login: ${e.message}")
            Result.failure(Exception("Error al iniciar sesión: ${e.message}"))
        }
    }

    suspend fun register(
        name: String,
        email: String,
        password: String,
        address: String? = null,
        latitude: Double? = null,
        longitude: Double? = null
    ): Result<User> {
        return try {
            println("🔍 DEBUG AuthRepository - Iniciando registro para: $email")

            // Crear usuario en Firebase Auth
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user

            if (firebaseUser != null) {
                println("🔍 DEBUG AuthRepository - Usuario creado en Firebase Auth: ${firebaseUser.uid}")

                // Crear usuario en Firestore
                val user = User(
                    id = firebaseUser.uid,
                    name = name,
                    email = email,
                    role = UserRole.USER,
                    address = address,
                    latitude = latitude,
                    longitude = longitude
                )

                // Guardar en Firestore con manejo de errores
                try {
                    firestore.collection("users").document(firebaseUser.uid)
                        .set(user)
                        .await()

                    // Guardar localmente
                    database.userDao().insertUser(user.toEntity())

                    println("🟢 DEBUG AuthRepository - Registro exitoso: ${user.name} (${user.email})")
                    Result.success(user)
                } catch (firestoreError: Exception) {
                    println("🔴 DEBUG AuthRepository - Error al guardar en Firestore: ${firestoreError.message}")

                    // Si falla Firestore, eliminar el usuario de Auth para consistencia
                    try {
                        auth.currentUser?.delete()?.await()
                        println("🔴 DEBUG AuthRepository - Usuario eliminado de Auth por fallo en Firestore")
                    } catch (deleteError: Exception) {
                        println("🔴 DEBUG AuthRepository - Error al eliminar usuario de Auth: ${deleteError.message}")
                    }

                    Result.failure(Exception("Error al guardar datos del usuario: ${firestoreError.message}"))
                }
            } else {
                println("🔴 DEBUG AuthRepository - Error: firebaseUser es null en registro")
                Result.failure(Exception("Error al crear usuario en authentication"))
            }
        } catch (e: Exception) {
            println("🔴 DEBUG AuthRepository - Error en registro: ${e.message}")
            Result.failure(Exception("Error al registrar usuario: ${e.message}"))
        }
    }

    suspend fun logout() {
        println("🔍 DEBUG AuthRepository - Cerrando sesión")
        auth.signOut()
        // Limpiar datos locales si es necesario
        database.reportDao().deleteAllReports()
        println("🟢 DEBUG AuthRepository - Sesión cerrada")
    }

    fun getCurrentUser(): FirebaseUser? {
        val user = auth.currentUser
        println("🔍 DEBUG AuthRepository - getCurrentUser: ${user?.uid ?: "null"}")
        return user
    }

    suspend fun getUserFromFirestore(userId: String): User? {
        return try {
            println("🔍 AuthRepository - Buscando usuario en Firestore: $userId")
            val document = firestore.collection("users").document(userId).get().await()

            if (document.exists()) {
                val data = document.data
                println("📄 Datos Firestore: $data")

                // Construir usuario manualmente para manejar el rol
                val user = User(
                    id = document.getString("id") ?: userId,
                    name = document.getString("name") ?: "Usuario",
                    email = document.getString("email") ?: "",
                    role = document.getString("role")?.let {
                        UserRole.fromString(it)
                    } ?: UserRole.USER,
                    address = document.getString("address"),
                    latitude = document.getDouble("latitude"),
                    longitude = document.getDouble("longitude"),
                    createdAt = document.getLong("createdAt") ?: System.currentTimeMillis()
                )

                println("🟢 Usuario encontrado: ${user.name}, Rol: ${user.role.getDisplayName()}")
                user
            } else {
                println("🔴 Usuario NO encontrado en Firestore: $userId")
                null
            }
        } catch (e: Exception) {
            println("🔴 Error al buscar usuario: ${e.message}")
            null
        }
    }

    suspend fun updateUserProfile(user: User): Result<Boolean> {
        return try {
            println("🔍 DEBUG AuthRepository - Actualizando perfil: ${user.name}")
            firestore.collection("users").document(user.id)
                .set(user)
                .await()

            // Actualizar localmente
            database.userDao().updateUser(user.toEntity())

            println("🟢 DEBUG AuthRepository - Perfil actualizado: ${user.name}")
            Result.success(true)
        } catch (e: Exception) {
            println("🔴 DEBUG AuthRepository - Error al actualizar perfil: ${e.message}")
            Result.failure(e)
        }
    }

    fun getCurrentUserFlow(userId: String): Flow<User?> {
        println("🔍 DEBUG AuthRepository - getCurrentUserFlow: $userId")
        return database.userDao().getUser(userId).map { entity ->
            entity?.toDomain()
        }
    }

    suspend fun resetPassword(email: String): Result<Boolean> {
        return try {
            println("🔍 DEBUG AuthRepository - Solicitando reset de password: $email")
            auth.sendPasswordResetEmail(email).await()
            println("🟢 DEBUG AuthRepository - Email de reset enviado: $email")
            Result.success(true)
        } catch (e: Exception) {
            println("🔴 DEBUG AuthRepository - Error al resetear password: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun createUserIfNotExists(userId: String, email: String, name: String? = null): Result<User> {
        return try {
            println("🔍 DEBUG AuthRepository - createUserIfNotExists: $userId")
            var user = getUserFromFirestore(userId)

            if (user == null) {
                user = User(
                    id = userId,
                    name = name ?: "Usuario",
                    email = email,
                    role = UserRole.USER
                )

                firestore.collection("users").document(userId)
                    .set(user)
                    .await()

                // Guardar localmente
                database.userDao().insertUser(user.toEntity())

                println("🟢 DEBUG AuthRepository - Usuario creado: ${user.name}")
            } else {
                println("🔍 DEBUG AuthRepository - Usuario ya existe: ${user.name}")
            }

            Result.success(user)
        } catch (e: Exception) {
            println("🔴 DEBUG AuthRepository - Error en createUserIfNotExists: ${e.message}")
            Result.failure(e)
        }
    }
}