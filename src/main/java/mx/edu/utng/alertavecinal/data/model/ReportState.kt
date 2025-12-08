package mx.edu.utng.alertavecinal.data.model

data class ReportState(
    val reports: List<Report> = emptyList(),
    val filteredReports: List<Report> = emptyList(),
    val selectedReport: Report? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val filterType: ReportType? = null,
    val filterStatus: ReportStatus? = null
)