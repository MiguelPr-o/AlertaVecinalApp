package mx.edu.utng.alertavecinal.ui.components

/*
Clase ErrorMessage: Este componente muestra un mensaje de error estilizado
dentro de un contenedor con fondo de color de error del tema de Material
Design. Proporciona una forma consistente y visualmente clara de mostrar
mensajes de error al usuario en toda la aplicación.
*/

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ErrorMessage(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.errorContainer,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(12.dp)
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.onErrorContainer,
            style = MaterialTheme.typography.bodySmall
        )
    }
}