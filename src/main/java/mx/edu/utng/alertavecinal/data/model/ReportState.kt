package mx.edu.utng.alertavecinal.data.model

/*
Clase ReportState: Esta clase representa el estado de los reportes en
la aplicación, almacenando la lista completa de reportes,
los reportes filtrados según criterios específicos, el reporte seleccionado,
y el estado de carga o error. Sirve como contenedor de datos para
gestionar y actualizar la interfaz de reportes de manera reactiva.
*/

data class ReportState(
    val reports: List<Report> = emptyList(),
    val filteredReports: List<Report> = emptyList(),
    val selectedReport: Report? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val filterType: ReportType? = null,
    val filterStatus: ReportStatus? = null
)