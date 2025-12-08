// Enums.kt (VERSIÓN ACTUALIZADA)
package mx.edu.utng.alertavecinal.data.model

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

// ✅ NUEVO: Enums para acciones de moderación
enum class ModerationAction {
    APPROVE,        // Aprobar reporte
    REJECT,         // Rechazar reporte
    EDIT,           // Editar reporte
    REQUEST_INFO,   // Solicitar más información
    DELETE          // Eliminar reporte (solo admin)
}

// ✅ NUEVO: Enums para filtros de moderador
enum class ModeratorFilter {
    ALL,            // Todos los reportes
    PENDING,        // Solo pendientes
    APPROVED,       // Solo aprobados
    REJECTED,       // Solo rechazados
    TODAY,          // Reportes de hoy
    URGENT          // Reportes urgentes (robos, incendios, etc.)
}

// ✅ NUEVO: Enums para estadísticas de moderador
enum class StatType {
    PENDING_COUNT,
    APPROVED_COUNT,
    REJECTED_COUNT,
    TOTAL_REPORTS,
    RESPONSE_TIME,
    APPROVAL_RATE
}

// ✅ NUEVO: Enums para tipos de usuario en el sistema
enum class UserType {
    REGULAR_USER,   // Usuario regular que reporta incidentes
    MODERATOR,      // Usuario que modera reportes
    ADMIN,          // Administrador del sistema
    GUEST           // Usuario no registrado
}

// ✅ NUEVO: Enums para prioridad de reportes
enum class ReportPriority {
    LOW,        // Ruido, mascota perdida, etc.
    MEDIUM,     // Vandalismo, persona sospechosa
    HIGH,       // Robo, pelea
    URGENT      // Incendio, accidente grave
}