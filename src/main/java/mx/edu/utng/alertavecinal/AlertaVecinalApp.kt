package mx.edu.utng.alertavecinal

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AlertaVecinalApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Inicializaciones globales de la aplicación
        // Firebase, Analytics, etc. se inicializan automáticamente con Hilt
    }
}