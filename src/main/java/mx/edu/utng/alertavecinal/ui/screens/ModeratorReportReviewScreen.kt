package mx.edu.utng.alertavecinal.ui.screens

/*
Clase ModeratorReportReviewScreen: Esta pantalla especializada permite a
moderadores y administradores revisar reportes de incidentes en detalle y
tomar acciones de moderación. Proporciona una vista completa del reporte
con opciones para aprobar, rechazar, solicitar más información o editar
el contenido, incluyendo diálogos especializados para cada acción con campos de
comentarios y validaciones.
*/

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberImagePainter
import mx.edu.utng.alertavecinal.data.model.Report
import mx.edu.utng.alertavecinal.data.model.ReportStatus
import mx.edu.utng.alertavecinal.data.model.ReportType
import mx.edu.utng.alertavecinal.ui.components.CustomButton
import mx.edu.utng.alertavecinal.ui.components.CustomOutlinedButton
import mx.edu.utng.alertavecinal.ui.components.ErrorMessage
import mx.edu.utng.alertavecinal.ui.components.LoadingIndicator
import mx.edu.utng.alertavecinal.utils.FormatUtils
import mx.edu.utng.alertavecinal.viewmodel.ModeratorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModeratorReportReviewScreen(
    reportId: String,
    moderatorId: String,
    moderatorName: String,
    onBack: () -> Unit,
    onReportUpdated: () -> Unit = {}
) {
    val viewModel: ModeratorViewModel = hiltViewModel()

    val report by viewModel.getReportById(reportId).collectAsState(initial = null)
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    var showApproveDialog by remember { mutableStateOf(false) }
    var showRejectDialog by remember { mutableStateOf(false) }
    var showRequestInfoDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var rejectReason by remember { mutableStateOf("") }
    var requestMessage by remember { mutableStateOf("") }
    var editTitle by remember { mutableStateOf("") }
    var editDescription by remember { mutableStateOf("") }
    var selectedEditType by remember { mutableStateOf<ReportType?>(null) }

    LaunchedEffect(reportId) {
        viewModel.loadReportById(reportId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Revisión de Reporte") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        bottomBar = {
            if (report?.status == ReportStatus.PENDING) {
                BottomActionBar(
                    onApproveClick = { showApproveDialog = true }, // Cambiado para mostrar diálogo
                    onRejectClick = { showRejectDialog = true },
                    onRequestInfoClick = { showRequestInfoDialog = true },
                    onEditClick = {
                        // Inicializar valores de edición con los actuales
                        editTitle = report?.title ?: ""
                        editDescription = report?.description ?: ""
                        selectedEditType = report?.reportType
                        showEditDialog = true
                    }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading && report == null) {
                LoadingIndicator()
            } else if (report == null) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Error",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Reporte no encontrado",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "ID: $reportId",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    CustomButton(
                        text = "Volver al Dashboard",
                        onClick = onBack
                    )
                }
            } else {
                val currentReport = report!!

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    errorMessage?.let { message ->
                        ErrorMessage(message = message)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    ReportHeaderSection(report = currentReport)

                    Spacer(modifier = Modifier.height(16.dp))

                    ReportInfoSection(report = currentReport)

                    Spacer(modifier = Modifier.height(16.dp))

                    if (currentReport.imageUrl != null) {
                        ReportImageSection(imageUrl = currentReport.imageUrl)
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    if (currentReport.approvedBy != null || currentReport.rejectionReason != null) {
                        ModerationHistorySection(report = currentReport)
                    }
                }
            }

            if (showApproveDialog) {
                ApproveReportDialog(
                    onConfirm = {
                        viewModel.approveReport(
                            reportId = reportId,
                            moderatorId = moderatorId,
                            moderatorName = moderatorName,
                            comment = "Aprobado por el moderador"
                        )
                        showApproveDialog = false
                        onReportUpdated()
                    },
                    onDismiss = { showApproveDialog = false }
                )
            }

            if (showRejectDialog) {
                RejectReportDialog(
                    reason = rejectReason,
                    onReasonChange = { rejectReason = it },
                    onConfirm = {
                        viewModel.rejectReport(
                            reportId = reportId,
                            moderatorId = moderatorId,
                            moderatorName = moderatorName,
                            reason = rejectReason
                        )
                        rejectReason = ""
                        showRejectDialog = false
                        onReportUpdated()
                    },
                    onDismiss = {
                        rejectReason = ""
                        showRejectDialog = false
                    }
                )
            }

            if (showRequestInfoDialog) {
                RequestInfoDialog(
                    message = requestMessage,
                    onMessageChange = { requestMessage = it },
                    onConfirm = {
                        viewModel.requestMoreInfo(
                            reportId = reportId,
                            moderatorId = moderatorId,
                            moderatorName = moderatorName,
                            message = requestMessage
                        )
                        requestMessage = ""
                        showRequestInfoDialog = false
                    },
                    onDismiss = {
                        requestMessage = ""
                        showRequestInfoDialog = false
                    }
                )
            }

            if (showEditDialog && report != null) {
                EditReportDialog(
                    currentReport = report!!,
                    title = editTitle,
                    onTitleChange = { editTitle = it },
                    description = editDescription,
                    onDescriptionChange = { editDescription = it },
                    selectedType = selectedEditType,
                    onTypeSelected = { selectedEditType = it },
                    onConfirm = {
                        viewModel.editReport(
                            reportId = reportId,
                            title = if (editTitle != report?.title) editTitle else null,
                            description = if (editDescription != report?.description) editDescription else null,
                            reportType = if (selectedEditType != report?.reportType) selectedEditType?.name else null,
                            moderatorId = moderatorId,
                            moderatorName = moderatorName
                        )
                        editTitle = ""
                        editDescription = ""
                        selectedEditType = null
                        showEditDialog = false
                        onReportUpdated()
                    },
                    onDismiss = {
                        editTitle = ""
                        editDescription = ""
                        selectedEditType = null
                        showEditDialog = false
                    }
                )
            }
        }
    }
}

