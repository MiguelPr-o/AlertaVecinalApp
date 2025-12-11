package mx.edu.utng.alertavecinal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import mx.edu.utng.alertavecinal.data.model.ReportStatus
import mx.edu.utng.alertavecinal.utils.FormatUtils
import mx.edu.utng.alertavecinal.viewmodel.ReportViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportDetailScreen(
    navController: NavController,
    reportId: String,
    viewModel: ReportViewModel = hiltViewModel()
) {
    // Obtener el estado del ViewModel
    val reportState by viewModel.reportState.collectAsState()

    // Variables locales para el reporte
    val selectedReport = reportState.selectedReport
    val isLoading = reportState.isLoading
    val error = reportState.error

    // Estado para controlar si ya intentamos cargar
    var hasAttemptedLoad by remember { mutableStateOf(false) }

    // Efecto para cargar el reporte cuando se inicia la pantalla
    LaunchedEffect(reportId) {
        if (reportId.isNotEmpty() && !hasAttemptedLoad) {
            println("🔍 ReportDetailScreen - Iniciando carga para reportId: $reportId")

            // 1. Primero buscar en las listas ya cargadas
            val currentReport = selectedReport
            if (currentReport?.id == reportId) {
                println("✅ ReportDetailScreen - Ya tiene el reporte seleccionado")
                return@LaunchedEffect
            }

            // 2. Buscar en todas las listas disponibles
            val allReports = reportState.reports
            println("📊 ReportDetailScreen - Total reportes en lista: ${allReports.size}")

            val foundInList = allReports.find { it.id == reportId }
            if (foundInList != null) {
                println("✅ ReportDetailScreen - Encontrado en lista local: ${foundInList.title}")
                viewModel.selectReport(foundInList)
            } else {
                println("🔍 ReportDetailScreen - No en lista, llamando a loadReportById")
                viewModel.loadReportById(reportId)
            }

            hasAttemptedLoad = true
        }
    }

    // También observar cambios en las listas
    LaunchedEffect(reportState.reports) {
        if (reportId.isNotEmpty() && selectedReport?.id != reportId) {
            val foundInUpdatedList = reportState.reports.find { it.id == reportId }
            foundInUpdatedList?.let {
                println("📈 ReportDetailScreen - Nueva lista contiene el reporte")
                viewModel.selectReport(it)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when {
                            isLoading -> "Cargando..."
                            selectedReport != null -> "Detalles del Reporte"
                            else -> "Reporte no encontrado"
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Estado de carga
            if (isLoading) {
                LoadingState(reportId = reportId)
            }
            // Estado de error
            else if (error != null) {
                ErrorState(
                    error = error,
                    reportId = reportId,
                    onRetry = { viewModel.loadReportById(reportId) },
                    onBack = { navController.popBackStack() }
                )
            }
            // Reporte no encontrado
            else if (selectedReport == null) {
                NotFoundState(
                    reportId = reportId,
                    onRetry = { viewModel.loadReportById(reportId) },
                    onBack = { navController.popBackStack() }
                )
            }
            // Reporte rechazado (mostrar mensaje especial)
            else if (selectedReport.status == ReportStatus.REJECTED) {
                RejectedState(
                    report = selectedReport,
                    onBack = { navController.popBackStack() }
                )
            }
            // Mostrar detalles del reporte (aprobado o pendiente)
            else {
                ReportContent(
                    report = selectedReport,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun LoadingState(reportId: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                strokeWidth = 4.dp
            )
            Text(
                text = "Cargando reporte...",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "ID: $reportId",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun ErrorState(
    error: String,
    reportId: String,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = "Error",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Ocurrió un error",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.error,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = error,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        Text(
            text = "ID: $reportId",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onRetry,
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                Text("Reintentar")
            }

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                Text("Volver al mapa")
            }
        }
    }
}

@Composable
private fun NotFoundState(
    reportId: String,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.QuestionMark,
            contentDescription = "No encontrado",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Reporte no encontrado",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "El reporte que buscas no está disponible o fue eliminado",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        Text(
            text = "ID: $reportId",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(top = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onRetry,
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                Text("Intentar nuevamente")
            }

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                Text("Volver al mapa")
            }
        }
    }
}

@Composable
private fun RejectedState(
    report: mx.edu.utng.alertavecinal.data.model.Report,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Rechazado",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Reporte Rechazado",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.error,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Este reporte fue revisado por un moderador y no cumple con las políticas de la comunidad",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        // Mostrar motivo de rechazo si existe
        report.rejectionReason?.let { reason ->
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Motivo del rechazo:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = reason,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            Text("Volver al mapa")
        }
    }
}

