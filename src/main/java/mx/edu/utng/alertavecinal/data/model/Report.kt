package mx.edu.utng.alertavecinal.data.model

data class Report(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val title: String = "",
    val description: String = "",
    val reportType: ReportType = ReportType.OTHER,
    val status: ReportStatus = ReportStatus.PENDING,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val address: String? = null,
    val imageUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val approvedBy: String? = null, // ID del moderador que aprobó
    val rejectionReason: String? = null // Razón si fue rechazado
) {
    // Función auxiliar para obtener ícono según tipo
    fun getIconResource(): String {
        return when (reportType) {
            ReportType.ROBBERY -> "🔫"
            ReportType.FIRE -> "🔥"
            ReportType.ACCIDENT -> "🚗"
            ReportType.SUSPICIOUS_PERSON -> "👤"
            ReportType.FIGHT -> "👊"
            ReportType.VANDALISM -> "💢"
            ReportType.NOISE -> "📢"
            ReportType.LOST_PET -> "🐕"
            ReportType.OTHER -> "⚠️"
        }
    }

    // Función para obtener color según estado
    fun getStatusColor(): String {
        return when (status) {
            ReportStatus.PENDING -> "#FFA500" // Naranja
            ReportStatus.APPROVED -> "#008000" // Verde
            ReportStatus.REJECTED -> "#FF0000" // Rojo
        }
    }
}

// ✅ AGREGAR ESTO AL FINAL DEL ARCHIVO (fuera de la data class)
fun Report.toEntityModel(): mx.edu.utng.alertavecinal.data.local.ReportEntity {
    return mx.edu.utng.alertavecinal.data.local.ReportEntity(
        id = this.id,
        userId = this.userId,
        userName = this.userName,
        title = this.title,
        description = this.description,
        reportType = this.reportType,
        status = this.status,
        latitude = this.latitude,
        longitude = this.longitude,
        address = this.address,
        imageUrl = this.imageUrl,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        approvedBy = this.approvedBy,
        rejectionReason = this.rejectionReason,
        editedBy = null,
        lastEditAt = null,
        moderatorComment = null,
        isSynced = false
    )
}