@Composable
private fun BottomActionBar(
    onApproveClick: () -> Unit,
    onRejectClick: () -> Unit,
    onRequestInfoClick: () -> Unit,
    onEditClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CustomButton(
                text = "Aprobar",
                onClick = onApproveClick,
                modifier = Modifier.weight(1f),
                backgroundColor = MaterialTheme.colorScheme.primary,
                leadingIcon = Icons.Default.CheckCircle
            )

            CustomButton(
                text = "Rechazar",
                onClick = onRejectClick,
                modifier = Modifier.weight(1f),
                backgroundColor = MaterialTheme.colorScheme.error,
                leadingIcon = Icons.Default.Close
            )

            CustomOutlinedButton(
                text = "Más Info",
                onClick = onRequestInfoClick,
                modifier = Modifier.weight(1f),
                leadingIcon = Icons.Default.Info
            )

            CustomOutlinedButton(
                text = "Editar",
                onClick = onEditClick,
                modifier = Modifier.weight(1f),
                leadingIcon = Icons.Default.Edit
            )
        }
    }
}

@Composable
private fun ReportHeaderSection(report: Report) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                    color = MaterialTheme.colorScheme.onSurface
                )

                StatusBadgeLarge(status = report.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = report.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun StatusBadgeLarge(status: ReportStatus) {
    val (backgroundColor, textColor, text) = when (status) {
        ReportStatus.PENDING -> Triple(Color(0xFFFFA000), Color(0xFF000000), "PENDIENTE")
        ReportStatus.APPROVED -> Triple(Color(0xFF4CAF50), Color(0xFFFFFFFF), "APROBADO")
        ReportStatus.REJECTED -> Triple(Color(0xFFF44336), Color(0xFFFFFFFF), "RECHAZADO")
    }

    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = textColor,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    )
}

@Composable
private fun ReportInfoSection(report: Report) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Tipo de reporte
            InfoRow(
                icon = Icons.Default.Warning,
                title = "Tipo de Incidente",
                value = report.reportType.name.replace("_", " ")
            )

            Divider()

            // Usuario que reportó
            InfoRow(
                icon = Icons.Default.Person,
                title = "Reportado por",
                value = report.userName
            )

            Divider()

            InfoRow(
                icon = Icons.Default.LocationOn,
                title = "Ubicación",
                value = report.address ?: "No especificada"
            )

            Divider()

            InfoRow(
                icon = Icons.Default.Schedule,
                title = "Fecha y Hora",
                value = FormatUtils.formatDate(report.createdAt)
            )

            Divider()

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
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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

            Image(
                painter = rememberImagePainter(data = imageUrl),
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
private fun ModerationHistorySection(report: Report) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                    icon = Icons.Default.CheckCircle,
                    title = "Moderado por",
                    value = approver
                )
            }

            report.rejectionReason?.let { reason ->
                Spacer(modifier = Modifier.height(8.dp))
                InfoRow(
                    icon = Icons.Default.Close,
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

@Composable
private fun ApproveReportDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("¿Aprobar este reporte?") },
        text = {
            Column {
                Text("El reporte será visible para todos los usuarios en el mapa.")
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Esta acción no se puede deshacer.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Aprobar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun RejectReportDialog(
    reason: String,
    onReasonChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("¿Rechazar este reporte?") },
        text = {
            Column {
                Text("Por favor, especifica el motivo del rechazo:")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = reason,
                    onValueChange = onReasonChange,
                    label = { Text("Motivo del rechazo") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = reason.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Rechazar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun RequestInfoDialog(
    message: String,
    onMessageChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Solicitar más información") },
        text = {
            Column {
                Text("¿Qué información adicional necesitas del usuario?")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = message,
                    onValueChange = onMessageChange,
                    label = { Text("Mensaje para el usuario") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = message.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Enviar solicitud")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun EditReportDialog(
    currentReport: Report,
    title: String,
    onTitleChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    selectedType: ReportType?,
    onTypeSelected: (ReportType?) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Reporte") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = if (title.isBlank()) currentReport.title else title,
                    onValueChange = onTitleChange,
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = if (description.isBlank()) currentReport.description else description,
                    onValueChange = onDescriptionChange,
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )

                Text(
                    text = "Tipo de Incidente",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                // Selector de tipo de reporte
                ReportTypeSelector(
                    selectedType = selectedType ?: currentReport.reportType,
                    onTypeSelected = { onTypeSelected(it) }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Guardar cambios")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun ReportTypeSelector(
    selectedType: ReportType,
    onTypeSelected: (ReportType) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Reemplazamos chunked() con una solución manual
        val reportTypes = ReportType.values()
        val chunkedList = mutableListOf<List<ReportType>>()
        val chunkSize = 3

        for (i in reportTypes.indices step chunkSize) {
            val end = (i + chunkSize).coerceAtMost(reportTypes.size)
            chunkedList.add(reportTypes.slice(i until end))
        }

        chunkedList.forEach { rowTypes ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowTypes.forEach { reportType ->
                    TypeChip(
                        type = reportType,
                        isSelected = selectedType == reportType,
                        onClick = { onTypeSelected(reportType) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TypeChip(
    type: ReportType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = type.name.replace("_", " "),
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}