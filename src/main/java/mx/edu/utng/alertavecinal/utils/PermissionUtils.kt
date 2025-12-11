package mx.edu.utng.alertavecinal.utils

/*
Clase PermissionUtils: Este objeto maneja toda la gestión centralizada de permisos
en tiempo de ejecución para la aplicación. Organiza los permisos requeridos por
funcionalidad (ubicación, cámara, almacenamiento, notificaciones), proporciona
métodos para verificar su estado, identifica permisos faltantes, y ofrece
descripciones amigables para cada permiso. Simplifica la implementación de la
lógica de permisos siguiendo las mejores prácticas de Android.
*/
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

object PermissionUtils {

    // Lista de permisos requeridos para la aplicación
    val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.POST_NOTIFICATIONS
    )

    // Permisos para funcionalidades específicas
    val LOCATION_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    val CAMERA_PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    val GALLERY_PERMISSIONS = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    val NOTIFICATION_PERMISSIONS = arrayOf(
        Manifest.permission.POST_NOTIFICATIONS
    )

    /**
     * Verifica si todos los permisos están concedidos
     */
    fun hasAllPermissions(context: Context, permissions: Array<String>): Boolean {
        return permissions.all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * Verifica si los permisos de ubicación están concedidos
     */
    fun hasLocationPermissions(context: Context): Boolean {
        return hasAllPermissions(context, LOCATION_PERMISSIONS)
    }

    /**
     * Verifica si los permisos de cámara están concedidos
     */
    fun hasCameraPermissions(context: Context): Boolean {
        return hasAllPermissions(context, CAMERA_PERMISSIONS)
    }

    /**
     * Verifica si los permisos de galería están concedidos
     */
    fun hasGalleryPermissions(context: Context): Boolean {
        return hasAllPermissions(context, GALLERY_PERMISSIONS)
    }

    /**
     * Verifica si los permisos de notificaciones están concedidos
     */
    fun hasNotificationPermissions(context: Context): Boolean {
        return hasAllPermissions(context, NOTIFICATION_PERMISSIONS)
    }

    /**
     * Obtiene los permisos que no han sido concedidos
     */
    fun getDeniedPermissions(context: Context, permissions: Array<String>): Array<String> {
        return permissions.filter { permission ->
            ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()
    }

    /**
     * Verifica si se debe mostrar la explicación de los permisos
     */
    fun shouldShowPermissionRationale(context: Context, permissions: Array<String>): Boolean {
        // En una implementación real, usarías ActivityCompat.shouldShowRequestPermissionRationale
        // Por ahora retornamos false como valor por defecto
        return false
    }

    /**
     * Obtiene el mensaje descriptivo para un permiso
     */
    fun getPermissionDescription(permission: String): String {
        return when (permission) {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION -> "Ubicación para mostrar incidentes cercanos"

            Manifest.permission.CAMERA -> "Cámara para tomar fotos de incidentes"

            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE -> "Almacenamiento para guardar y cargar imágenes"

            Manifest.permission.POST_NOTIFICATIONS -> "Notificaciones para alertas de incidentes"

            else -> "Funcionalidad de la aplicación"
        }
    }

    /**
     * Obtiene el nombre legible para un permiso
     */
    fun getPermissionName(permission: String): String {
        return when (permission) {
            Manifest.permission.ACCESS_FINE_LOCATION -> "Ubicación precisa"
            Manifest.permission.ACCESS_COARSE_LOCATION -> "Ubicación aproximada"
            Manifest.permission.CAMERA -> "Cámara"
            Manifest.permission.WRITE_EXTERNAL_STORAGE -> "Escritura en almacenamiento"
            Manifest.permission.READ_EXTERNAL_STORAGE -> "Lectura de almacenamiento"
            Manifest.permission.POST_NOTIFICATIONS -> "Notificaciones"
            else -> "Permiso desconocido"
        }
    }
}