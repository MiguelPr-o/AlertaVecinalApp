package mx.edu.utng.alertavecinal.utils

/*
Clase NotificationUtils: Este objeto maneja toda la lógica relacionada con
notificaciones push en la aplicación, incluyendo la creación de canales de
notificación (para Android 8+), el envío de diferentes tipos de notificaciones
(aprobación/rechazo de reportes, alertas de incidentes cercanos) y la gestión
del estado de las notificaciones. Proporciona una API unificada para todas las
operaciones de notificación en el sistema.
*/

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import mx.edu.utng.alertavecinal.R

object NotificationUtils {

    // Canales de notificación
    const val CHANNEL_ID_REPORTS = "channel_reports"
    const val CHANNEL_ID_ALERTS = "channel_alerts"
    const val CHANNEL_ID_GENERAL = "channel_general"

    // IDs de notificación
    private var notificationId = 1000

    // Permiso para notificaciones (requerido desde Android 13/API 33)
    private const val PERMISSION_POST_NOTIFICATIONS = android.Manifest.permission.POST_NOTIFICATIONS

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Canal para reportes
            val reportsChannel = NotificationChannel(
                CHANNEL_ID_REPORTS,
                "Reportes de Incidentes",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones sobre el estado de tus reportes"
                enableLights(true)
                enableVibration(true)
            }

            // Canal para alertas
            val alertsChannel = NotificationChannel(
                CHANNEL_ID_ALERTS,
                "Alertas Vecinales",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alertas sobre incidentes cercanos"
                enableLights(true)
                enableVibration(true)
                setShowBadge(true)
            }

            // Canal para notificaciones generales
            val generalChannel = NotificationChannel(
                CHANNEL_ID_GENERAL,
                "Notificaciones Generales",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notificaciones generales de la aplicación"
                enableLights(true)
                enableVibration(false)
            }

            notificationManager.createNotificationChannels(
                listOf(reportsChannel, alertsChannel, generalChannel)
            )
        }
    }

    /**
     * Verifica si la aplicación tiene permiso para mostrar notificaciones
     */
    fun checkNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Para Android 13+ necesitamos verificar explícitamente el permiso
            ActivityCompat.checkSelfPermission(
                context,
                PERMISSION_POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // Para versiones anteriores, las notificaciones están habilitadas por defecto
            true
        }
    }

    fun showSimpleNotification(
        context: Context,
        title: String,
        message: String,
        channelId: String = CHANNEL_ID_GENERAL
    ) {
        // Verificar permiso antes de mostrar la notificación
        if (!checkNotificationPermission(context)) {
            // Opcional: Registrar el intento fallido o pedir el permiso
            return
        }

        val notificationId = getNextNotificationId()

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        try {
            with(NotificationManagerCompat.from(context)) {
                notify(notificationId, builder.build())
            }
        } catch (securityException: SecurityException) {
            // Manejar la excepción de seguridad
            // Podrías registrar el error o intentar solicitar el permiso
            println("Error de seguridad al mostrar notificación: ${securityException.message}")
        }
    }

    fun showReportApprovedNotification(context: Context, reportTitle: String) {
        if (!checkNotificationPermission(context)) {
            return
        }

        val notificationId = getNextNotificationId()

        val builder = NotificationCompat.Builder(context, CHANNEL_ID_REPORTS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Reporte Aprobado")
            .setContentText("Tu reporte '$reportTitle' ha sido aprobado")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        try {
            with(NotificationManagerCompat.from(context)) {
                notify(notificationId, builder.build())
            }
        } catch (securityException: SecurityException) {
            println("Error de seguridad al mostrar notificación de aprobación: ${securityException.message}")
        }
    }

    fun showReportRejectedNotification(context: Context, reportTitle: String, reason: String?) {
        if (!checkNotificationPermission(context)) {
            return
        }

        val notificationId = getNextNotificationId()

        val message = if (!reason.isNullOrEmpty()) {
            "Tu reporte '$reportTitle' fue rechazado: $reason"
        } else {
            "Tu reporte '$reportTitle' fue rechazado"
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID_REPORTS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Reporte Rechazado")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        try {
            with(NotificationManagerCompat.from(context)) {
                notify(notificationId, builder.build())
            }
        } catch (securityException: SecurityException) {
            println("Error de seguridad al mostrar notificación de rechazo: ${securityException.message}")
        }
    }

    fun showNearbyIncidentAlert(
        context: Context,
        incidentType: String,
        distance: Float,
        address: String?
    ) {
        if (!checkNotificationPermission(context)) {
            return
        }

        val notificationId = getNextNotificationId()

        val distanceText = FormatUtils.formatDistance(distance)
        val locationText = address ?: "Cerca de tu ubicación"

        val builder = NotificationCompat.Builder(context, CHANNEL_ID_ALERTS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Incidente Cercano: $incidentType")
            .setContentText("A $distanceText - $locationText")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setTimeoutAfter(300000)

        try {
            with(NotificationManagerCompat.from(context)) {
                notify(notificationId, builder.build())
            }
        } catch (securityException: SecurityException) {
            println("Error de seguridad al mostrar alerta de incidente: ${securityException.message}")
        }
    }

    fun areNotificationsEnabled(context: Context): Boolean {
        return NotificationManagerCompat.from(context).areNotificationsEnabled() &&
                checkNotificationPermission(context)
    }

    private fun getNextNotificationId(): Int {
        return notificationId++.also {
            if (it > 9999) notificationId = 1000
        }
    }

    fun cancelNotification(context: Context, notificationId: Int) {
        NotificationManagerCompat.from(context).cancel(notificationId)
    }

    fun cancelAllNotifications(context: Context) {
        NotificationManagerCompat.from(context).cancelAll()
    }
}