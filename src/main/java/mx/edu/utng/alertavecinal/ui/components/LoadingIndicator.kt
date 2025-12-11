package mx.edu.utng.alertavecinal.ui.components

/*
Clase LoadingIndicator: Este archivo contiene varios componentes
reutilizables para mostrar estados de carga en diferentes contextos
de la aplicación. Incluye indicadores de carga desde pequeños hasta
pantalla completa y un diálogo de carga modal, proporcionando una
experiencia visual consistente durante operaciones asíncronas en toda la app.
*/

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 4.dp
        )
    }
}

@Composable
fun FullScreenLoading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(64.dp),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 4.dp
        )
    }
}

@Composable
fun LoadingDialog(
    onDismissRequest: () -> Unit = {}
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(64.dp),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 4.dp
        )
    }
}