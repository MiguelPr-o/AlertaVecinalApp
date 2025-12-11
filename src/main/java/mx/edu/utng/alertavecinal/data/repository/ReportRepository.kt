package mx.edu.utng.alertavecinal.data.repository

/*
Clase ReportRepository: Esta clase es el repositorio principal que maneja
toda la lógica de reportes e incidentes en la aplicación. Se encarga de
sincronizar datos entre Firebase Firestore (la base de datos en la nube),
Firebase Storage (para imágenes) y la base de datos local Room, proporcionando
funciones para crear, modificar, aprobar, rechazar y buscar reportes, así
como para gestionar el historial de moderación.
*/

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import mx.edu.utng.alertavecinal.data.local.AppDatabase
import mx.edu.utng.alertavecinal.data.local.ReportEntity
import mx.edu.utng.alertavecinal.data.local.toDomainModel
import mx.edu.utng.alertavecinal.data.local.updateWithModerationData
import mx.edu.utng.alertavecinal.data.model.Report
import mx.edu.utng.alertavecinal.data.model.ReportStatus
import mx.edu.utng.alertavecinal.data.model.ReportType
import mx.edu.utng.alertavecinal.data.model.toEntityModel
import java.util.UUID
import javax.inject.Inject

class ReportRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val database: AppDatabase
) {

    private fun convertReportToEntity(report: Report): ReportEntity {
        return ReportEntity(
            id = report.id,
            userId = report.userId,
            userName = report.userName,
            title = report.title,
            description = report.description,
            reportType = report.reportType,
            status = report.status,
            latitude = report.latitude,
            longitude = report.longitude,
            address = report.address,
            imageUrl = report.imageUrl,
            createdAt = report.createdAt,
            updatedAt = report.updatedAt,
            approvedBy = report.approvedBy,
            rejectionReason = report.rejectionReason,
            editedBy = null,
            lastEditAt = null,
            moderatorComment = null,
            isSynced = false
        )
    }

    suspend fun createReport(report: Report): Result<String> {
        return try {
            val reportId = report.id.ifEmpty { UUID.randomUUID().toString() }
            val reportWithId = report.copy(id = reportId)

            firestore.collection("reports").document(reportId)
                .set(reportWithId)
                .await()

            database.reportDao().insertReport(convertReportToEntity(reportWithId))
            Result.success(reportId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadReportImage(imageBytes: ByteArray): Result<String> {
        return try {
            val imageName = "reports/${UUID.randomUUID()}.jpg"
            val storageRef = storage.reference.child(imageName)

            val uploadTask = storageRef.putBytes(imageBytes).await()
            val downloadUrl = storageRef.downloadUrl.await()

            Result.success(downloadUrl.toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getApprovedReports(): Flow<List<Report>> {
        return database.reportDao().getApprovedReports().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    fun getReportsByType(reportType: ReportType): Flow<List<Report>> {
        return database.reportDao().getReportsByType(reportType).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    fun getUserReports(userId: String): Flow<List<Report>> {
        return database.reportDao().getUserReports(userId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    fun getPendingReports(): Flow<List<Report>> {
        return database.reportDao().getPendingReports().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    fun getRejectedReports(): Flow<List<Report>> {
        return database.reportDao().getReportsByStatus(ReportStatus.REJECTED).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    suspend fun updateReportStatus(
        reportId: String,
        status: ReportStatus,
        approvedBy: String? = null,
        rejectionReason: String? = null
    ): Result<Boolean> {
        return try {
            val updateData = mapOf(
                "status" to status.name,
                "approvedBy" to approvedBy,
                "rejectionReason" to rejectionReason,
                "updatedAt" to System.currentTimeMillis()
            )

            firestore.collection("reports").document(reportId)
                .update(updateData)
                .await()

            database.reportDao().updateReportStatus(
                reportId,
                status,
                approvedBy,
                System.currentTimeMillis()
            )

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateReportStatusWithComment(
        reportId: String,
        status: ReportStatus,
        moderatorId: String,
        moderatorName: String,
        comment: String? = null,
        rejectionReason: String? = null
    ): Result<Boolean> {
        println("🎯 REPOSITORY - updateReportStatusWithComment")
        println("   reportId: $reportId")
        println("   status: $status")
        println("   moderatorId: $moderatorId")
        return try {
            val approvedBy = "$moderatorName ($moderatorId)"

            val updateData = hashMapOf<String, Any>(
                "status" to status.name,
                "approvedBy" to approvedBy,
                "updatedAt" to System.currentTimeMillis()
            )

            comment?.let { updateData["moderatorComment"] = it }
            rejectionReason?.let { updateData["rejectionReason"] = it }

            if (status == ReportStatus.APPROVED) {
                updateData["approvedAt"] = System.currentTimeMillis()
            }

            firestore.collection("reports").document(reportId)
                .update(updateData)
                .await()

            val currentEntity = database.reportDao().getReport(reportId).first()
            currentEntity?.let { entity ->
                val updatedEntity = entity.updateWithModerationData(
                    status = status,
                    approvedBy = approvedBy,
                    rejectionReason = rejectionReason,
                    moderatorComment = comment
                )
                database.reportDao().updateReport(updatedEntity)
            }

            createModerationHistory(
                reportId = reportId,
                moderatorId = moderatorId,
                moderatorName = moderatorName,
                action = when (status) {
                    ReportStatus.APPROVED -> "APPROVE"
                    ReportStatus.REJECTED -> "REJECT"
                    else -> "UPDATE"
                },
                comment = comment ?: rejectionReason
            )

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun editReport(
        reportId: String,
        title: String? = null,
        description: String? = null,
        reportType: ReportType? = null,
        address: String? = null,
        moderatorId: String,
        moderatorName: String
    ): Result<Boolean> {
        return try {
            val currentEntity = database.reportDao().getReport(reportId).first()

            if (currentEntity == null) {
                return Result.failure(Exception("Reporte no encontrado"))
            }

            val updatedEntity = currentEntity.updateWithModerationData(
                title = title,
                description = description,
                reportType = reportType,
                address = address,
                editedBy = "$moderatorName ($moderatorId)",
                moderatorComment = "Editado por moderador"
            )

            database.reportDao().updateReport(updatedEntity)

            val updateData = hashMapOf<String, Any>()

            if (title != null) updateData["title"] = title
            if (description != null) updateData["description"] = description
            if (reportType != null) updateData["reportType"] = reportType.name
            if (address != null) updateData["address"] = address

            updateData["updatedAt"] = System.currentTimeMillis()
            updateData["editedBy"] = "$moderatorName ($moderatorId)"
            updateData["lastEditAt"] = System.currentTimeMillis()

            firestore.collection("reports").document(reportId)
                .update(updateData)
                .await()

            createModerationHistory(
                reportId = reportId,
                moderatorId = moderatorId,
                moderatorName = moderatorName,
                action = "EDIT",
                comment = "Reporte editado por moderador",
                changes = updateData
            )

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun syncReports() {
        try {
            val snapshot = firestore.collection("reports")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            val reports = snapshot.toObjects(Report::class.java)
            val entities = reports.map { convertReportToEntity(it) }
            database.reportDao().insertAllReports(entities)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getReportById(reportId: String): Report? {
        Log.d("ReportRepository", "🔍 Buscando reporte: $reportId")

        return try {
            // 1. Buscar en Room (base de datos local) - CORREGIDO
            Log.d("ReportRepository", "📱 Buscando en Room...")

            // ✅ CORREGIDO: Usa first() en lugar de collect
            val entity = database.reportDao().getReport(reportId).first()
            var report: Report? = entity?.toDomainModel()

            if (report != null) {
                Log.d("ReportRepository", "✅ Encontrado en Room: ${report.title}")
                return report
            }

            Log.d("ReportRepository", "📡 No en Room, buscando en Firestore...")

            // 2. Buscar en Firestore
            val document = firestore.collection("reports").document(reportId).get().await()

            if (document.exists()) {
                Log.d("ReportRepository", "✅ Documento existe en Firestore")
                report = document.toObject(Report::class.java)

                // 3. Guardar en Room para futuras consultas
                report?.let {
                    Log.d("ReportRepository", "📝 Guardando en Room...")
                    database.reportDao().insertReport(it.toEntityModel())
                }

                return report
            } else {
                Log.d("ReportRepository", "❌ Documento NO existe en Firestore")
                return null
            }
        } catch (e: Exception) {
            Log.e("ReportRepository", "💥 Error en getReportById", e)
            null
        }
    }

    suspend fun deleteReport(reportId: String): Result<Boolean> {
        return try {
            firestore.collection("reports").document(reportId).delete().await()
            database.reportDao().deleteReport(reportId)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getAllReports(): Flow<List<Report>> {
        return database.reportDao().getAllReports().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    fun getReportsByStatus(status: ReportStatus): Flow<List<Report>> {
        return database.reportDao().getReportsByStatus(status).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    suspend fun requestMoreInfo(
        reportId: String,
        moderatorId: String,
        moderatorName: String,
        message: String
    ): Result<Boolean> {
        return try {
            // 1. Obtener el usuario que creó el reporte
            val report = getReportById(reportId)
            val userId = report?.userId ?: ""

            // 2. Crear notificación
            val notificationId = UUID.randomUUID().toString()
            val notificationData = hashMapOf<String, Any>(
                "id" to notificationId,
                "reportId" to reportId,
                "userId" to userId,
                "moderatorId" to moderatorId,
                "moderatorName" to moderatorName,
                "type" to "INFO_REQUESTED",
                "message" to message,
                "timestamp" to System.currentTimeMillis(),
                "read" to false
            )

            firestore.collection("notifications").document(notificationId)
                .set(notificationData)
                .await()

            // 3. Crear historial de moderación
            createModerationHistory(
                reportId = reportId,
                moderatorId = moderatorId,
                moderatorName = moderatorName,
                action = "REQUEST_INFO",
                comment = message
            )

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // FUNCIÓN PARA OBTENER ESTADÍSTICAS
    suspend fun getModerationStats(moderatorId: String? = null): Map<String, Any> {
        return try {
            val query = if (moderatorId != null) {
                firestore.collection("reports")
                    .whereEqualTo("approvedBy", moderatorId)
            } else {
                firestore.collection("reports")
            }

            val snapshot = query.get().await()
            val reports = snapshot.toObjects(Report::class.java)

            val pendingCount = reports.count { it.status == ReportStatus.PENDING }
            val approvedCount = reports.count { it.status == ReportStatus.APPROVED }
            val rejectedCount = reports.count { it.status == ReportStatus.REJECTED }
            val totalCount = reports.size

            mapOf(
                "pendingCount" to pendingCount,
                "approvedCount" to approvedCount,
                "rejectedCount" to rejectedCount,
                "totalCount" to totalCount,
                "approvalRate" to if (totalCount > 0) (approvedCount.toFloat() / totalCount * 100).toInt() else 0
            )
        } catch (e: Exception) {
            emptyMap()
        }
    }

    // FUNCIÓN PRIVADA PARA CREAR HISTORIAL DE MODERACIÓN
    private suspend fun createModerationHistory(
        reportId: String,
        moderatorId: String,
        moderatorName: String,
        action: String,
        comment: String? = null,
        changes: Map<String, Any>? = null
    ) {
        try {
            val historyId = UUID.randomUUID().toString()
            val historyData = hashMapOf<String, Any>(
                "id" to historyId,
                "reportId" to reportId,
                "moderatorId" to moderatorId,
                "moderatorName" to moderatorName,
                "action" to action,
                "timestamp" to System.currentTimeMillis()
            )

            comment?.let { historyData["comment"] = it }
            changes?.let { historyData["changes"] = it }

            firestore.collection("moderation_history").document(historyId)
                .set(historyData)
                .await()
        } catch (e: Exception) {
            // Silenciar error, no es crítico para la operación principal
        }
    }

    fun getUrgentReports(): Flow<List<Report>> {
        // Si tienes esta función en DAO, úsala. Si no, filtra localmente.
        return database.reportDao().getPendingReports().map { entities ->
            entities.filter { entity ->
                entity.reportType in listOf(
                    ReportType.ROBBERY,
                    ReportType.FIRE,
                    ReportType.ACCIDENT,
                    ReportType.FIGHT
                )
            }.map { it.toDomainModel() }
        }
    }

    fun searchReports(searchQuery: String): Flow<List<Report>> {
        return database.reportDao().getAllReports().map { entities ->
            entities.filter { entity ->
                entity.title.contains(searchQuery, ignoreCase = true) ||
                        entity.description.contains(searchQuery, ignoreCase = true) ||
                        entity.userName.contains(searchQuery, ignoreCase = true)
            }.map { it.toDomainModel() }
        }
    }
}