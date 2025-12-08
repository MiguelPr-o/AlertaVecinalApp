package mx.edu.utng.alertavecinal.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import mx.edu.utng.alertavecinal.data.model.Report
import mx.edu.utng.alertavecinal.data.model.ReportType

@Composable
fun IncidentMarker(
    report: Report,
    onClick: () -> Unit
) {
    val markerColor = when (report.reportType) {
        ReportType.ROBBERY -> BitmapDescriptorFactory.HUE_RED
        ReportType.FIRE -> BitmapDescriptorFactory.HUE_ORANGE
        ReportType.ACCIDENT -> BitmapDescriptorFactory.HUE_YELLOW
        ReportType.SUSPICIOUS_PERSON -> BitmapDescriptorFactory.HUE_VIOLET
        ReportType.FIGHT -> BitmapDescriptorFactory.HUE_ROSE
        ReportType.VANDALISM -> BitmapDescriptorFactory.HUE_MAGENTA
        ReportType.NOISE -> BitmapDescriptorFactory.HUE_BLUE
        ReportType.LOST_PET -> BitmapDescriptorFactory.HUE_CYAN
        ReportType.OTHER -> BitmapDescriptorFactory.HUE_GREEN
    }

    Marker(
        state = MarkerState(
            position = LatLng(report.latitude, report.longitude)
        ),
        title = report.title,
        snippet = report.description,
        icon = BitmapDescriptorFactory.defaultMarker(markerColor),
        onInfoWindowClick = { onClick() }
    )
}