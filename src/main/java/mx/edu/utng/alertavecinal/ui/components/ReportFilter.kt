package mx.edu.utng.alertavecinal.ui.components

/*
Clase ReportFilter: Este componente proporciona un filtro interactivo de
tipo "chip" para filtrar reportes por categoría en la aplicación. Muestra
una fila horizontal de opciones de filtro (todos los tipos de reporte
más categorías específicas) que los usuarios pueden seleccionar para ver
solo los reportes de cierto tipo.
*/

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mx.edu.utng.alertavecinal.data.model.ReportType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportFilter(
    selectedType: ReportType?,
    onTypeSelected: (ReportType?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                text = "Todos",
                selected = selectedType == null,
                onClick = { onTypeSelected(null) }
            )
        }

        items(ReportType.values()) { type ->
            FilterChip(
                text = type.name.replace("_", " "),
                selected = selectedType == type,
                onClick = { onTypeSelected(type) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    AssistChip(
        onClick = onClick,
        label = { Text(text) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            },
            labelColor = if (selected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        ),
        shape = RoundedCornerShape(16.dp)
    )
}