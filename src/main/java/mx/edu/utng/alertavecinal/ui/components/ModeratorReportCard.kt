// ModeratorReportCard.kt
package mx.edu.utng.alertavecinal.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import mx.edu.utng.alertavecinal.data.model.Report
import mx.edu.utng.alertavecinal.data.model.ReportStatus
import mx.edu.utng.alertavecinal.data.model.ReportType
import mx.edu.utng.alertavecinal.utils.FormatUtils

@Composable
fun ModeratorReportCard(
    report: Report,
    onClick: () -> Unit,
    showStatus: Boolean = true,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header con tipo y estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tipo de incidente con ícono
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = getReportTypeIcon(report.reportType),
                        contentDescription = "Tipo",
                        modifier = Modifier.size(16.dp),
                        tint = getReportTypeColor(report.reportType)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formatReportType(report.reportType),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = getReportTypeColor(report.reportType)
                    )
                }

                // Badge de estado (solo si showStatus es true)
                if (showStatus) {
                    ReportStatusBadge(status = report.status)
                }
            }

            Spacer(modifier = Modifier.size(8.dp))

            // Título
            Text(
                text = report.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.size(4.dp))

            // Descripción
            Text(
                text = report.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.size(12.dp))

            // Información del usuario
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Usuario",
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = report.userName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }

            // Información de ubicación
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Ubicación",
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = report.address ?: "Ubicación no disponible",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }

            // Información de fecha
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = "Fecha",
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = FormatUtils.formatRelativeTime(report.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.weight(1f))

                // Ícono de acción
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Ver detalles",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun ReportStatusBadge(status: ReportStatus) {
    val (backgroundColor, textColor, text) = when (status) {
        ReportStatus.PENDING -> Triple(Color(0xFFFFA000), Color(0xFF000000), "PENDIENTE")
        ReportStatus.APPROVED -> Triple(Color(0xFF4CAF50), Color(0xFFFFFFFF), "APROBADO")
        ReportStatus.REJECTED -> Triple(Color(0xFFF44336), Color(0xFFFFFFFF), "RECHAZADO")
    }

    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = textColor,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

fun getReportTypeIcon(reportType: ReportType): ImageVector {
    return when (reportType) {
        ReportType.ROBBERY -> Icons.Default.Warning
        ReportType.FIRE -> Icons.Default.Warning
        ReportType.ACCIDENT -> Icons.Default.Warning
        ReportType.SUSPICIOUS_PERSON -> Icons.Default.Warning
        ReportType.FIGHT -> Icons.Default.Warning
        ReportType.VANDALISM -> Icons.Default.Warning
        ReportType.NOISE -> Icons.Default.Warning
        ReportType.LOST_PET -> Icons.Default.Warning
        ReportType.OTHER -> Icons.Default.Warning
    }
}

@Composable
fun getReportTypeColor(reportType: ReportType): Color {
    return when (reportType) {
        ReportType.ROBBERY -> MaterialTheme.colorScheme.error
        ReportType.FIRE -> Color(0xFFFF5722)
        ReportType.ACCIDENT -> Color(0xFFFF9800)
        ReportType.SUSPICIOUS_PERSON -> Color(0xFF9C27B0)
        ReportType.FIGHT -> Color(0xFFF44336)
        ReportType.VANDALISM -> Color(0xFF795548)
        ReportType.NOISE -> Color(0xFF607D8B)
        ReportType.LOST_PET -> Color(0xFF2196F3)
        ReportType.OTHER -> MaterialTheme.colorScheme.secondary
    }
}

fun formatReportType(reportType: ReportType): String {
    return reportType.name
        .replace("_", " ")
        .lowercase()
        .replaceFirstChar { it.uppercase() }
}