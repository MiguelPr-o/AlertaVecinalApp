package mx.edu.utng.alertavecinal.data.local

import androidx.room.TypeConverter
import mx.edu.utng.alertavecinal.data.model.ReportStatus
import mx.edu.utng.alertavecinal.data.model.ReportType
import mx.edu.utng.alertavecinal.data.model.UserRole

// Clase Converters: Sirve como un convertidor de tipos para la base de datos
// Room. Transforma tipos de datos complejos y personalizados de la aplicación
// (como enumeraciones y listas) en formatos simples que SQLite puede almacenar
// (cadenas de texto) y viceversa, permitiendo que Room persista estos objetos
// especiales directamente en la base de datos.

class Converters {

    @TypeConverter
    fun fromReportType(type: ReportType): String {
        return type.name
    }

    @TypeConverter
    fun toReportType(name: String): ReportType {
        return ReportType.valueOf(name)
    }

    @TypeConverter
    fun fromReportStatus(status: ReportStatus): String {
        return status.name
    }

    @TypeConverter
    fun toReportStatus(name: String): ReportStatus {
        return ReportStatus.valueOf(name)
    }

    @TypeConverter
    fun fromUserRole(role: UserRole): String {
        return role.name
    }

    @TypeConverter
    fun toUserRole(name: String): UserRole {
        return UserRole.valueOf(name)
    }

    @TypeConverter
    fun fromStringList(list: List<String>): String {
        return list.joinToString(",")
    }

    @TypeConverter
    fun toStringList(data: String): List<String> {
        return if (data.isEmpty()) emptyList() else data.split(",")
    }
}