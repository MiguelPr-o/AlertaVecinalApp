package mx.edu.utng.alertavecinal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mx.edu.utng.alertavecinal.data.model.ReportStatus
import mx.edu.utng.alertavecinal.data.model.User
import mx.edu.utng.alertavecinal.data.repository.AuthRepository
import mx.edu.utng.alertavecinal.data.repository.ReportRepository
import mx.edu.utng.alertavecinal.data.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val reportRepository: ReportRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _userProfile = MutableStateFlow<User?>(null)
    val userProfile: StateFlow<User?> = _userProfile.asStateFlow()

    private val _userReports = MutableStateFlow<List<mx.edu.utng.alertavecinal.data.model.Report>>(emptyList())
    val userReports: StateFlow<List<mx.edu.utng.alertavecinal.data.model.Report>> = _userReports.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _deleteState = MutableStateFlow<DeleteState>(DeleteState.Idle)
    val deleteState: StateFlow<DeleteState> = _deleteState.asStateFlow()

    sealed class DeleteState {
        object Idle : DeleteState()
        object Loading : DeleteState()
        data class Success(val reportId: String) : DeleteState()
        data class Error(val message: String) : DeleteState()
    }

    fun loadUserProfile(userId: String) {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val firestoreUser = userRepository.getUserById(userId)

                if (firestoreUser != null) {
                    _userProfile.value = firestoreUser
                    loadUserReports(userId)
                } else {
                    _error.value = "No se pudo cargar el perfil del usuario"
                }

                _isLoading.value = false

            } catch (e: Exception) {
                _isLoading.value = false
                _error.value = "Error al cargar perfil: ${e.message}"
            }
        }
    }

    private fun loadUserReports(userId: String) {
        viewModelScope.launch {
            try {
                reportRepository.getUserReports(userId).collect { reports ->
                    _userReports.value = reports
                    println("✅ DEBUG ProfileViewModel - Se cargaron ${reports.size} reportes")
                }
            } catch (e: Exception) {
                println("❌ DEBUG ProfileViewModel - Error al cargar reportes: ${e.message}")
                _error.value = "Error al cargar reportes: ${e.message}"
            }
        }
    }

    // ✅✅✅ CORREGIDO: Método deleteReport optimizado
    fun deleteReport(reportId: String, userId: String) {
        println("🔍 DEBUG ProfileViewModel - deleteReport INICIADO")
        println("🔍 DEBUG ProfileViewModel - reportId: $reportId")
        println("🔍 DEBUG ProfileViewModel - userId: $userId")

        _deleteState.value = DeleteState.Loading

        viewModelScope.launch {
            try {
                // Primero verificar si el usuario puede eliminar este reporte
                val userReportsList = _userReports.value
                val reportToDelete = userReportsList.find { it.id == reportId }

                if (reportToDelete == null) {
                    println("❌ DEBUG ProfileViewModel - Reporte no encontrado en la lista local")
                    _deleteState.value = DeleteState.Error("Reporte no encontrado")
                    return@launch
                }

                println("🔍 DEBUG ProfileViewModel - Reporte encontrado:")
                println("  - ID: ${reportToDelete.id}")
                println("  - Título: ${reportToDelete.title}")
                println("  - UserId: ${reportToDelete.userId}")
                println("  - Current UserId: $userId")
                println("  - Status: ${reportToDelete.status}")

                // Validaciones
                if (reportToDelete.userId != userId) {
                    println("❌ DEBUG ProfileViewModel - No pertenece al usuario")
                    _deleteState.value = DeleteState.Error("No puedes eliminar reportes de otros usuarios")
                    return@launch
                }

                if (reportToDelete.status != ReportStatus.PENDING) {
                    println("❌ DEBUG ProfileViewModel - No es PENDING (es ${reportToDelete.status})")
                    _deleteState.value = DeleteState.Error("Solo puedes eliminar reportes pendientes")
                    return@launch
                }

                // Proceder con la eliminación
                println("🔍 DEBUG ProfileViewModel - Procediendo con eliminación...")
                val result = reportRepository.deleteReport(reportId)

                result.fold(
                    onSuccess = {
                        println("✅ DEBUG ProfileViewModel - Reporte eliminado exitosamente")

                        // Actualizar lista local INMEDIATAMENTE (sin esperar recarga)
                        val updatedList = userReportsList.filter { it.id != reportId }
                        _userReports.value = updatedList

                        // Sincronizar con Firebase
                        reportRepository.syncReports()

                        _deleteState.value = DeleteState.Success(reportId)
                    },
                    onFailure = { exception ->
                        println("❌ DEBUG ProfileViewModel - Error en repositorio: ${exception.message}")
                        _deleteState.value = DeleteState.Error(exception.message ?: "Error al eliminar reporte")
                    }
                )
            } catch (e: Exception) {
                println("❌ DEBUG ProfileViewModel - Excepción general: ${e.message}")
                _deleteState.value = DeleteState.Error(e.message ?: "Error al eliminar reporte")
            }
        }
    }

    fun resetDeleteState() {
        _deleteState.value = DeleteState.Idle
    }

    fun updateUserLocation(
        userId: String,
        latitude: Double,
        longitude: Double,
        address: String? = null
    ) {
        viewModelScope.launch {
            val result = userRepository.updateUserLocation(userId, latitude, longitude, address)
            result.fold(
                onSuccess = {
                    _userProfile.value = _userProfile.value?.copy(
                        latitude = latitude,
                        longitude = longitude,
                        address = address
                    )
                },
                onFailure = { exception ->
                    _error.value = exception.message ?: "Error al actualizar ubicación"
                }
            )
        }
    }

    fun updateNotificationSettings(
        userId: String,
        radius: Int,
        enabled: Boolean
    ) {
        viewModelScope.launch {
            val result = userRepository.updateNotificationSettings(userId, radius, enabled)
            result.fold(
                onSuccess = {
                    _userProfile.value = _userProfile.value?.copy(
                        notificationRadius = radius,
                        notificationsEnabled = enabled
                    )
                },
                onFailure = { exception ->
                    _error.value = exception.message ?: "Error al actualizar notificaciones"
                }
            )
        }
    }

    fun updateUserProfile(
        userId: String,
        name: String,
        phone: String?
    ) {
        _isLoading.value = true

        viewModelScope.launch {
            val result = userRepository.updateUserProfile(userId, name, phone)
            result.fold(
                onSuccess = {
                    _isLoading.value = false
                    loadUserProfile(userId)
                },
                onFailure = { exception ->
                    _isLoading.value = false
                    _error.value = exception.message ?: "Error al actualizar perfil"
                }
            )
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    fun refreshUserData(userId: String) {
        loadUserProfile(userId)
    }
}