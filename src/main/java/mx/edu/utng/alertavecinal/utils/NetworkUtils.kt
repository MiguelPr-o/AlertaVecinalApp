package mx.edu.utng.alertavecinal.utils

/*
Clase NetworkUtils: Este objeto proporciona funciones para monitorear
y gestionar el estado de conectividad de red en la aplicación. Incluye verificación
de disponibilidad de internet, detección del tipo de conexión (WiFi, datos móviles),
registro de callbacks para cambios en la red y utilidades para simular condiciones
de red durante pruebas.
*/

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object NetworkUtils {

    private val _isNetworkAvailable = MutableStateFlow(true)
    val isNetworkAvailable: StateFlow<Boolean> = _isNetworkAvailable

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            capabilities != null && (
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                    )
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo
            networkInfo != null && networkInfo.isConnected
        }
    }

    fun registerNetworkCallback(context: Context) {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                _isNetworkAvailable.value = true
            }

            override fun onLost(network: Network) {
                _isNetworkAvailable.value = false
            }

            override fun onUnavailable() {
                _isNetworkAvailable.value = false
            }
        })
    }

    /**
     * Obtiene el tipo de conexión actual
     */
    fun getConnectionType(context: Context): String {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)

            when {
                capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true -> "WiFi"
                capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> "Datos móviles"
                capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true -> "Ethernet"
                else -> "Desconocido"
            }
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo
            when (networkInfo?.type) {
                ConnectivityManager.TYPE_WIFI -> "WiFi"
                ConnectivityManager.TYPE_MOBILE -> "Datos móviles"
                ConnectivityManager.TYPE_ETHERNET -> "Ethernet"
                else -> "Desconocido"
            }
        }
    }

    fun isConnectionGoodForUpload(context: Context): Boolean {
        val connectionType = getConnectionType(context)
        return connectionType == "WiFi" || connectionType == "Ethernet"
    }

    suspend fun simulateNetworkDelay() {
        kotlinx.coroutines.delay(1000L) // 1 segundo de delay simulado
    }
}