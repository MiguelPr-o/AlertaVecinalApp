package mx.edu.utng.alertavecinal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mx.edu.utng.alertavecinal.data.model.MapState
import mx.edu.utng.alertavecinal.data.model.Report
import mx.edu.utng.alertavecinal.data.model.ReportType
import mx.edu.utng.alertavecinal.data.repository.MapRepository
import mx.edu.utng.alertavecinal.data.repository.ReportRepository
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val reportRepository: ReportRepository,
    private val mapRepository: MapRepository
) : ViewModel() {

    private val _mapState = MutableStateFlow(MapState())
    val mapState: StateFlow<MapState> = _mapState.asStateFlow()

    init {
        loadAllReports()
        getCurrentLocation()
        syncWithFirebase()
    }

    private fun syncWithFirebase() {
        viewModelScope.launch {
            reportRepository.syncReports()
        }
    }

    fun loadAllReports() {
        _mapState.value = _mapState.value.copy(isLoading = true)

        viewModelScope.launch {
            try {
                reportRepository.getAllReports().collect { reports ->
                    _mapState.value = _mapState.value.copy(
                        reports = reports,
                        isLoading = false,
                        error = null,
                        filterType = null
                    )
                }
            } catch (e: Exception) {
                _mapState.value = _mapState.value.copy(
                    isLoading = false,
                    error = "Error al cargar reportes: ${e.message}"
                )
            }
        }
    }

    fun loadApprovedReports() {
        _mapState.value = _mapState.value.copy(isLoading = true)

        viewModelScope.launch {
            try {
                reportRepository.getApprovedReports().collect { reports ->
                    _mapState.value = _mapState.value.copy(
                        reports = reports,
                        isLoading = false,
                        error = null,
                        filterType = null
                    )
                }
            } catch (e: Exception) {
                _mapState.value = _mapState.value.copy(
                    isLoading = false,
                    error = "Error al cargar reportes: ${e.message}"
                )
            }
        }
    }

    fun getCurrentLocation() {
        viewModelScope.launch {
            _mapState.value = _mapState.value.copy(isLoading = true)

            try {
                val locationData = mapRepository.getCurrentLocation()
                val latLng = locationData?.let {
                    LatLng(it.latitude, it.longitude)
                }

                _mapState.value = _mapState.value.copy(
                    currentLocation = latLng,
                    isLocationEnabled = locationData != null,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _mapState.value = _mapState.value.copy(
                    isLoading = false,
                    error = "Error al obtener ubicación: ${e.message}"
                )
            }
        }
    }

    fun selectReport(report: Report?) {
        _mapState.value = _mapState.value.copy(selectedReport = report)
    }

    fun filterReportsByType(type: String?) {
        viewModelScope.launch {
            _mapState.value = _mapState.value.copy(isLoading = true)
            try {
                val reports = if (type.isNullOrEmpty()) {
                    reportRepository.getAllReports()
                } else {
                    // ✅ CORREGIDO: Convertir String a ReportType
                    val reportType = ReportType.valueOf(type.uppercase())
                    reportRepository.getReportsByType(reportType)
                }

                reports.collect { filteredReports ->
                    _mapState.value = _mapState.value.copy(
                        reports = filteredReports,
                        filterType = type,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _mapState.value = _mapState.value.copy(
                    isLoading = false,
                    error = "Error al filtrar reportes: ${e.message}"
                )
            }
        }
    }

    // Método mejorado para filtrar por ReportType enum
    fun filterReportsByReportType(reportType: ReportType?) {
        viewModelScope.launch {
            _mapState.value = _mapState.value.copy(isLoading = true)
            try {
                val reports = if (reportType == null) {
                    reportRepository.getAllReports()
                } else {
                    // ✅ CORREGIDO: Pasar directamente el ReportType
                    reportRepository.getReportsByType(reportType)
                }

                reports.collect { filteredReports ->
                    _mapState.value = _mapState.value.copy(
                        reports = filteredReports,
                        filterType = reportType?.let { getReportTypeDisplayName(it) },
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _mapState.value = _mapState.value.copy(
                    isLoading = false,
                    error = "Error al filtrar reportes: ${e.message}"
                )
            }
        }
    }

    fun refreshData() {
        loadAllReports()
        getCurrentLocation()

        viewModelScope.launch {
            reportRepository.syncReports()
        }
    }

    fun clearError() {
        _mapState.value = _mapState.value.copy(error = null)
    }

    fun centerOnLocation(latLng: LatLng) {
        _mapState.value = _mapState.value.copy(currentLocation = latLng)
    }

    private fun getReportTypeDisplayName(reportType: ReportType): String {
        return when (reportType) {
            ReportType.ROBBERY -> "Robo"
            ReportType.FIRE -> "Incendio"
            ReportType.ACCIDENT -> "Accidente"
            ReportType.SUSPICIOUS_PERSON -> "Persona Sospechosa"
            ReportType.FIGHT -> "Pelea"
            ReportType.VANDALISM -> "Vandalismo"
            ReportType.NOISE -> "Ruido"
            ReportType.LOST_PET -> "Mascota Perdida"
            ReportType.OTHER -> "Otro"
        }
    }


    fun getApprovedReports(): List<Report> {
        return _mapState.value.reports.filter { it.status.name == "APPROVED" }
    }

    fun getPendingReports(): List<Report> {
        return _mapState.value.reports.filter { it.status.name == "PENDING" }
    }
}