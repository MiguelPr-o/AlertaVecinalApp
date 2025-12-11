package mx.edu.utng.alertavecinal.viewmodel

/*
Clase ReportViewModel: ViewModel central para la gestión completa de reportes
de incidentes en la aplicación. Maneja la creación, consulta, filtrado y
modificación de reportes, exponiendo múltiples StateFlows separados para
todos los reportes, reportes pendientes y reportes del usuario actual.
Implementa un sistema de formulario para creación de reportes con estado
persistente, integra sincronización automática con Firebase, y proporciona
funcionalidades de filtrado por tipo y estado. Se utiliza tanto para usuarios
normales como para moderadores, manteniendo una arquitectura flexible.
*/

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mx.edu.utng.alertavecinal.data.model.Report
import mx.edu.utng.alertavecinal.data.model.ReportState
import mx.edu.utng.alertavecinal.data.model.ReportStatus
import mx.edu.utng.alertavecinal.data.model.ReportType
import mx.edu.utng.alertavecinal.data.repository.ReportRepository
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val reportRepository: ReportRepository
) : ViewModel() {

    private val _allReports = MutableStateFlow<List<Report>>(emptyList())
    private val _pendingReports = MutableStateFlow<List<Report>>(emptyList())
    private val _userReports = MutableStateFlow<List<Report>>(emptyList())

    val allReportsState: StateFlow<List<Report>> = _allReports.asStateFlow()
    val pendingReportsState: StateFlow<List<Report>> = _pendingReports.asStateFlow()
    val userReportsState: StateFlow<List<Report>> = _userReports.asStateFlow()

    private val _reportState = MutableStateFlow(ReportState())
    val reportState: StateFlow<ReportState> = _reportState.asStateFlow()

    // Estado para creación de reportes
    data class CreateReportState(
        val title: String = "",
        val description: String = "",
        val selectedType: ReportType? = null,
        val currentLocation: String? = null,
        val latitude: Double = 0.0,
        val longitude: Double = 0.0
    )

    private val _createReportState = MutableStateFlow(CreateReportState())
    val createReportState: CreateReportState get() = _createReportState.value

    init {
        loadAllReports()
        loadPendingReports()
        syncWithFirebase()
    }

    private fun syncWithFirebase() {
        viewModelScope.launch {
            reportRepository.syncReports()
            // Recargar después de sincronizar
            loadAllReports()
            loadPendingReports()
        }
    }

    fun updateCreateReportState(
        title: String = _createReportState.value.title,
        description: String = _createReportState.value.description,
        selectedType: ReportType? = _createReportState.value.selectedType,
        currentLocation: String? = _createReportState.value.currentLocation,
        latitude: Double = _createReportState.value.latitude,
        longitude: Double = _createReportState.value.longitude
    ) {
        _createReportState.value = CreateReportState(
            title = title,
            description = description,
            selectedType = selectedType,
            currentLocation = currentLocation,
            latitude = latitude,
            longitude = longitude
        )
    }

    fun clearCreateReportState() {
        _createReportState.value = CreateReportState()
    }

    fun loadAllReports() {
        viewModelScope.launch {
            reportRepository.getAllReports().collect { reports ->
                _allReports.value = reports
                _reportState.value = _reportState.value.copy(
                    reports = reports,
                    filteredReports = applyFilters(reports)
                )
            }
        }
    }

    fun loadPendingReports() {
        viewModelScope.launch {
            reportRepository.getPendingReports().collect { reports ->
                _pendingReports.value = reports
                _reportState.value = _reportState.value.copy(
                    reports = reports,
                    filteredReports = applyFilters(reports)
                )
            }
        }
    }

    fun loadUserReports(userId: String) {
        viewModelScope.launch {
            reportRepository.getUserReports(userId).collect { reports ->
                _userReports.value = reports
                _reportState.value = _reportState.value.copy(
                    reports = reports,
                    filteredReports = applyFilters(reports)
                )
            }
        }
    }

    fun deleteReport(reportId: String, userId: String) {
        viewModelScope.launch {
            _reportState.value = _reportState.value.copy(isLoading = true)

            val result = reportRepository.deleteReport(reportId)
            result.fold(
                onSuccess = {
                    // Recargar todos los listados
                    loadAllReports()
                    loadPendingReports()
                    loadUserReports(userId)

                    _reportState.value = _reportState.value.copy(
                        isLoading = false,
                        error = null
                    )
                },
                onFailure = { exception ->
                    _reportState.value = _reportState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Error al eliminar reporte"
                    )
                }
            )
        }
    }

    fun createReport(
        title: String,
        description: String,
        reportType: ReportType,
        latitude: Double,
        longitude: Double,
        address: String? = null,
        imageUrl: String? = null,
        userId: String,
        userName: String
    ) {
        _reportState.value = _reportState.value.copy(isLoading = true)

        viewModelScope.launch {
            val report = Report(
                title = title,
                description = description,
                reportType = reportType,
                latitude = latitude,
                longitude = longitude,
                address = address,
                imageUrl = imageUrl,
                userId = userId,
                userName = userName,
                status = ReportStatus.PENDING
            )

            val result = reportRepository.createReport(report)
            result.fold(
                onSuccess = { reportId ->
                    // Sincronizar y recargar
                    reportRepository.syncReports()
                    loadUserReports(userId)
                    loadAllReports()

                    // Limpiar estado
                    clearCreateReportState()

                    _reportState.value = _reportState.value.copy(
                        isLoading = false,
                        error = null
                    )
                },
                onFailure = { exception ->
                    _reportState.value = _reportState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Error al crear reporte"
                    )
                }
            )
        }
    }

    fun approveReport(reportId: String, approvedBy: String) {
        viewModelScope.launch {
            val result = reportRepository.updateReportStatus(
                reportId = reportId,
                status = ReportStatus.APPROVED,
                approvedBy = approvedBy
            )

            result.fold(
                onSuccess = {
                    loadPendingReports()
                    loadAllReports()
                },
                onFailure = { exception ->
                    _reportState.value = _reportState.value.copy(
                        error = exception.message ?: "Error al aprobar reporte"
                    )
                }
            )
        }
    }

    fun rejectReport(reportId: String, approvedBy: String, reason: String) {
        viewModelScope.launch {
            val result = reportRepository.updateReportStatus(
                reportId = reportId,
                status = ReportStatus.REJECTED,
                approvedBy = approvedBy,
                rejectionReason = reason
            )

            result.fold(
                onSuccess = {
                    loadPendingReports()
                    loadAllReports()
                },
                onFailure = { exception ->
                    _reportState.value = _reportState.value.copy(
                        error = exception.message ?: "Error al rechazar reporte"
                    )
                }
            )
        }
    }

    fun selectReport(report: Report?) {
        _reportState.value = _reportState.value.copy(selectedReport = report)
    }

    fun filterByType(type: ReportType?) {
        _reportState.value = _reportState.value.copy(filterType = type)
        applyFiltersToCurrentReports()
    }

    fun filterByStatus(status: ReportStatus?) {
        _reportState.value = _reportState.value.copy(filterStatus = status)
        applyFiltersToCurrentReports()
    }

    private fun applyFiltersToCurrentReports() {
        val filtered = applyFilters(_reportState.value.reports)
        _reportState.value = _reportState.value.copy(filteredReports = filtered)
    }

    private fun applyFilters(reports: List<Report>): List<Report> {
        var filtered = reports
        _reportState.value.filterType?.let { type ->
            filtered = filtered.filter { it.reportType == type }
        }
        _reportState.value.filterStatus?.let { status ->
            filtered = filtered.filter { it.status == status }
        }
        return filtered
    }

    fun clearError() {
        _reportState.value = _reportState.value.copy(error = null)
    }

    fun uploadReportImage(imageBytes: ByteArray, onResult: (Result<String>) -> Unit) {
        viewModelScope.launch {
            val result = reportRepository.uploadReportImage(imageBytes)
            onResult(result)
        }
    }

    fun forceSync() {
        viewModelScope.launch {
            reportRepository.syncReports()
            loadAllReports()
            loadPendingReports()
        }
    }

    // En ReportViewModel.kt - Agrega esta función
    fun getReportById(reportId: String) {
        viewModelScope.launch {
            Log.d("ReportViewModel", "🔄 INICIANDO getReportById: $reportId")
            _reportState.value = _reportState.value.copy(isLoading = true)

            try {
                // Usar el repositorio
                val report = reportRepository.getReportById(reportId)

                if (report != null) {
                    Log.d("ReportViewModel", "✅ Reporte obtenido: ${report.title}")
                    _reportState.value = _reportState.value.copy(
                        selectedReport = report,
                        isLoading = false,
                        error = null
                    )
                } else {
                    Log.d("ReportViewModel", "❌ Reporte NO encontrado")
                    _reportState.value = _reportState.value.copy(
                        selectedReport = null,
                        isLoading = false,
                        error = "Reporte no encontrado"
                    )
                }
            } catch (e: Exception) {
                Log.e("ReportViewModel", "💥 Error: ${e.message}", e)
                _reportState.value = _reportState.value.copy(
                    isLoading = false,
                    error = "Error: ${e.message ?: "Error desconocido"}"
                )
            }
        }
    }

    fun loadReportById(reportId: String) {
        viewModelScope.launch {
            Log.d("ReportViewModel", "🔄 INICIANDO loadReportById: $reportId")
            _reportState.value = _reportState.value.copy(isLoading = true)

            try {
                Log.d("ReportViewModel", "📞 Llamando a repository.getReportById")
                val report = reportRepository.getReportById(reportId)
                Log.d("ReportViewModel", "📦 Reporte obtenido: ${report?.id ?: "NULL"}")

                if (report != null) {
                    Log.d("ReportViewModel", "✅ Reporte cargado: ${report.title}")
                    _reportState.value = _reportState.value.copy(
                        selectedReport = report,
                        isLoading = false,
                        error = null
                    )
                } else {
                    Log.d("ReportViewModel", "❌ Reporte NO encontrado en repository")
                    _reportState.value = _reportState.value.copy(
                        selectedReport = null,
                        isLoading = false,
                        error = "Reporte no encontrado"
                    )
                }
            } catch (e: Exception) {
                Log.e("ReportViewModel", "💥 ERROR en loadReportById", e)
                _reportState.value = _reportState.value.copy(
                    isLoading = false,
                    error = "Error: ${e.message}"
                )
            }
        }
    }


    // Versión alternativa en ReportViewModel
    fun loadReportDirectly(reportId: String) {
        viewModelScope.launch {
            _reportState.value = _reportState.value.copy(isLoading = true)

            try {
                // 1. Intentar cargar usando el repositorio
                val report = withContext(Dispatchers.IO) {
                    // Forzar una carga sincrónica
                    runCatching {
                        // Aquí necesitarías una función sincrónica en el repositorio
                        // Por ahora, usemos un enfoque diferente
                        null
                    }.getOrNull()
                }

                // 2. Si falla, intentar de las listas existentes
                if (report == null) {
                    val allReports = _allReports.value
                    val foundReport = allReports.find { it.id == reportId }

                    if (foundReport != null) {
                        _reportState.value = _reportState.value.copy(
                            selectedReport = foundReport,
                            isLoading = false
                        )
                    } else {
                        // 3. Intentar recargar todas las listas
                        loadAllReports()
                        loadPendingReports()

                        // Esperar un momento y verificar de nuevo
                        delay(500)

                        val updatedReports = _allReports.value
                        val finalReport = updatedReports.find { it.id == reportId }

                        _reportState.value = _reportState.value.copy(
                            selectedReport = finalReport,
                            isLoading = false,
                            error = if (finalReport == null) "Reporte no encontrado" else null
                        )
                    }
                }
            } catch (e: Exception) {
                _reportState.value = _reportState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error desconocido"
                )
            }
        }
    }
}