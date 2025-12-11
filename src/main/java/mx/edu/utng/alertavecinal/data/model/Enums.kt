package mx.edu.utng.alertavecinal.data.model

/*
Clase Enums (fichero Enums.kt): Este archivo contiene todos los
enumerados (enums) que definen los tipos, estados y roles dentro
de la aplicación "Alerta Vecinal". Sirve como una fuente única
de verdad para las constantes del sistema, asegurando consistencia
en tipos de reportes, estados de moderación, roles de usuario y
categorías de notificaciones en toda la aplicación.
 */

enum class UserRole {
    USER,
    MODERATOR,
    ADMIN;

    // Función para obtener el nombre legible
    fun getDisplayName(): String {
        return when (this) {
            USER -> "Usuario"
            MODERATOR -> "Moderador"
            ADMIN -> "Administrador"
        }
    }

    // Función para obtener el nombre en inglés (para comparaciones)
    fun getFirestoreName(): String {
        return when (this) {
            USER -> "USER"
            MODERATOR -> "MODERATOR"
            ADMIN -> "ADMIN"
        }
    }

    companion object {
        // Convertir de String a UserRole
        fun fromString(value: String?): UserRole {
            return when (value?.uppercase()) {
                "MODERATOR", "MODERADOR" -> MODERATOR
                "ADMIN", "ADMINISTRADOR" -> ADMIN
                else -> USER
            }
        }
    }
}

enum class ReportType {
    ROBBERY,           // Robo
    FIRE,              // Incendio
    ACCIDENT,          // Accidente
    SUSPICIOUS_PERSON, // Persona sospechosa
    FIGHT,             // Pelea
    VANDALISM,         // Vandalismo
    NOISE,             // Ruido
    LOST_PET,          // Mascota perdida
    OTHER              // Otro
}

enum class ReportStatus {
    PENDING,   // Pendiente de moderación
    APPROVED,  // Aprobado y visible
    REJECTED   // Rechazado
}

enum class NotificationType {
    REPORT_APPROVED,    // Reporte aprobado
    REPORT_REJECTED,    // Reporte rechazado
    NEW_INCIDENT_NEARBY, // Nuevo incidente cercano
    INFO_REQUESTED       // Se solicitó más información (para moderador)
}

enum class ModerationAction {
    APPROVE,        // Aprobar reporte
    REJECT,         // Rechazar reporte
    EDIT,           // Editar reporte
    REQUEST_INFO,   // Solicitar más información
    DELETE          // Eliminar reporte (solo admin)
}

enum class ModeratorFilter {
    ALL,            // Todos los reportes
    PENDING,        // Solo pendientes
    APPROVED,       // Solo aprobados
    REJECTED,       // Solo rechazados
    TODAY,          // Reportes de hoy
    URGENT          // Reportes urgentes (robos, incendios, etc.)
}

enum class StatType {
    PENDING_COUNT,
    APPROVED_COUNT,
    REJECTED_COUNT,
    TOTAL_REPORTS,
    RESPONSE_TIME,
    APPROVAL_RATE
}

enum class UserType {
    REGULAR_USER,
    MODERATOR,
    ADMIN,
    GUEST
}

enum class ReportPriority {
    LOW,
    MEDIUM,
    HIGH,
    URGENT
}