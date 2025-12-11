package mx.edu.utng.alertavecinal.data.local

/*
Clase ReportDao: Es una interfaz DAO que define todas las operaciones
de base de datos local relacionadas con los reportes o alertas. Actúa
como caché para los reportes de la aplicación, permitiendo almacenarlos
localmente para acceso rápido, funcionalidad offline y sincronización
con Firebase, además de proporcionar filtros por estado, tipo y búsquedas avanzadas.
*/

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import mx.edu.utng.alertavecinal.data.model.ReportStatus
import mx.edu.utng.alertavecinal.data.model.ReportType

@Dao
interface ReportDao {

    @Query("SELECT * FROM reports ORDER BY createdAt DESC")
    fun getAllReports(): Flow<List<ReportEntity>>

    @Query("SELECT * FROM reports WHERE userId = :userId ORDER BY createdAt DESC")
    fun getUserReports(userId: String): Flow<List<ReportEntity>>

    @Query("SELECT * FROM reports WHERE status = :status ORDER BY createdAt DESC")
    fun getReportsByStatus(status: ReportStatus): Flow<List<ReportEntity>>

    @Query("SELECT * FROM reports WHERE status = 'PENDING' ORDER BY createdAt DESC")
    fun getPendingReports(): Flow<List<ReportEntity>>

    @Query("SELECT * FROM reports WHERE status = 'APPROVED' ORDER BY createdAt DESC")
    fun getApprovedReports(): Flow<List<ReportEntity>>

    @Query("SELECT * FROM reports WHERE status = 'REJECTED' ORDER BY createdAt DESC")
    fun getRejectedReports(): Flow<List<ReportEntity>>

    @Query("SELECT * FROM reports WHERE id = :reportId")
    fun getReport(reportId: String): Flow<ReportEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: ReportEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllReports(reports: List<ReportEntity>)

    @Update
    suspend fun updateReport(report: ReportEntity)

    @Query("DELETE FROM reports WHERE id = :reportId")
    suspend fun deleteReport(reportId: String)

    @Query("DELETE FROM reports")
    suspend fun deleteAllReports()

    @Query("SELECT * FROM reports WHERE reportType = :type AND status = 'APPROVED' ORDER BY createdAt DESC")
    fun getReportsByType(type: ReportType): Flow<List<ReportEntity>>

    @Query("SELECT * FROM reports WHERE isSynced = 0")
    suspend fun getUnsyncedReports(): List<ReportEntity>

    @Query("UPDATE reports SET isSynced = 1 WHERE id = :reportId")
    suspend fun markReportAsSynced(reportId: String)

    @Query("UPDATE reports SET status = :status, approvedBy = :approvedBy, updatedAt = :updatedAt WHERE id = :reportId")
    suspend fun updateReportStatus(reportId: String, status: ReportStatus, approvedBy: String?, updatedAt: Long)

    suspend fun updateReportInfo(
        reportId: String,
        title: String?,
        description: String?,
        reportType: ReportType?,
        address: String?,
        editedBy: String?,
        moderatorComment: String?,
        updatedAt: Long
    ) {
        val currentReport = getReport(reportId)
    }

    suspend fun updateReportWithModeration(
        reportId: String,
        title: String? = null,
        description: String? = null,
        reportType: ReportType? = null,
        address: String? = null,
        editedBy: String? = null,
        moderatorComment: String? = null
    ): Boolean {
        return try {
            // Obtener el reporte actual
            val currentReportFlow = getReport(reportId)
            var currentReport: ReportEntity? = null

            true
        } catch (e: Exception) {
            false
        }
    }


    // Obtener estadísticas rápidas
    @Query("SELECT COUNT(*) FROM reports WHERE status = 'PENDING'")
    suspend fun getPendingCount(): Int

    @Query("SELECT COUNT(*) FROM reports WHERE status = 'APPROVED'")
    suspend fun getApprovedCount(): Int

    @Query("SELECT COUNT(*) FROM reports WHERE status = 'REJECTED'")
    suspend fun getRejectedCount(): Int

    @Query("SELECT COUNT(*) FROM reports")
    suspend fun getTotalCount(): Int

    // Obtener reportes urgentes
    @Query("""
        SELECT * FROM reports 
        WHERE reportType IN ('ROBBERY', 'FIRE', 'ACCIDENT', 'FIGHT')
        AND status = 'PENDING'
        ORDER BY createdAt ASC
    """)
    fun getUrgentReports(): Flow<List<ReportEntity>>

    // Buscar reportes por texto
    @Query("""
        SELECT * FROM reports 
        WHERE title LIKE '%' || :searchQuery || '%' 
        OR description LIKE '%' || :searchQuery || '%'
        ORDER BY createdAt DESC
    """)
    fun searchReports(searchQuery: String): Flow<List<ReportEntity>>
}