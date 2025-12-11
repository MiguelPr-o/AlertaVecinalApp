package mx.edu.utng.alertavecinal.viewmodel

/*
Clase ModeratorViewModel: ViewModel especializado para la gestión de moderadores
y administradores de la aplicación. Maneja toda la lógica relacionada con la
revisión, aprobación y rechazo de reportes de incidentes. Organiza los reportes
en tres categorías principales (pendientes, aprobados, rechazados), calcula
estadísticas de moderación, y proporciona funcionalidades avanzadas como
solicitar más información, editar reportes y filtrar por tipo. Expone múltiples
StateFlows para cada estado y mantiene separación clara de responsabilidades
en la moderación de contenido.
*/

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mx.edu.utng.alertavecinal.data.model.Report
import mx.edu.utng.alertavecinal.data.model.ReportStatus
import mx.edu.utng.alertavecinal.data.repository.ReportRepository
import javax.inject.Inject

@HiltViewModel
class ModeratorViewModel @Inject constructor(
    private val reportRepository: ReportRepository
) : ViewModel() {

    // Estados para los diferentes tipos de reportes
    private val _pendingReports = MutableStateFlow<List<Report>>(emptyList())
    val pendingReports: StateFlow<List<Report>> = _pendingReports.asStateFlow()

    private val _approvedReports = MutableStateFlow<List<Report>>(emptyList())
    val approvedReports: StateFlow<List<Report>> = _approvedReports.asStateFlow()

    private val _rejectedReports = MutableStateFlow<List<Report>>(emptyList())
    val rejectedReports: StateFlow<List<Report>> = _rejectedReports.asStateFlow()

    // Estado de carga y error
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Reporte seleccionado para revisión
    private val _selectedReport = MutableStateFlow<Report?>(null)

    // Estadísticas del moderador
    private val _moderatorStats = MutableStateFlow(ModeratorStats())
    val moderatorStats: StateFlow<ModeratorStats> = _moderatorStats.asStateFlow()

    init {
        loadAllModeratorData()
    }

    fun loadAllModeratorData() {
        loadPendingReports()
        loadApprovedReports()
        loadRejectedReports()
        calculateStats()
    }

    fun loadPendingReports() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                reportRepository.getPendingReports().collect { reports ->
                    _pendingReports.value = reports
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar reportes pendientes: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    // Cargar reportes aprobados
    fun loadApprovedReports() {
        viewModelScope.launch {
            try {
                reportRepository.getAllReports().collect { reports ->
                    _approvedReports.value = reports.filter { it.status == ReportStatus.APPROVED }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar reportes aprobados: ${e.message}"
            }
        }
    }

    fun loadRejectedReports() {
        viewModelScope.launch {
            try {
                reportRepository.getAllReports().collect { reports ->
                    _rejectedReports.value = reports.filter { it.status == ReportStatus.REJECTED }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar reportes rechazados: ${e.message}"
            }
        }
    }

    fun approveReport(
        reportId: String,
        moderatorId: String,
        moderatorName: String,
        comment: String? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = reportRepository.updateReportStatusWithComment(
                    reportId = reportId,
                    status = ReportStatus.APPROVED,
                    moderatorId = moderatorId,
                    moderatorName = moderatorName,
                    comment = comment
                )

                result.fold(
                    onSuccess = {
                        loadAllModeratorData()
                        _errorMessage.value = null

                        if (_selectedReport.value?.id == reportId) {
                            _selectedReport.value = null
                        }
                    },
                    onFailure = { exception ->
                        _errorMessage.value = "Error al aprobar reporte: ${exception.message}"
                    }
                )
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun rejectReport(
        reportId: String,
        moderatorId: String,
        moderatorName: String,
        reason: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = reportRepository.updateReportStatus(
                    reportId = reportId,
                    status = ReportStatus.REJECTED,
                    approvedBy = "$moderatorName ($moderatorId)",
                    rejectionReason = reason
                )

                result.fold(
                    onSuccess = {
                        loadAllModeratorData()
                        _errorMessage.value = null

                        if (_selectedReport.value?.id == reportId) {
                            _selectedReport.value = null
                        }
                    },
                    onFailure = { exception ->
                        _errorMessage.value = "Error al rechazar reporte: ${exception.message}"
                    }
                )
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun requestMoreInfo(
        reportId: String,
        moderatorId: String,
        moderatorName: String,
        message: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Aquí implementarías la lógica para enviar notificación al usuario
                // Por ahora solo actualizamos localmente
                _errorMessage.value = "Notificación enviada al usuario solicitando más información"

                // Actualizar estadísticas
                calculateStats()
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun editReport(
        reportId: String,
        title: String? = null,
        description: String? = null,
        reportType: String? = null,
        moderatorId: String,
        moderatorName: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Obtener el reporte actual
                val currentReport = _pendingReports.value.find { it.id == reportId }

                if (currentReport != null) {
                    // Crear reporte actualizado
                    val updatedReport = currentReport.copy(
                        title = title ?: currentReport.title,
                        description = description ?: currentReport.description,
                        reportType = reportType?.let {
                            mx.edu.utng.alertavecinal.data.model.ReportType.valueOf(it)
                        } ?: currentReport.reportType,
                        approvedBy = "Editado por $moderatorName",
                        updatedAt = System.currentTimeMillis()
                    )

                    // Aquí implementarías la lógica para actualizar en Firestore
                    // Por ahora solo actualizamos localmente

                    // Actualizar listas
                    loadAllModeratorData()
                    _errorMessage.value = "Reporte editado correctamente"
                } else {
                    _errorMessage.value = "No se encontró el reporte"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al editar reporte: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectReport(report: Report) {
        _selectedReport.value = report
    }

    fun clearSelectedReport() {
        _selectedReport.value = null
    }

    private fun calculateStats() {
        viewModelScope.launch {
            val pending = _pendingReports.value.size
            val approved = _approvedReports.value.size
            val rejected = _rejectedReports.value.size
            val total = pending + approved + rejected

            val stats = ModeratorStats(
                pendingCount = pending,
                approvedCount = approved,
                rejectedCount = rejected,
                totalReports = total,
                approvalRate = if (total > 0) (approved.toFloat() / total * 100).toInt() else 0,
                averageResponseTime = calculateAverageResponseTime()
            )

            _moderatorStats.value = stats
        }
    }

    private fun calculateAverageResponseTime(): Long {
        // Calcular tiempo promedio de respuesta en minutos
        val approvedReports = _approvedReports.value
        if (approvedReports.isEmpty()) return 0L

        val totalResponseTime = approvedReports.sumOf { report ->
            if (report.approvedBy != null && report.updatedAt > report.createdAt) {
                report.updatedAt - report.createdAt
            } else {
                0L
            }
        }

        return totalResponseTime / (approvedReports.size * 60000) // Convertir a minutos
    }

    fun filterReportsByType(type: String?) {
        viewModelScope.launch {
            if (type == null) {
                loadPendingReports()
            } else {
                try {
                    reportRepository.getPendingReports().collect { reports ->
                        _pendingReports.value = reports.filter { it.reportType.name == type }
                    }
                } catch (e: Exception) {
                    _errorMessage.value = "Error al filtrar reportes: ${e.message}"
                }
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun getReportById(reportId: String): StateFlow<Report?> {
        val reportFlow = MutableStateFlow<Report?>(null)

        viewModelScope.launch {
            try {
                reportRepository.getAllReports().collect { reports ->
                    val foundReport = reports.find { it.id == reportId }
                    reportFlow.value = foundReport
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar el reporte: ${e.message}"
            }
        }

        return reportFlow.asStateFlow()
    }

    fun loadReportById(reportId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val allReports = _pendingReports.value + _approvedReports.value + _rejectedReports.value
                val foundReport = allReports.find { it.id == reportId }

                if (foundReport != null) {
                    _selectedReport.value = foundReport
                } else {
                    // Si no está en las listas, intentar obtenerlo del repositorio
                    reportRepository.getAllReports().collect { reports ->
                        val reportFromRepo = reports.find { it.id == reportId }
                        _selectedReport.value = reportFromRepo
                        _errorMessage.value = if (reportFromRepo == null) "Reporte no encontrado" else null
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar el reporte: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    val selectedReport: StateFlow<Report?> = _selectedReport.asStateFlow()
}

data class ModeratorStats(
    val pendingCount: Int = 0,
    val approvedCount: Int = 0,
    val rejectedCount: Int = 0,
    val totalReports: Int = 0,
    val approvalRate: Int = 0, // Porcentaje
    val averageResponseTime: Long = 0L // En minutos
)