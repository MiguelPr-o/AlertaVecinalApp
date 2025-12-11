package mx.edu.utng.alertavecinal.ui.components

/*
Clase ReportActionsModal: Este archivo contiene un conjunto de componentes
modales y diálogos diseñados específicamente para las acciones de
moderación de reportes. Incluye modales para aprobar, rechazar, solicitar
más información y editar reportes, proporcionando interfaces especializadas
para cada acción con campos de comentario y confirmación apropiados.
*/

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

enum class ModeratorAction {
    APPROVE, REJECT, REQUEST_INFO, EDIT
}

data class ActionInfo(
    val title: String,
    val message: String,
    val icon: ImageVector,
    val confirmText: String,
    val requiresComment: Boolean
)

@Composable
fun ReportActionsModal(
    action: ModeratorAction,
    currentComment: String = "",
    onActionConfirmed: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var comment by remember { mutableStateOf(currentComment) }

    val actionInfo = when (action) {
        ModeratorAction.APPROVE -> ActionInfo(
            title = "¿Aprobar este reporte?",
            message = "El reporte será visible para todos los usuarios en el mapa. Puedes agregar un comentario opcional:",
            icon = Icons.Default.CheckCircle,
            confirmText = "Aprobar",
            requiresComment = false
        )
        ModeratorAction.REJECT -> ActionInfo(
            title = "¿Rechazar este reporte?",
            message = "Por favor, especifica el motivo del rechazo. Esto será visible para el usuario:",
            icon = Icons.Default.Close,
            confirmText = "Rechazar",
            requiresComment = true
        )
        ModeratorAction.REQUEST_INFO -> ActionInfo(
            title = "Solicitar más información",
            message = "¿Qué información adicional necesitas del usuario?",
            icon = Icons.Default.Info,
            confirmText = "Enviar solicitud",
            requiresComment = true
        )
        ModeratorAction.EDIT -> ActionInfo(
            title = "Editar reporte",
            message = "Realiza los cambios necesarios. Se notificará al usuario sobre las modificaciones:",
            icon = Icons.Default.Edit,
            confirmText = "Guardar cambios",
            requiresComment = false
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = actionInfo.icon,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = actionInfo.title,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column {
                Text(
                    text = actionInfo.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (action == ModeratorAction.EDIT) {
                    // Campos para edición
                    OutlinedTextField(
                        value = comment,
                        onValueChange = { comment = it },
                        label = { Text("Título (opcional)") },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Dejar vacío para no cambiar") }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = comment,
                        onValueChange = { comment = it },
                        label = { Text("Descripción (opcional)") },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Dejar vacío para no cambiar") },
                        minLines = 3,
                        maxLines = 5
                    )
                } else if (actionInfo.requiresComment || action == ModeratorAction.APPROVE) {
                    // Campo de comentario
                    OutlinedTextField(
                        value = comment,
                        onValueChange = { comment = it },
                        label = {
                            Text(
                                if (action == ModeratorAction.APPROVE)
                                    "Comentario (opcional)"
                                else
                                    "Comentario"
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                when (action) {
                                    ModeratorAction.APPROVE -> "Ej: Verificado por el equipo de moderación"
                                    ModeratorAction.REJECT -> "Ej: Contenido inapropiado o información insuficiente"
                                    ModeratorAction.REQUEST_INFO -> "Ej: ¿Podrías proporcionar más detalles sobre lo sucedido?"
                                    else -> ""
                                }
                            )
                        },
                        minLines = 3,
                        maxLines = 5
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (!actionInfo.requiresComment || comment.isNotBlank()) {
                        onActionConfirmed(comment)
                    }
                },
                enabled = !actionInfo.requiresComment || comment.isNotBlank(),
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text(actionInfo.confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

// Versión simplificada para acciones rápidas
@Composable
fun QuickActionModal(
    title: String,
    message: String,
    confirmText: String,
    cancelText: String = "Cancelar",
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(message)
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text(cancelText)
            }
        }
    )
}

// Modal para confirmar eliminación
@Composable
fun ConfirmDeleteModal(
    title: String = "¿Eliminar reporte?",
    message: String = "Esta acción no se puede deshacer. El reporte será eliminado permanentemente.",
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
        },
        text = {
            Text(message)
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Eliminar")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text("Cancelar")
            }
        }
    )
}