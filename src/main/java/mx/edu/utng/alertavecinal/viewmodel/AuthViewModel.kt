package mx.edu.utng.alertavecinal.viewmodel

/*
Clase AuthViewModel: ViewModel responsable de toda la lógica de autenticación
y gestión del estado del usuario en la aplicación. Maneja el inicio de sesión,
registro, cierre de sesión y verificación del usuario actual. Utiliza Hilt
para la inyección de dependencias y expone estados observables a través de
StateFlow. También implementa la lógica de redirección automática según el
rol del usuario (normal → mapa, moderador/admin → dashboard) después de la
autenticación exitosa.
*/

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mx.edu.utng.alertavecinal.data.model.AuthState
import mx.edu.utng.alertavecinal.data.model.User
import mx.edu.utng.alertavecinal.data.model.UserRole
import mx.edu.utng.alertavecinal.data.repository.AuthRepository
import mx.edu.utng.alertavecinal.utils.Constants
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _shouldNavigate = MutableStateFlow<String?>(null)
    val shouldNavigate: StateFlow<String?> = _shouldNavigate.asStateFlow()

    init {
        println("🟢 DEBUG AuthViewModel - Inicializando ViewModel")
        checkCurrentUser()
    }

    fun login(email: String, password: String) {
        _authState.value = _authState.value.copy(
            isLoading = true,
            error = null
        )

        viewModelScope.launch {
            println("🔍 DEBUG AuthViewModel - Iniciando login para: $email")
            val result = authRepository.login(email, password)
            result.fold(
                onSuccess = { user ->
                    _authState.value = AuthState(
                        isAuthenticated = true,
                        currentUser = user,
                        isLoading = false,
                        error = null
                    )
                    println("🟢 DEBUG AuthViewModel - Login exitoso: ${user.name}")

                    determineRedirectDestination(user)
                },
                onFailure = { exception ->
                    _authState.value = AuthState(
                        isAuthenticated = false,
                        currentUser = null,
                        isLoading = false,
                        error = exception.message ?: "Error al iniciar sesión"
                    )
                    println("🔴 DEBUG AuthViewModel - Error en login: ${exception.message}")
                    _shouldNavigate.value = null
                }
            )
        }
    }

    fun register(
        name: String,
        email: String,
        password: String,
        address: String? = null,
        latitude: Double? = null,
        longitude: Double? = null
    ) {
        _authState.value = _authState.value.copy(
            isLoading = true,
            error = null
        )

        viewModelScope.launch {
            println("🔍 DEBUG AuthViewModel - Iniciando registro para: $email")
            val result = authRepository.register(name, email, password, address, latitude, longitude)
            result.fold(
                onSuccess = { user ->
                    _authState.value = AuthState(
                        isAuthenticated = true,
                        currentUser = user,
                        isLoading = false,
                        error = null
                    )
                    println("🟢 DEBUG AuthViewModel - Registro exitoso: ${user.name}")

                    determineRedirectDestination(user)
                },
                onFailure = { exception ->
                    _authState.value = AuthState(
                        isAuthenticated = false,
                        currentUser = null,
                        isLoading = false,
                        error = exception.message ?: "Error al registrar usuario"
                    )
                    println("🔴 DEBUG AuthViewModel - Error en registro: ${exception.message}")
                    _shouldNavigate.value = null
                }
            )
        }
    }

    fun logout() {
        println("🔍 DEBUG AuthViewModel - Cerrando sesión")
        viewModelScope.launch {
            authRepository.logout()
            _authState.value = AuthState(
                isAuthenticated = false,
                currentUser = null,
                isLoading = false,
                error = null
            )
            _shouldNavigate.value = null
            println("🟢 DEBUG AuthViewModel - Sesión cerrada")
        }
    }

    fun clearError() {
        _authState.value = _authState.value.copy(error = null)
        println("🔍 DEBUG AuthViewModel - Error limpiado")
    }

    fun checkCurrentUser() {
        println("🔍 DEBUG AuthViewModel - checkCurrentUser() llamado")

        _authState.value = _authState.value.copy(isLoading = true)

        viewModelScope.launch {
            try {
                val currentUser = authRepository.getCurrentUser()
                println("🔍 DEBUG AuthViewModel - Usuario de Auth: ${currentUser?.uid ?: "null"}")

                if (currentUser != null) {
                    val userResult = authRepository.createUserIfNotExists(
                        currentUser.uid,
                        currentUser.email ?: "",
                        currentUser.displayName ?: "Usuario"
                    )

                    userResult.fold(
                        onSuccess = { user ->
                            _authState.value = AuthState(
                                isAuthenticated = true,
                                currentUser = user,
                                isLoading = false,
                                error = null
                            )
                            println("🟢 DEBUG AuthViewModel - Usuario verificado: ${user.name} (${user.email})")

                            determineRedirectDestination(user)
                        },
                        onFailure = { exception ->
                            _authState.value = AuthState(
                                isAuthenticated = true, // Aún autenticado en Firebase Auth
                                currentUser = null,
                                isLoading = false,
                                error = "Error al cargar perfil: ${exception.message}"
                            )
                            println("🟡 DEBUG AuthViewModel - Usuario autenticado pero error en perfil: ${exception.message}")
                        }
                    )
                } else {
                    _authState.value = AuthState(
                        isAuthenticated = false,
                        currentUser = null,
                        isLoading = false,
                        error = null
                    )
                    println("🔵 DEBUG AuthViewModel - No hay usuario autenticado")
                }
            } catch (e: Exception) {
                _authState.value = AuthState(
                    isAuthenticated = false,
                    currentUser = null,
                    isLoading = false,
                    error = "Error al verificar usuario: ${e.message}"
                )
                println("🔴 DEBUG AuthViewModel - Error en checkCurrentUser: ${e.message}")
            }
        }
    }

    private fun determineRedirectDestination(user: User) {
        viewModelScope.launch {
            val destination = when (user.role) {
                UserRole.MODERATOR, UserRole.ADMIN -> {
                    println("🎯 DEBUG - Usuario es moderador/admin, redirigiendo a dashboard")
                    Constants.ROUTE_MODERATOR_DASHBOARD
                }
                else -> {
                    println("🎯 DEBUG - Usuario es normal, redirigiendo a mapa")
                    Constants.ROUTE_MAP
                }
            }

            _shouldNavigate.value = destination
        }
    }

    fun clearNavigation() {
        _shouldNavigate.value = null
    }

    fun refreshUser() {
        println("🔍 DEBUG AuthViewModel - Refrescando datos del usuario")
        checkCurrentUser()
    }

    fun updateCurrentUser(updatedUser: User) {
        println("🔍 DEBUG AuthViewModel - Actualizando usuario local: ${updatedUser.name}")
        _authState.value = _authState.value.copy(currentUser = updatedUser)
    }

    fun getCurrentFirebaseUser() = authRepository.getCurrentUser()

    fun printCurrentState() {
        println("=== DEBUG AuthViewModel Estado Actual ===")
        println("isAuthenticated: ${_authState.value.isAuthenticated}")
        println("currentUser: ${_authState.value.currentUser?.name ?: "null"}")
        println("isLoading: ${_authState.value.isLoading}")
        println("error: ${_authState.value.error ?: "null"}")
        println("Firebase User: ${authRepository.getCurrentUser()?.uid ?: "null"}")
        println("Should Navigate: ${_shouldNavigate.value ?: "null"}")
        println("=====================================")
    }

    // AuthViewModel.kt - Agregar esta función
    fun createModeratorAccount() {
        viewModelScope.launch {
            val email = "angelgodinez1289@gmail.com"
            val password = "miguel1289"
            val name = "Moderador Principal"

            val result = authRepository.register(
                name = name,
                email = email,
                password = password,
                address = "Oficina Central"
            )

            result.fold(
                onSuccess = { user ->
                    // Actualizar rol a MODERATOR
                    val moderatorUser = user.copy(role = UserRole.MODERATOR)
                    authRepository.updateUserProfile(moderatorUser)
                    println("✅ Moderador creado: ${moderatorUser.email}")
                },
                onFailure = { exception ->
                    println("❌ Error: ${exception.message}")
                }
            )
        }
    }

}