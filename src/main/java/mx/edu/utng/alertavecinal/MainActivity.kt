package mx.edu.utng.alertavecinal

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mx.edu.utng.alertavecinal.ui.navigation.AppNavigation
import mx.edu.utng.alertavecinal.ui.theme.AlertaVecinalTheme
import mx.edu.utng.alertavecinal.viewmodel.AuthViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // ✅ NUEVO: Registrar para solicitar permisos de ubicación
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (fineLocationGranted || coarseLocationGranted) {
            // Permisos concedidos - la ubicación funcionará
            println("✅ Permisos de ubicación concedidos")
        } else {
            // Permisos denegados - mostrar mensaje al usuario
            println("❌ Permisos de ubicación denegados")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AlertaVecinalTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()

                    // ✅ NUEVO: Solicitar permisos automáticamente al iniciar
                    LaunchedEffect(Unit) {
                        requestLocationPermissions()
                    }
                }
            }
        }
    }



    // ✅ NUEVO: Función para solicitar permisos de ubicación
    private fun requestLocationPermissions() {
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
}


