package mx.edu.utng.alertavecinal.ui.screens

/*
Clase WelcomeScreen: Esta es la pantalla inicial de la aplicación que da
la bienvenida a los usuarios y presenta las opciones principales de acceso:
iniciar sesión para usuarios existentes o registrarse para nuevos usuarios.
Sirve como punto de entrada a la aplicación y establece el tono visual y
de funcionalidad para el resto de la experiencia de usuario
*/

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import mx.edu.utng.alertavecinal.BuildConfig
import mx.edu.utng.alertavecinal.R
import mx.edu.utng.alertavecinal.ui.components.CustomButton
import mx.edu.utng.alertavecinal.utils.Constants
import mx.edu.utng.alertavecinal.viewmodel.AuthViewModel

@Composable
fun WelcomeScreen(
    navController: NavController
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Logo Alerta Vecinal",
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Alerta Vecinal",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Mantente informado y protege tu comunidad reportando incidentes en tu área",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(64.dp))

            CustomButton(
                text = "Iniciar Sesión",
                onClick = { navController.navigate("login") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            CustomButton(
                text = "Registrarse",
                onClick = { navController.navigate("register") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                backgroundColor = MaterialTheme.colorScheme.secondary
            )

        }
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    WelcomeScreen(navController = rememberNavController())
}