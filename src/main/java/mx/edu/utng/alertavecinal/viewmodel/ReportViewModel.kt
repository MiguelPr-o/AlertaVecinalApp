package mx.edu.utng.alertavecinal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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

    // Estados separados para cada tipo de reporte
    private val _allReports = MutableStateFlow<List<Report>>(emptyList())
    private val _pendingReports = MutableStateFlow<List<Report>>(emptyList())
    private val _userReports = MutableStateFlow<List<Report>>(emptyList())

    // ✅ NUEVO: Exponer como StateFlow para las Screens
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

    // ✅ CORREGIDO: Sincronizar con Firebase al iniciar
    private fun syncWithFirebase() {
        viewModelScope.launch {
            reportRepository.syncReports()
            // Recargar después de sincronizar
            loadAllReports()
            loadPendingReports()
        }
    }

    // ✅ NUEVO: Método para actualizar el estado del formulario
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

    // ✅ NUEVO: Método para limpiar el estado después de enviar
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
                // ✅ CORREGIDO: Actualizar el reportState también
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

    // ✅ NUEVO: Método para eliminar reporte
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

    // ✅ NUEVO: Forzar sincronización
    fun forceSync() {
        viewModelScope.launch {
            reportRepository.syncReports()
            loadAllReports()
            loadPendingReports()
        }
    }
}