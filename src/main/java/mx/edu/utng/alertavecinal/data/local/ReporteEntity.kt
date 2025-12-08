// ReportEntity.kt
package mx.edu.utng.alertavecinal.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import mx.edu.utng.alertavecinal.data.model.ReportStatus
import mx.edu.utng.alertavecinal.data.model.ReportType

@Entity(tableName = "reports")
data class ReportEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val userName: String,
    val title: String,
    val description: String,
    val reportType: ReportType,
    val status: ReportStatus,
    val latitude: Double,
    val longitude: Double,
    val address: String?,
    val imageUrl: String?,
    val createdAt: Long,
    val updatedAt: Long,
    val approvedBy: String?,
    val rejectionReason: String?,
    // ✅ NUEVOS CAMPOS PARA MODERADOR
    val editedBy: String? = null,
    val lastEditAt: Long? = null,
    val moderatorComment: String? = null,
    val isSynced: Boolean = false
)

// ✅ SOLO funciones para ReportEntity
fun ReportEntity.toDomainModel(): mx.edu.utng.alertavecinal.data.model.Report {
    return mx.edu.utng.alertavecinal.data.model.Report(
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
        rejectionReason = this.rejectionReason
    )
}

fun ReportEntity.updateWithModerationData(
    title: String? = null,
    description: String? = null,
    reportType: ReportType? = null,
    address: String? = null,
    editedBy: String? = null,
    moderatorComment: String? = null,
    status: ReportStatus? = null,
    approvedBy: String? = null,
    rejectionReason: String? = null
): ReportEntity {
    return this.copy(
        title = title ?: this.title,
        description = description ?: this.description,
        reportType = reportType ?: this.reportType,
        address = address ?: this.address,
        editedBy = editedBy ?: this.editedBy,
        moderatorComment = moderatorComment ?: this.moderatorComment,
        status = status ?: this.status,
        approvedBy = approvedBy ?: this.approvedBy,
        rejectionReason = rejectionReason ?: this.rejectionReason,
        lastEditAt = if (editedBy != null) System.currentTimeMillis() else this.lastEditAt,
        updatedAt = System.currentTimeMillis()
    )
}