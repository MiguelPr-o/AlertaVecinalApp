// LoginScreen.kt (VERSIÓN COMPLETA ACTUALIZADA)
package mx.edu.utng.alertavecinal.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import mx.edu.utng.alertavecinal.data.model.UserRole
import mx.edu.utng.alertavecinal.ui.components.CustomButton
import mx.edu.utng.alertavecinal.ui.components.CustomTextField
import mx.edu.utng.alertavecinal.ui.components.ErrorMessage
import mx.edu.utng.alertavecinal.utils.Constants
import mx.edu.utng.alertavecinal.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsState()
    val shouldNavigate by viewModel.shouldNavigate.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showDebugPanel by remember { mutableStateOf(false) }

    // ✅ REDIRECCIÓN USANDO EL FLUJO DEL VIEWMODEL
    LaunchedEffect(shouldNavigate) {
        shouldNavigate?.let { destination ->
            println("🟢 LoginScreen - Redirigiendo a: $destination")

            when (destination) {
                Constants.ROUTE_MODERATOR_DASHBOARD -> {
                    navController.navigate(destination) {
                        popUpTo(Constants.ROUTE_LOGIN) { inclusive = true }
                    }
                }
                Constants.ROUTE_MAP -> {
                    navController.navigate(destination) {
                        popUpTo(Constants.ROUTE_WELCOME) { inclusive = true }
                    }
                }
            }

            // Limpiar el estado de navegación
            viewModel.clearNavigation()
        }
    }

    // ✅ REDIRECCIÓN ALTERNATIVA (backup)
    LaunchedEffect(authState.isAuthenticated) {
        if (authState.isAuthenticated && authState.currentUser != null && shouldNavigate == null) {
            println("⚠️ LoginScreen - Redirección alternativa activada")
            val userRole = authState.currentUser?.role

            val destination = when (userRole) {
                UserRole.MODERATOR, UserRole.ADMIN -> {
                    println("🎯 Usuario es moderador/admin")
                    Constants.ROUTE_MODERATOR_DASHBOARD
                }
                else -> {
                    println("🎯 Usuario es normal")
                    Constants.ROUTE_MAP
                }
            }

            println("🟢 Redirigiendo a: $destination")
            navController.navigate(destination) {
                popUpTo(Constants.ROUTE_WELCOME) { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Iniciar Sesión") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                actions = {

                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Logo/Icono
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            MaterialTheme.shapes.medium
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Login",
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Título
                Text(
                    text = "Bienvenido de nuevo",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Ingresa a tu cuenta",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(48.dp))

                // Campo de email
                CustomTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Correo electrónico",
                    leadingIcon = Icons.Default.Email,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Campo de contraseña
                CustomTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Contraseña",
                    leadingIcon = Icons.Default.Lock,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Mostrar error si existe
                authState.error?.let { error ->
                    ErrorMessage(message = error)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Mostrar información del usuario si está autenticado
                if (authState.isAuthenticated) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .background(
                                            Color.Green,
                                            MaterialTheme.shapes.small
                                        )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "✅ Autenticado",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            authState.currentUser?.let { user ->
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Usuario: ${user.name}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Email: ${user.email}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )

                                // ✅ MOSTRAR ROL CORRECTAMENTE
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Rol: ",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = user.role.getDisplayName(),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = when (user.role) {
                                            UserRole.MODERATOR -> Color(0xFF4CAF50)
                                            UserRole.ADMIN -> Color(0xFF2196F3)
                                            else -> MaterialTheme.colorScheme.onSurface
                                        }
                                    )
                                }

                                Text(
                                    text = "Redirección: ${shouldNavigate ?: "Determinando..."}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }

                // Botón de iniciar sesión
                CustomButton(
                    text = if (authState.isLoading) "Iniciando sesión..." else "Iniciar Sesión",
                    onClick = {
                        println("🔍 LoginScreen - Iniciando login para: $email")
                        viewModel.login(email, password)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = email.isNotEmpty() && password.isNotEmpty() && !authState.isLoading,
                    isLoading = authState.isLoading
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Enlace a registro
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "¿No tienes cuenta? ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "Regístrate",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .clickable {
                                navController.navigate(Constants.ROUTE_REGISTER)
                            }
                            .padding(horizontal = 4.dp)
                    )
                }

                // Loading indicator
                if (authState.isLoading) {
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    MaterialTheme {
        LoginScreen(navController = rememberNavController())
    }
}