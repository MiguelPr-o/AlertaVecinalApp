package mx.edu.utng.alertavecinal.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

object FormatUtils {

    private val displayDateFormat = SimpleDateFormat(Constants.DATE_FORMAT_DISPLAY, Locale.getDefault())
    private val storageDateFormat = SimpleDateFormat(Constants.DATE_FORMAT_STORAGE, Locale.getDefault())
    private val timeFormat = SimpleDateFormat(Constants.TIME_FORMAT, Locale.getDefault())

    fun formatDate(timestamp: Long): String {
        return displayDateFormat.format(Date(timestamp))
    }

    fun formatDate(date: Date): String {
        return displayDateFormat.format(date)
    }

    fun formatTime(timestamp: Long): String {
        return timeFormat.format(Date(timestamp))
    }

    fun formatStorageDate(date: Date): String {
        return storageDateFormat.format(date)
    }

    fun parseStorageDate(dateString: String): Date? {
        return try {
            storageDateFormat.parse(dateString)
        } catch (e: Exception) {
            null
        }
    }

    fun formatRelativeTime(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        return when {
            diff < TimeUnit.MINUTES.toMillis(1) -> "Hace un momento"
            diff < TimeUnit.HOURS.toMillis(1) -> {
                val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
                "Hace ${minutes} minuto${if (minutes > 1) "s" else ""}"
            }
            diff < TimeUnit.DAYS.toMillis(1) -> {
                val hours = TimeUnit.MILLISECONDS.toHours(diff)
                "Hace ${hours} hora${if (hours > 1) "s" else ""}"
            }
            diff < TimeUnit.DAYS.toMillis(7) -> {
                val days = TimeUnit.MILLISECONDS.toDays(diff)
                "Hace ${days} día${if (days > 1) "s" else ""}"
            }
            else -> formatDate(timestamp)
        }
    }

    fun formatDistance(meters: Float): String {
        return when {
            meters < 1000 -> "${meters.roundToInt()} m"
            else -> "${"%.1f".format(meters / 1000)} km"
        }
    }

    fun formatAddress(
        street: String? = null,
        neighborhood: String? = null,
        city: String? = null,
        state: String? = null
    ): String {
        return listOfNotNull(street, neighborhood, city, state)
            .joinToString(", ")
            .takeIf { it.isNotEmpty() } ?: "Ubicación no disponible"
    }

    fun formatReportType(type: String): String {
        return type.replace("_", " ").lowercase()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }

    fun formatUserName(fullName: String): String {
        return fullName.split(" ")
            .take(2)
            .joinToString(" ") { it.replaceFirstChar { char ->
                if (char.isLowerCase()) char.titlecase(Locale.getDefault()) else char.toString()
            } }
    }

    fun formatPhoneNumber(phone: String): String {
        return if (phone.length == 10) {
            "(${phone.substring(0, 3)}) ${phone.substring(3, 6)}-${phone.substring(6)}"
        } else {
            phone
        }
    }

    fun capitalizeText(text: String): String {
        return text.lowercase()
            .split(" ")
            .joinToString(" ") { it.replaceFirstChar { char ->
                if (char.isLowerCase()) char.titlecase(Locale.getDefault()) else char.toString()
            } }
    }

    fun truncateText(text: String, maxLength: Int): String {
        return if (text.length > maxLength) {
            text.substring(0, maxLength - 3) + "..."
        } else {
            text
        }
    }

    fun formatFileSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${"%.1f".format(bytes / 1024.0)} KB"
            bytes < 1024 * 1024 * 1024 -> "${"%.1f".format(bytes / (1024.0 * 1024.0))} MB"
            else -> "${"%.1f".format(bytes / (1024.0 * 1024.0 * 1024.0))} GB"
        }
    }
}