package mx.edu.utng.alertavecinal

/*
Clase AlertaVecinalApp: Clase Application personalizada que sirve como punto
de entrada principal de la aplicación. Configurada con la anotación
@HiltAndroidApp para habilitar la inyección de dependencias a través de
Hilt en toda la aplicación. Aquí se pueden realizar inicializaciones
globales como configuraciones de Firebase, Analytics, Crashlytics, o
cualquier otra librería que necesite inicialización al iniciar la app.
*/

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