package mx.edu.utng.alertavecinal.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
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

    /**
     * Crea los canales de notificación (requerido para Android 8.0+)
     */
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
     * Muestra una notificación simple
     */
    fun showSimpleNotification(
        context: Context,
        title: String,
        message: String,
        channelId: String = CHANNEL_ID_GENERAL
    ) {
        val notificationId = getNextNotificationId()

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, builder.build())
        }
    }

    /**
     * Muestra una notificación de reporte aprobado
     */
    fun showReportApprovedNotification(context: Context, reportTitle: String) {
        val notificationId = getNextNotificationId()

        val builder = NotificationCompat.Builder(context, CHANNEL_ID_REPORTS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Reporte Aprobado")
            .setContentText("Tu reporte '$reportTitle' ha sido aprobado")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, builder.build())
        }
    }

    /**
     * Muestra una notificación de reporte rechazado
     */
    fun showReportRejectedNotification(context: Context, reportTitle: String, reason: String?) {
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

        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, builder.build())
        }
    }

    /**
     * Muestra una alerta de incidente cercano
     */
    fun showNearbyIncidentAlert(
        context: Context,
        incidentType: String,
        distance: Float,
        address: String?
    ) {
        val notificationId = getNextNotificationId()

        val distanceText = FormatUtils.formatDistance(distance)
        val locationText = address ?: "Cerca de tu ubicación"

        val builder = NotificationCompat.Builder(context, CHANNEL_ID_ALERTS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Incidente Cercano: $incidentType")
            .setContentText("A $distanceText - $locationText")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setTimeoutAfter(300000) // 5 minutos

        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, builder.build())
        }
    }

    /**
     * Verifica si las notificaciones están habilitadas
     */
    fun areNotificationsEnabled(context: Context): Boolean {
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    /**
     * Obtiene el siguiente ID de notificación único
     */
    private fun getNextNotificationId(): Int {
        return notificationId++.also {
            if (it > 9999) notificationId = 1000 // Reset si llega al límite
        }
    }

    /**
     * Cancela una notificación específica
     */
    fun cancelNotification(context: Context, notificationId: Int) {
        NotificationManagerCompat.from(context).cancel(notificationId)
    }

    /**
     * Cancela todas las notificaciones
     */
    fun cancelAllNotifications(context: Context) {
        NotificationManagerCompat.from(context).cancelAll()
    }
}