@Composable
private fun ReportContent(
    report: mx.edu.utng.alertavecinal.data.model.Report,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        // Header con título y estado
        ReportHeaderSection(report = report)

        Spacer(modifier = Modifier.height(16.dp))

        // Información básica
        ReportInfoSection(report = report)

        Spacer(modifier = Modifier.height(16.dp))

        // Imagen (si existe)
        report.imageUrl?.let { imageUrl ->
            ReportImageSection(imageUrl = imageUrl)
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Información de moderación (si fue moderado)
        if (report.approvedBy != null || report.rejectionReason != null) {
            ModerationHistorySection(report = report)
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Espacio al final
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun ReportHeaderSection(report: mx.edu.utng.alertavecinal.data.model.Report) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = report.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    modifier = Modifier.weight(1f)
                )

                // Badge de estado
                val (backgroundColor, textColor) = when (report.status) {
                    ReportStatus.APPROVED -> Pair(Color(0xFF4CAF50), Color.White)
                    ReportStatus.PENDING -> Pair(Color(0xFFFFA000), Color.Black)
                    ReportStatus.REJECTED -> Pair(Color(0xFFF44336), Color.White)
                }

                Text(
                    text = when (report.status) {
                        ReportStatus.APPROVED -> "APROBADO"
                        ReportStatus.PENDING -> "PENDIENTE"
                        ReportStatus.REJECTED -> "RECHAZADO"
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(backgroundColor)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = report.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Tipo de incidente
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Tipo",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Tipo: ${report.reportType.name.replace("_", " ")}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun ReportInfoSection(report: mx.edu.utng.alertavecinal.data.model.Report) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Usuario que reportó
            InfoRow(
                icon = Icons.Default.Person,
                title = "Reportado por",
                value = report.userName
            )

            Divider()

            // Ubicación
            InfoRow(
                icon = Icons.Default.LocationOn,
                title = "Ubicación",
                value = report.address ?: "No especificada"
            )

            Divider()

            // Fecha y hora
            InfoRow(
                icon = Icons.Default.CalendarToday,
                title = "Fecha y Hora",
                value = FormatUtils.formatDate(report.createdAt)
            )

            Divider()

            // Coordenadas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Latitud",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = String.format("%.6f", report.latitude),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Longitud",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = String.format("%.6f", report.longitude),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
private fun ReportImageSection(imageUrl: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Photo,
                    contentDescription = "Imagen",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Imagen Adjunta",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }

            AsyncImage(
                model = imageUrl,
                contentDescription = "Imagen del reporte",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
private fun ModerationHistorySection(report: mx.edu.utng.alertavecinal.data.model.Report) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Historial de Moderación",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            report.approvedBy?.let { approver ->
                InfoRow(
                    icon = Icons.Default.Person,
                    title = "Moderado por",
                    value = approver
                )
            }

            report.rejectionReason?.let { reason ->
                Spacer(modifier = Modifier.height(8.dp))
                InfoRow(
                    icon = Icons.Default.Warning,
                    title = "Motivo de rechazo",
                    value = reason
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Última actualización: ${FormatUtils.formatRelativeTime(report.updatedAt)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}