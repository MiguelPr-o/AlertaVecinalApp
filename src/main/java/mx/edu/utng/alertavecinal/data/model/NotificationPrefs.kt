package mx.edu.utng.alertavecinal.data.model

data class NotificationPrefs(
    val userId: String = "",
    val enabled: Boolean = true,
    val radius: Int = 1000, // Radio en metros
    val types: List<NotificationType> = listOf(
        NotificationType.REPORT_APPROVED,
        NotificationType.REPORT_REJECTED,
        NotificationType.NEW_INCIDENT_NEARBY
    ),
    val silentHours: Boolean = false,
    val silentStart: Int = 22, // 10 PM
    val silentEnd: Int = 7     // 7 AM
)