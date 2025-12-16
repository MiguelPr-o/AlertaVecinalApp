# 📱 Alerta Vecinal
## Documentación de Arquitectura del Proyecto

Aplicación móvil para el **reporte y visualización de incidentes en tiempo real**, con enfoque comunitario, moderación.

---

## Configuración Inicial del Proyecto
Explicación: Antes de construir una casa necesitas los materiales correctos. En Android Studio, necesitamos configurar nuestro proyecto con las "herramientas" adecuadas.

## Configuración del archivo build.gradle.kts (Module:app)
```
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
    id("kotlin-parcelize")
}

android {
    namespace = "mx.edu.utng.alertavecinal"
    compileSdk = 34

    defaultConfig {
        applicationId = "mx.edu.utng.alertavecinal"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        // Corrección de sintaxis deprecada
        testInstrumentationRunnerArguments["runnerBuilder"] = "de.mannodermaus.junit5.AndroidJUnitPlatformBuilder"

        vectorDrawables {
            useSupportLibrary = true
        }

        // =========================================================
        // 🚀 INYECCIÓN DE CLAVE DE API DE GOOGLE MAPS
        // =========================================================
        // Ahora usamos Properties gracias a la importación en la línea 2
        val propertiesFile = project.rootProject.file("local.properties")

        // Carga de propiedades (solo si el archivo existe)
        if (propertiesFile.exists()) {
            val properties = Properties() // 'Properties' ahora está disponible
            propertiesFile.inputStream().use { properties.load(it) }

            val mapsApiKey = properties.getProperty("MAPS_API_KEY") ?: "API_KEY_NOT_FOUND"

            // Inyecta la clave en el Manifest para Google Maps
            manifestPlaceholders["mapsApiKey"] = mapsApiKey

            // Inyecta la clave en BuildConfig
            buildConfigField("String", "MAPS_API_KEY", "\"$mapsApiKey\"")
        } else {
            // Valores de fallback
            manifestPlaceholders["mapsApiKey"] = "API_KEY_NOT_FOUND"
            buildConfigField("String", "MAPS_API_KEY", "\"API_KEY_NOT_FOUND\"")
        }
        // =========================================================
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/gradle/incremental.annotation.processors"
        }
    }
}

dependencies {
    // Android Core
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    // Compose
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.6")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.48.1")
    kapt("com.google.dagger:hilt-compiler:2.48.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.2"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")

    // Coil para imágenes
    implementation("io.coil-kt:coil-compose:2.5.0")

    // Google Maps
    implementation("com.google.maps.android:maps-compose:2.14.0")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // System UI Controller
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.32.0")

    // Permissions
    implementation("com.google.accompanist:accompanist-permissions:0.32.0")

    // Gson
    implementation("com.google.code.gson:gson:2.10.1")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    // Test
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

kapt {
    correctErrorTypes = true
}
```

## Configuración del archivo AndroidManifest
```
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

    <uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
    <uses-permission android:name="android.permission.READ_MEDIA_VIDEO" />

    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
    <uses-permission android:name="android.permission.VIBRATE" />

    <application
        android:name=".AlertaVecinalApp"
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.AlertaVecinal"
        android:enableOnBackInvokedCallback="true"
        tools:targetApi="31">

        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:label="@string/app_name"
            android:theme="@style/Theme.AlertaVecinal">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <meta-data
            android:name="com.google.android.geo.API_KEY"
            android:value="${mapsApiKey}" />

    </application>

</manifest>
```

## 🗄️ CAPA DE DATOS LOCAL (ROOM DATABASE) - 8 Clases
### Paso 1.1: AppDatabase - Base de datos principal
Analogía: Es como la bóveda principal del banco. Todas las demás tablas (cajas de seguridad) están contenidas aquí.

```xml
package mx.edu.utng.alertavecinal.data.local

// CLASE PRINCIPAL DE BASE DE DATOS DE LA APLICACIÓN
// Esta clase define y gestiona la base de datos local de la app "Alerta Vecinal"
// Utiliza Room Persistence Library para almacenar datos de usuarios, reportes y notificaciones

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        UserEntity::class,
        ReportEntity::class,
        NotificationEntity::class
    ],
    version = 2,
    exportSchema = false
)   
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun reportDao(): ReportDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "alerta_vecinal_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
```

### Paso 1.2: Converters - Convertidores de tipos
Estos convertidores son como traductores que transforman tipos de datos complejos (como enums o listas) en un "idioma" que SQLite entienda (String o Int).

```
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
```

### Paso 1.3: NotificationEntity - Entidad de notificaciones
Analogía: Cada notificación es como una carta registrada que llega al buzón, con remitente, destinatario, fecha y contenido.


```
package mx.edu.utng.alertavecinal.data.local

/*
Clase NotificationEntity: Esta clase define la estructura de una
notificación que se almacena en la base de datos local del dispositivo.
Representa la tabla "notifications" en SQLite y contiene todos los datos
necesarios para mostrar y gestionar notificaciones dentro de la aplicación,
incluyendo su tipo, estado de lectura y metadatos asociados.
 */

import androidx.room.Entity
import androidx.room.PrimaryKey
import mx.edu.utng.alertavecinal.data.model.NotificationType

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val title: String,
    val message: String,
    val type: NotificationType,
    val reportId: String?,
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val data: String? = null
)
```

### Paso 1.4: NotificationDao - Operaciones con notificaciones
Analogía: Es como el sistema de clasificación del correo: ordena por fecha, busca por tipo, marca como leído.

```
package mx.edu.utng.alertavecinal.data.local

/*
Clase NotificationDao: Es una interfaz DAO (Data Access Object) que define
todas las operaciones de base de datos relacionadas con las notificaciones.
Proporciona métodos para insertar, actualizar, eliminar y consultar
notificaciones, permitiendo gestionar el estado de lectura y obtener conteos
de notificaciones no leídas para cada usuario.
 */


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {

    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY createdAt DESC")
    fun getUserNotifications(userId: String): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notifications WHERE userId = :userId AND isRead = 0 ORDER BY createdAt DESC")
    fun getUnreadNotifications(userId: String): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Update
    suspend fun updateNotification(notification: NotificationEntity)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :notificationId")
    suspend fun markAsRead(notificationId: String)

    @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId")
    suspend fun markAllAsRead(userId: String)

    @Query("DELETE FROM notifications WHERE id = :notificationId")
    suspend fun deleteNotification(notificationId: String)

    @Query("DELETE FROM notifications WHERE userId = :userId")
    suspend fun deleteUserNotifications(userId: String)

    @Query("SELECT COUNT(*) FROM notifications WHERE userId = :userId AND isRead = 0")
    fun getUnreadCount(userId: String): Flow<Int>
}
```

### Paso 1.5: ReportEntity - Entidad de reportes
Explicación detallada: Esta tabla almacena todos los reportes de incidentes, similar a un expediente policial con fotos, ubicación y estado.

```
package mx.edu.utng.alertavecinal.data.local

/*
Clase ReportEntity: Esta clase define la estructura de un reporte o
alerta que se almacena localmente en la base de datos del dispositivo.
Representa la tabla "reports" en SQLite y contiene todos los datos de
un incidente reportado, incluyendo su ubicación, tipo, estado, imágenes,
y metadatos de moderación para cuando los administradores revisen o editen
los reportes.
 */

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
```

### Paso 1.6: ReportDao - Operaciones con reportes
Analogía: Como un archivista experto que puede buscar expedientes por múltiples criterios: fecha, tipo, estado, etc.

```
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
```

### Paso 1.7: UserEntity - Entidad de usuarios
Explicación detallada: El perfil del usuario almacenado localmente para acceso rápido sin conexión.

```
package mx.edu.utng.alertavecinal.data.local

/*
Clase UserEntity: Esta clase define la estructura de un usuario
que se almacena en la base de datos local del dispositivo. Representa
la tabla "users" en SQLite y contiene todos los datos del perfil del
usuario, incluyendo información personal, preferencias de notificación
y rol dentro de la aplicación.
*/

import androidx.room.Entity
import androidx.room.PrimaryKey
import mx.edu.utng.alertavecinal.data.model.UserRole

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val email: String,
    val role: UserRole,
    val address: String?,
    val latitude: Double?,
    val longitude: Double?,
    val phone: String?,
    val createdAt: Long,
    val notificationRadius: Int,
    val notificationsEnabled: Boolean,
    val lastSync: Long = System.currentTimeMillis()
) {
    fun toDomain(): mx.edu.utng.alertavecinal.data.model.User {
        return mx.edu.utng.alertavecinal.data.model.User(
            id = id,
            name = name,
            email = email,
            role = role,
            address = address,
            latitude = latitude,
            longitude = longitude,
            phone = phone,
            createdAt = createdAt,
            notificationRadius = notificationRadius,
            notificationsEnabled = notificationsEnabled
        )
    }
}

// Función de extensión para convertir de dominio a entidad
fun mx.edu.utng.alertavecinal.data.model.User.toEntity(): UserEntity {
    return UserEntity(
        id = id,
        name = name,
        email = email,
        role = role,
        address = address,
        latitude = latitude,
        longitude = longitude,
        phone = phone,
        createdAt = createdAt,
        notificationRadius = notificationRadius,
        notificationsEnabled = notificationsEnabled
    )
}
```

### Paso 1.8: UserDao - Operaciones con usuarios
Analogía: El carné de identidad digital del usuario, siempre accesible incluso sin conexión a internet.

```
package mx.edu.utng.alertavecinal.data.local

/*
Clase UserDao: Es una interfaz DAO que define las operaciones de
base de datos local relacionadas con los usuarios. Maneja el almacenamiento
y recuperación de información de usuario en el caché local, permitiendo
acceso rápido a datos del perfil y configuración sin necesidad de consultar
constantemente Firebase.
*/

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUser(userId: String): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUser(userId: String)

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("UPDATE users SET notificationRadius = :radius WHERE id = :userId")
    suspend fun updateNotificationRadius(userId: String, radius: Int)

    @Query("UPDATE users SET notificationsEnabled = :enabled WHERE id = :userId")
    suspend fun updateNotificationsEnabled(userId: String, enabled: Boolean)

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<UserEntity>
}
```

## 📦 CAPA DE MODELOS (DOMAIN LAYER) - 15 Clases
### Paso 2.1: Enums.kt - Todos los enumerados
Explicación detallada: Archivo centralizado que define todos los tipos constantes, como un diccionario de términos oficiales de la aplicación.

```
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
```

## Paso 2.2: Report - Modelo de dominio
Analogía: La "tarjeta de incidente" completa que circula por toda la app, con métodos inteligentes que saben cómo presentarse.

```
package mx.edu.utng.alertavecinal.data.model

/*
Clase Report: Esta clase representa un reporte o alerta de incidente
en el dominio de la aplicación. Contiene todos los datos de un
reporte como su tipo, ubicación, descripción y estado de moderación,
y proporciona métodos para obtener íconos y colores según el tipo
y estado para mostrar en la interfaz de usuario.
*/

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
```

### Paso 2.3: LocationData - Datos de ubicación
Explicación detallada: Representa una ubicación geográfica con métodos de utilidad para cálculos.

```
package mx.edu.utng.alertavecinal.data.model

/*
Clase LocationData: Esta clase representa una ubicación geográfica
dentro de la aplicación, almacenando coordenadas de latitud y longitud
junto con una dirección opcional. Proporciona métodos para convertir
entre formatos de cadena de texto y objetos, facilitando el manejo
de ubicaciones en toda la aplicación.
 */

data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val address: String? = null,
    val timestamp: Long = System.currentTimeMillis()
) {
    companion object {
        fun fromString(locationString: String): LocationData? {
            return try {
                val parts = locationString.split(",")
                if (parts.size == 2) {
                    LocationData(
                        latitude = parts[0].toDouble(),
                        longitude = parts[1].toDouble()
                    )
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    fun toStringFormat(): String {
        return "$latitude,$longitude"
    }
}
```

### Paso 2.4: User - Modelo de dominio de usuario
Analogía: El perfil completo del usuario que incluye tanto datos personales como preferencias.

```
package mx.edu.utng.alertavecinal.data.model

/*
Clase User: Esta clase representa a un usuario del sistema en el
dominio de la aplicación. Contiene todos los datos del perfil del
usuario incluyendo información personal, ubicación, preferencias
de notificación y rol dentro de la aplicación.
*/

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val role: UserRole = UserRole.USER,
    val address: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val phone: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val notificationRadius: Int = 1000, // Radio en metros (1km por defecto)
    val notificationsEnabled: Boolean = true
)
```

### Paso 2.5: AuthState - Estado de autenticación
Explicación detallada: Clase sellada que representa todos los posibles estados del flujo de autenticación.

```
/*
Clase AuthState: Esta clase representa el estado de autenticación
del usuario, almacenando información sobre si el usuario está
autenticado, los datos del usuario actual, y el estado de carga o
error durante operaciones de autenticación.
*/

data class AuthState(
    val isAuthenticated: Boolean = false,
    val currentUser: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
```

### Paso 2.6: ReportState - Estado de reportes
Analogía: Como el tablero de control de un editor de periódico, muestra qué reportes hay, cuáles están filtrados, y si hay errores.

```
package mx.edu.utng.alertavecinal.data.model

/*
Clase ReportState: Esta clase representa el estado de los reportes en
la aplicación, almacenando la lista completa de reportes,
los reportes filtrados según criterios específicos, el reporte seleccionado,
y el estado de carga o error. Sirve como contenedor de datos para
gestionar y actualizar la interfaz de reportes de manera reactiva.
*/

data class ReportState(
    val reports: List<Report> = emptyList(),
    val filteredReports: List<Report> = emptyList(),
    val selectedReport: Report? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val filterType: ReportType? = null,
    val filterStatus: ReportStatus? = null
)
```

### Paso 2.7: UiState - Estados genéricos de UI
Explicación detallada: Patrón reutilizable para manejar estados de carga, éxito y error en cualquier pantalla.

```
/*
Clase UiState: Esta es una clase sellada que representa los posibles
estados de la interfaz de usuario para operaciones asíncronas:
Cargando, Éxito (con datos) y Error (con mensaje). Proporciona una
forma tipo-segura de manejar estados de carga en toda la aplicación.
*/

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
```

### Paso 2.8: MapState - Estado del mapa
Analogía: Como el panel de control de un piloto de drones: muestra ubicación, reportes visibles, y estado de conexión.

```
package mx.edu.utng.alertavecinal.data.model

import com.google.android.gms.maps.model.LatLng

/*
Clase MapState: Esta clase representa el estado del mapa en la aplicación,
almacenando la ubicación actual del usuario, los reportes visibles en
el mapa, el reporte seleccionado, y el estado de carga o error. Sirve
como contenedor de datos para gestionar y actualizar la interfaz del
mapa de manera reactiva.
 */


data class MapState(
    val currentLocation: LatLng? = null,
    val reports: List<Report> = emptyList(),
    val selectedReport: Report? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val filterType: String? = null,
    val isLocationEnabled: Boolean = false
)
```

### Paso 2.9: NotificationPrefs - Preferencias de notificación
Explicación detallada: Configuración personalizable de qué notificaciones quiere recibir el usuario y cuándo.

```
package mx.edu.utng.alertavecinal.data.model

/*
Clase NotificationPrefs: Esta clase representa las preferencias
de notificación del usuario, almacenando configuraciones como el
radio de alerta, tipos de notificaciones habilitados, y horarios
silenciosos. Permite personalizar cómo y cuándo el usuario
recibe alertas sobre incidentes cercanos en la aplicación.
 */

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
```

}

## 🔄 CAPA DE REPOSITORIOS - 4 Clases
### Paso 3.1: ReportRepository - Repositorio de reportes
Analogía: El centro de operaciones que coordina entre Firebase (nube), Room (local) y Storage (imágenes).

```
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
```

### Paso 3.2: AuthRepository - Repositorio de autenticación
Explicación detallada: Gestiona todo el ciclo de vida del usuario: registro, login, logout y recuperación.

```
package mx.edu.utng.alertavecinal.data.repository

/*
Clase AuthRepository: Esta clase es el repositorio central que
maneja todas las operaciones de autenticación y gestión de usuarios
de la aplicación. Se encarga de comunicarse con Firebase Authentication
para el login y registro, con Firestore para almacenar y recuperar
datos de usuario, y con la base de datos local para caché, sincronizando
los tres sistemas para mantener la consistencia de datos del usuario.
*/

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import mx.edu.utng.alertavecinal.data.local.AppDatabase
import mx.edu.utng.alertavecinal.data.local.toEntity
import mx.edu.utng.alertavecinal.data.model.User
import mx.edu.utng.alertavecinal.data.model.UserRole
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val database: AppDatabase
) {

    suspend fun login(email: String, password: String): Result<User> {
        return try {
            println("🔍 DEBUG AuthRepository - Iniciando login para: $email")
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user

            if (firebaseUser != null) {
                println("🔍 DEBUG AuthRepository - Usuario autenticado en Firebase Auth: ${firebaseUser.uid}")
                val user = getUserFromFirestore(firebaseUser.uid)

                // Si el usuario no existe en Firestore, FALLAR con mensaje claro
                if (user == null) {
                    println("🔴 DEBUG AuthRepository - ERROR: Usuario autenticado pero no existe en Firestore")
                    println("🔴 DEBUG AuthRepository - UserId: ${firebaseUser.uid}, Email: $email")

                    // Cerrar sesión para limpiar estado inconsistente
                    auth.signOut()

                    return Result.failure(Exception(
                        "Tu cuenta fue autenticada pero no tiene perfil completo. " +
                                "Esto puede pasar si la cuenta fue creada directamente en Firebase Console. " +
                                "Por favor, regístrate nuevamente o contacta al administrador."
                    ))
                }

                // Usuario existe - guardar localmente
                println("🟢 DEBUG AuthRepository - Login exitoso: ${user.name} (${user.email})")
                database.userDao().insertUser(user.toEntity())

                Result.success(user)
            } else {
                println("🔴 DEBUG AuthRepository - Error: firebaseUser es null")
                Result.failure(Exception("Error en autenticación - usuario no encontrado"))
            }
        } catch (e: Exception) {
            println("🔴 DEBUG AuthRepository - Error en login: ${e.message}")
            Result.failure(Exception("Error al iniciar sesión: ${e.message}"))
        }
    }

    suspend fun register(
        name: String,
        email: String,
        password: String,
        address: String? = null,
        latitude: Double? = null,
        longitude: Double? = null
    ): Result<User> {
        return try {
            println("🔍 DEBUG AuthRepository - Iniciando registro para: $email")

            // Crear usuario en Firebase Auth
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user

            if (firebaseUser != null) {
                println("🔍 DEBUG AuthRepository - Usuario creado en Firebase Auth: ${firebaseUser.uid}")

                // Crear usuario en Firestore
                val user = User(
                    id = firebaseUser.uid,
                    name = name,
                    email = email,
                    role = UserRole.USER,
                    address = address,
                    latitude = latitude,
                    longitude = longitude
                )

                // Guardar en Firestore con manejo de errores
                try {
                    firestore.collection("users").document(firebaseUser.uid)
                        .set(user)
                        .await()

                    // Guardar localmente
                    database.userDao().insertUser(user.toEntity())

                    println("🟢 DEBUG AuthRepository - Registro exitoso: ${user.name} (${user.email})")
                    Result.success(user)
                } catch (firestoreError: Exception) {
                    println("🔴 DEBUG AuthRepository - Error al guardar en Firestore: ${firestoreError.message}")

                    // Si falla Firestore, eliminar el usuario de Auth para consistencia
                    try {
                        auth.currentUser?.delete()?.await()
                        println("🔴 DEBUG AuthRepository - Usuario eliminado de Auth por fallo en Firestore")
                    } catch (deleteError: Exception) {
                        println("🔴 DEBUG AuthRepository - Error al eliminar usuario de Auth: ${deleteError.message}")
                    }

                    Result.failure(Exception("Error al guardar datos del usuario: ${firestoreError.message}"))
                }
            } else {
                println("🔴 DEBUG AuthRepository - Error: firebaseUser es null en registro")
                Result.failure(Exception("Error al crear usuario en authentication"))
            }
        } catch (e: Exception) {
            println("🔴 DEBUG AuthRepository - Error en registro: ${e.message}")
            Result.failure(Exception("Error al registrar usuario: ${e.message}"))
        }
    }

    suspend fun logout() {
        println("🔍 DEBUG AuthRepository - Cerrando sesión")
        auth.signOut()
        // Limpiar datos locales si es necesario
        database.reportDao().deleteAllReports()
        println("🟢 DEBUG AuthRepository - Sesión cerrada")
    }

    fun getCurrentUser(): FirebaseUser? {
        val user = auth.currentUser
        println("🔍 DEBUG AuthRepository - getCurrentUser: ${user?.uid ?: "null"}")
        return user
    }

    suspend fun getUserFromFirestore(userId: String): User? {
        return try {
            println("🔍 AuthRepository - Buscando usuario en Firestore: $userId")
            val document = firestore.collection("users").document(userId).get().await()

            if (document.exists()) {
                val data = document.data
                println("📄 Datos Firestore: $data")

                // Construir usuario manualmente para manejar el rol
                val user = User(
                    id = document.getString("id") ?: userId,
                    name = document.getString("name") ?: "Usuario",
                    email = document.getString("email") ?: "",
                    role = document.getString("role")?.let {
                        UserRole.fromString(it)
                    } ?: UserRole.USER,
                    address = document.getString("address"),
                    latitude = document.getDouble("latitude"),
                    longitude = document.getDouble("longitude"),
                    createdAt = document.getLong("createdAt") ?: System.currentTimeMillis()
                )

                println("🟢 Usuario encontrado: ${user.name}, Rol: ${user.role.getDisplayName()}")
                user
            } else {
                println("🔴 Usuario NO encontrado en Firestore: $userId")
                null
            }
        } catch (e: Exception) {
            println("🔴 Error al buscar usuario: ${e.message}")
            null
        }
    }

    suspend fun updateUserProfile(user: User): Result<Boolean> {
        return try {
            println("🔍 DEBUG AuthRepository - Actualizando perfil: ${user.name}")
            firestore.collection("users").document(user.id)
                .set(user)
                .await()

            // Actualizar localmente
            database.userDao().updateUser(user.toEntity())

            println("🟢 DEBUG AuthRepository - Perfil actualizado: ${user.name}")
            Result.success(true)
        } catch (e: Exception) {
            println("🔴 DEBUG AuthRepository - Error al actualizar perfil: ${e.message}")
            Result.failure(e)
        }
    }

    fun getCurrentUserFlow(userId: String): Flow<User?> {
        println("🔍 DEBUG AuthRepository - getCurrentUserFlow: $userId")
        return database.userDao().getUser(userId).map { entity ->
            entity?.toDomain()
        }
    }

    suspend fun resetPassword(email: String): Result<Boolean> {
        return try {
            println("🔍 DEBUG AuthRepository - Solicitando reset de password: $email")
            auth.sendPasswordResetEmail(email).await()
            println("🟢 DEBUG AuthRepository - Email de reset enviado: $email")
            Result.success(true)
        } catch (e: Exception) {
            println("🔴 DEBUG AuthRepository - Error al resetear password: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun createUserIfNotExists(userId: String, email: String, name: String? = null): Result<User> {
        return try {
            println("🔍 DEBUG AuthRepository - createUserIfNotExists: $userId")
            var user = getUserFromFirestore(userId)

            if (user == null) {
                user = User(
                    id = userId,
                    name = name ?: "Usuario",
                    email = email,
                    role = UserRole.USER
                )

                firestore.collection("users").document(userId)
                    .set(user)
                    .await()

                // Guardar localmente
                database.userDao().insertUser(user.toEntity())

                println("🟢 DEBUG AuthRepository - Usuario creado: ${user.name}")
            } else {
                println("🔍 DEBUG AuthRepository - Usuario ya existe: ${user.name}")
            }

            Result.success(user)
        } catch (e: Exception) {
            println("🔴 DEBUG AuthRepository - Error en createUserIfNotExists: ${e.message}")
            Result.failure(e)
        }
    }
}
```

### Paso 3.3: MapRepository - Repositorio de mapas/ubicación
Analogía: Como un navegador GPS que obtiene ubicación, calcula rutas y gestiona permisos.

```
package mx.edu.utng.alertavecinal.data.repository

/*
Clase MapRepository: Esta clase es el repositorio encargado de toda
la lógica relacionada con ubicación y mapas en la aplicación. Gestiona
la obtención de la ubicación actual del dispositivo usando los servicios
de Google Play, verifica permisos, calcula distancias entre puntos y
proporciona funciones para validar y formatear ubicaciones para mostrar al usuario.
*/

import android.Manifest
import android.content.Context
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await
import mx.edu.utng.alertavecinal.data.model.LocationData
import mx.edu.utng.alertavecinal.utils.PermissionUtils
import javax.inject.Inject

class MapRepository @Inject constructor(
    private val context: Context
) {

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    suspend fun getCurrentLocation(): LocationData? {
        return try {
            if (!hasLocationPermission()) {
                return null
            }

            if (!PermissionUtils.hasLocationPermissions(context)) {
                throw SecurityException("Los permisos de ubicación no fueron concedidos")
            }

            getLocationWithTimeout()

        } catch (securityEx: SecurityException) {
            securityEx.printStackTrace()
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private suspend fun getLocationWithTimeout(): LocationData? {
        return try {
            // checkSelfPermission ANTES de llamar a lastLocation
            val fineLocationPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            val coarseLocationPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )

            // Verificar si al menos un permiso fue concedido
            val hasPermission = fineLocationPermission == android.content.pm.PackageManager.PERMISSION_GRANTED ||
                    coarseLocationPermission == android.content.pm.PackageManager.PERMISSION_GRANTED

            if (!hasPermission) {
                throw SecurityException("Permisos de ubicación requeridos no concedidos")
            }

            // Ahora sí obtener la ubicación
            val location = fusedLocationClient.lastLocation.await()
            location?.toLocationData()

        } catch (securityEx: SecurityException) {
            // Manejo explícito de SecurityException
            securityEx.printStackTrace()
            null
        } catch (e: Exception) {
            // Si lastLocation falla, retornar null
            e.printStackTrace()
            null
        }
    }

    private fun Location.toLocationData(): LocationData {
        return LocationData(
            latitude = this.latitude,
            longitude = this.longitude,
            // Podrías agregar aquí lógica para obtener la dirección si quieres
            address = null, // Por defecto null, puedes obtenerlo después
            timestamp = System.currentTimeMillis()
        )
    }

    private fun hasLocationPermission(): Boolean {
        return PermissionUtils.hasLocationPermissions(context)
    }

    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]
    }

    // Obtener dirección desde coordenadas
    suspend fun getAddressFromLocation(latitude: Double, longitude: Double): String? {
        return try {
            // Implementación simplificada - puedes integrar Geocoder aquí
            // Por ahora retornamos las coordenadas formateadas
            "Ubicación seleccionada: ${String.format("%.6f", latitude)}, ${String.format("%.6f", longitude)}"
        } catch (e: Exception) {
            e.printStackTrace()
            "Ubicación: ${String.format("%.6f", latitude)}, ${String.format("%.6f", longitude)}"
        }
    }

    // Verificar permisos de ubicación de forma segura
    fun checkLocationPermissions(): Boolean {
        return try {
            PermissionUtils.hasLocationPermissions(context)
        } catch (e: SecurityException) {
            false
        }
    }

    // Obtener ubicación con callback para manejar falta de permisos
    suspend fun getCurrentLocationWithCallback(
        onPermissionDenied: () -> Unit = {},
        onLocationUnavailable: () -> Unit = {}
    ): LocationData? {
        return try {
            if (!hasLocationPermission()) {
                onPermissionDenied()
                return null
            }

            val location = getLocationWithTimeout()
            if (location == null) {
                onLocationUnavailable()
            }
            location

        } catch (securityEx: SecurityException) {
            onPermissionDenied()
            null
        } catch (e: Exception) {
            onLocationUnavailable()
            null
        }
    }

    // Obtener ubicación con dirección
    suspend fun getCurrentLocationWithAddress(): LocationData? {
        return try {
            val location = getCurrentLocation()
            location?.let { loc ->
                val address = getAddressFromLocation(loc.latitude, loc.longitude)
                loc.copy(address = address)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Calcular distancia entre dos LocationData
    fun calculateDistanceBetween(location1: LocationData, location2: LocationData): Float {
        return calculateDistance(
            location1.latitude,
            location1.longitude,
            location2.latitude,
            location2.longitude
        )
    }

    // Formatear distancia para mostrar al usuario
    fun formatDistance(distanceInMeters: Float): String {
        return when {
            distanceInMeters < 1000 -> "${String.format("%.0f", distanceInMeters)} m"
            else -> "${String.format("%.1f", distanceInMeters / 1000)} km"
        }
    }

    // Verificar si una ubicación es válida
    fun isValidLocation(latitude: Double, longitude: Double): Boolean {
        return latitude != 0.0 && longitude != 0.0 &&
                latitude >= -90 && latitude <= 90 &&
                longitude >= -180 && longitude <= 180
    }

    // Verificar si un LocationData es válido
    fun isValidLocationData(locationData: LocationData?): Boolean {
        return locationData != null && isValidLocation(locationData.latitude, locationData.longitude)
    }
}
```

### Paso 3.4: UserRepository - Repositorio de usuarios
Explicación detallada: Gestiona todas las operaciones del perfil de usuario y sincronización de preferencias.

```
package mx.edu.utng.alertavecinal.data.repository

/*
Clase UserRepository: Esta clase es el repositorio encargado de gestionar
todas las operaciones relacionadas con usuarios en la aplicación. Maneja
la sincronización de datos de usuario entre Firebase Firestore (base de
datos en la nube) y la base de datos local Room, incluyendo actualización
de ubicación, preferencias de notificación, perfil de usuario y obtención
de información de múltiples usuarios.
*/

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import mx.edu.utng.alertavecinal.data.local.AppDatabase
import mx.edu.utng.alertavecinal.data.local.toEntity
import mx.edu.utng.alertavecinal.data.model.User
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val database: AppDatabase
) {

    fun getCurrentUser(userId: String): Flow<User?> {
        return database.userDao().getUser(userId).map { entity ->
            entity?.toDomain()
        }
    }

    suspend fun updateUserLocation(
        userId: String,
        latitude: Double,
        longitude: Double,
        address: String? = null
    ): Result<Boolean> {
        return try {
            // Actualizar en Firestore
            val updateData = mapOf(
                "latitude" to latitude,
                "longitude" to longitude,
                "address" to address
            )

            firestore.collection("users").document(userId)
                .update(updateData)
                .await()

            // Actualizar localmente
            val userEntity = database.userDao().getUser(userId)
            userEntity.collect { entity ->
                entity?.let {
                    val updatedEntity = it.copy(
                        latitude = latitude,
                        longitude = longitude,
                        address = address
                    )
                    database.userDao().updateUser(updatedEntity)
                }
            }

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateNotificationSettings(
        userId: String,
        radius: Int,
        enabled: Boolean
    ): Result<Boolean> {
        return try {
            val updateData = mapOf(
                "notificationRadius" to radius,
                "notificationsEnabled" to enabled
            )

            firestore.collection("users").document(userId)
                .update(updateData)
                .await()

            // Actualizar localmente
            database.userDao().updateNotificationRadius(userId, radius)
            database.userDao().updateNotificationsEnabled(userId, enabled)

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserById(userId: String): User? {
        return try {
            val document = firestore.collection("users").document(userId).get().await()
            if (document.exists()) {
                document.toObject(User::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateUserProfile(userId: String, name: String, phone: String?): Result<Boolean> {
        return try {
            val updateData = mutableMapOf<String, Any>(
                "name" to name
            )

            phone?.let {
                updateData["phone"] = it
            }

            firestore.collection("users").document(userId)
                .update(updateData)
                .await()

            // Actualizar localmente
            val userEntity = database.userDao().getUser(userId)
            userEntity.collect { entity ->
                entity?.let {
                    val updatedEntity = it.copy(
                        name = name,
                        phone = phone
                    )
                    database.userDao().updateUser(updatedEntity)
                }
            }

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllUsers(): List<User> {
        return try {
            val snapshot = firestore.collection("users").get().await()
            snapshot.toObjects(User::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun createOrUpdateUser(user: User): Result<Boolean> {
        return try {
            firestore.collection("users").document(user.id)
                .set(user)
                .await()

            // Guardar localmente
            database.userDao().insertUser(user.toEntity())

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

## 💉 CAPA DE INYECCIÓN DE DEPENDENCIAS - 1 Clase
### Paso 4.1: AppModule - Módulo principal Dagger Hilt
Analogía: Como el director de casting de una película: decide qué actor (instancia) interpreta cada papel (dependencia).

```
package mx.edu.utng.alertavecinal.di

/*
Clase AppModule (Módulo Dagger Hilt): Este es el módulo principal
de inyección de dependencias de la aplicación que define cómo se
crean y proporcionan todas las instancias necesarias en toda la
app. Utiliza Dagger Hilt para gestionar la inyección de
dependencias de forma automática, incluyendo servicios de Firebase,
la base de datos Room, y todos los repositorios principales.
*/

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import mx.edu.utng.alertavecinal.data.local.AppDatabase
import mx.edu.utng.alertavecinal.data.repository.AuthRepository
import mx.edu.utng.alertavecinal.data.repository.MapRepository
import mx.edu.utng.alertavecinal.data.repository.ReportRepository
import mx.edu.utng.alertavecinal.data.repository.UserRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    // Firestore
    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    // Firebase Storage
    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }

    // Room Database
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    // FusedLocationProviderClient
    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(@ApplicationContext context: Context): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

    // Repositories

    @Provides
    @Singleton
    fun provideAuthRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore,
        database: AppDatabase
    ): AuthRepository {
        return AuthRepository(auth, firestore, database)
    }

    @Provides
    @Singleton
    fun provideReportRepository(
        firestore: FirebaseFirestore,
        storage: FirebaseStorage,
        database: AppDatabase
    ): ReportRepository {
        return ReportRepository(firestore, storage, database)
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        firestore: FirebaseFirestore,
        database: AppDatabase
    ): UserRepository {
        return UserRepository(firestore, database)
    }

    @Provides
    @Singleton
    fun provideMapRepository(
        @ApplicationContext context: Context
    ): MapRepository {
        return MapRepository(context)
    }
}
```

## 🎨 CAPA DE COMPONENTES UI (COMPOSE) - 9 Clases
### Paso 5.1: CustomButtons - Botones personalizados
Analogía: Como un set de botones de control de una consola de mezcla, cada uno con función y apariencia específica.

```
package mx.edu.utng.alertavecinal.ui.components

/*
Clase CustomButtons: Este archivo contiene un conjunto de componentes
de botones personalizados y reutilizables para la interfaz de usuario
de la aplicación. Incluye botones con diferentes estilos (rellenos, con borde),
soporte para íconos, estados de carga y personalización completa de colores
y tamaños, proporcionando una experiencia de UI consistente y moderna en toda la app.
*/

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = MaterialTheme.colorScheme.onPrimary,
    height: Dp = 48.dp,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null
) {
    Button(
        onClick = {
            if (!isLoading) {
                onClick()
            }
        },
        modifier = modifier.height(height),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            disabledContainerColor = backgroundColor.copy(alpha = 0.5f)
        ),
        enabled = enabled && !isLoading,
        contentPadding = PaddingValues(horizontal = 10.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = textColor,
                strokeWidth = 2.dp
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                leadingIcon?.let { icon ->
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = textColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Text(
                    text = text,
                    color = textColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                trailingIcon?.let { icon ->
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = textColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CustomOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    borderColor: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = MaterialTheme.colorScheme.primary,
    isLoading: Boolean = false,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null
) {
    Button(
        onClick = {
            if (!isLoading) {
                onClick()
            }
        },
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = textColor
        ),
        contentPadding = PaddingValues(horizontal = 10.dp),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            width = 2.dp
        ),
        enabled = !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                color = textColor,
                strokeWidth = 2.dp
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ✅ NUEVO: Mostrar ícono inicial si está presente
                leadingIcon?.let { icon ->
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = textColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Text(
                    text = text,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                trailingIcon?.let { icon ->
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = textColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CustomButtonWithLoading(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = MaterialTheme.colorScheme.onPrimary,
    height: Dp = 56.dp,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    loadingText: String = "Cargando...",
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null
) {
    Button(
        onClick = {
            if (!isLoading) {
                onClick()
            }
        },
        modifier = modifier.height(height),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            disabledContainerColor = backgroundColor.copy(alpha = 0.5f)
        ),
        enabled = enabled && !isLoading,
        contentPadding = PaddingValues(horizontal = 24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = textColor,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = loadingText,
                    color = textColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            } else {
                leadingIcon?.let { icon ->
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = textColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Text(
                    text = text,
                    color = textColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                trailingIcon?.let { icon ->
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = textColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CustomIconButton(
    text: String,
    onClick: () -> Unit,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = MaterialTheme.colorScheme.onPrimary,
    height: Dp = 48.dp,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    iconPosition: IconPosition = IconPosition.Start // ✅ Posición del ícono
) {
    Button(
        onClick = {
            if (!isLoading) {
                onClick()
            }
        },
        modifier = modifier.height(height),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            disabledContainerColor = backgroundColor.copy(alpha = 0.5f)
        ),
        enabled = enabled && !isLoading,
        contentPadding = PaddingValues(horizontal = 24.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = textColor,
                strokeWidth = 2.dp
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                when (iconPosition) {
                    IconPosition.Start -> {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = textColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = text,
                            color = textColor,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    IconPosition.End -> {
                        Text(
                            text = text,
                            color = textColor,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = textColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

enum class IconPosition {
    Start, End
}
```

### Paso 5.2: CustomTextField - Campos de texto
Explicación detallada: Campos de entrada con validación integrada y diferentes modos (normal, contraseña, búsqueda).

```
package mx.edu.utng.alertavecinal.ui.components

/*
Clase CustomTextField: Este componente proporciona un campo de texto
personalizado y reutilizable para la interfaz de usuario de la aplicación.
Incluye soporte para íconos, campos de contraseña con visibilidad toggle,
manejo de errores y múltiples opciones de personalización, ofreciendo
una experiencia de entrada de datos consistente y accesible en toda la app.
*/

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    isPassword: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    error: String? = null,
    isError: Boolean = false
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    // Determinar si hay error (puede venir del parámetro isError o del mensaje de error)
    val hasError = isError || error != null

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier,
        leadingIcon = leadingIcon?.let {
            {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = if (hasError) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        },
        trailingIcon = {
            if (isPassword) {
                val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                val description = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = image,
                        contentDescription = description,
                        tint = if (hasError) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurface
                    )
                }
            } else {
                trailingIcon?.let {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = if (hasError) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        },
        keyboardOptions = if (isPassword) {
            keyboardOptions.copy(keyboardType = KeyboardType.Password)
        } else {
            keyboardOptions
        },
        visualTransformation = if (isPassword && !passwordVisible) {
            PasswordVisualTransformation()
        } else {
            visualTransformation
        },
        isError = hasError,
        supportingText = error?.let {
            { Text(text = it, color = MaterialTheme.colorScheme.error) }
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = if (hasError) MaterialTheme.colorScheme.error
            else MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = if (hasError) MaterialTheme.colorScheme.error
            else MaterialTheme.colorScheme.outline,
            errorBorderColor = MaterialTheme.colorScheme.error,
            focusedLabelColor = if (hasError) MaterialTheme.colorScheme.error
            else MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = if (hasError) MaterialTheme.colorScheme.error
            else MaterialTheme.colorScheme.onSurface,
            //textColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = MaterialTheme.shapes.medium
    )
}
```

### Paso 5.3: IncidentMarker - Marcadores de mapa
Analogía: Como las chinchetas de colores en un mapa físico, cada color representa un tipo diferente de incidente.

```
package mx.edu.utng.alertavecinal.ui.components

/*
Clase IncidentMarker: Este componente crea un marcador personalizado en
el mapa para representar un reporte o incidente. Asigna diferentes colores
a los marcadores según el tipo de reporte (robo, incendio, accidente, etc.)
y proporciona interacción mediante clics para mostrar detalles del incidente
en el mapa de la aplicación.
*/

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import mx.edu.utng.alertavecinal.data.model.Report
import mx.edu.utng.alertavecinal.data.model.ReportType

@Composable
fun IncidentMarker(
    report: Report,
    onClick: () -> Unit
) {
    val markerColor = when (report.reportType) {
        ReportType.ROBBERY -> BitmapDescriptorFactory.HUE_RED
        ReportType.FIRE -> BitmapDescriptorFactory.HUE_ORANGE
        ReportType.ACCIDENT -> BitmapDescriptorFactory.HUE_YELLOW
        ReportType.SUSPICIOUS_PERSON -> BitmapDescriptorFactory.HUE_VIOLET
        ReportType.FIGHT -> BitmapDescriptorFactory.HUE_ROSE
        ReportType.VANDALISM -> BitmapDescriptorFactory.HUE_MAGENTA
        ReportType.NOISE -> BitmapDescriptorFactory.HUE_BLUE
        ReportType.LOST_PET -> BitmapDescriptorFactory.HUE_CYAN
        ReportType.OTHER -> BitmapDescriptorFactory.HUE_GREEN
    }

    Marker(
        state = MarkerState(
            position = LatLng(report.latitude, report.longitude)
        ),
        title = report.title,
        snippet = report.description,
        icon = BitmapDescriptorFactory.defaultMarker(markerColor),
        onInfoWindowClick = { onClick() }
    )
}
```

### Paso 5.4: ReportFilter - Filtros de reportes
Explicación detallada: Componente tipo "chips" que permite filtrar reportes por categoría de forma intuitiva.

```
package mx.edu.utng.alertavecinal.ui.components

/*
Clase ReportFilter: Este componente proporciona un filtro interactivo de
tipo "chip" para filtrar reportes por categoría en la aplicación. Muestra
una fila horizontal de opciones de filtro (todos los tipos de reporte
más categorías específicas) que los usuarios pueden seleccionar para ver
solo los reportes de cierto tipo.
*/

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mx.edu.utng.alertavecinal.data.model.ReportType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportFilter(
    selectedType: ReportType?,
    onTypeSelected: (ReportType?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                text = "Todos",
                selected = selectedType == null,
                onClick = { onTypeSelected(null) }
            )
        }

        items(ReportType.values()) { type ->
            FilterChip(
                text = type.name.replace("_", " "),
                selected = selectedType == type,
                onClick = { onTypeSelected(type) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    AssistChip(
        onClick = onClick,
        label = { Text(text) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            },
            labelColor = if (selected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        ),
        shape = RoundedCornerShape(16.dp)
    )
}
```

### Paso 5.5: LoadingIndicator - Indicadores de carga
Analogía: Como las luces intermitentes de "cargando" en diferentes dispositivos, cada una para un contexto específico.

```
package mx.edu.utng.alertavecinal.ui.components

/*
Clase LoadingIndicator: Este archivo contiene varios componentes
reutilizables para mostrar estados de carga en diferentes contextos
de la aplicación. Incluye indicadores de carga desde pequeños hasta
pantalla completa y un diálogo de carga modal, proporcionando una
experiencia visual consistente durante operaciones asíncronas en toda la app.
*/

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 4.dp
        )
    }
}

@Composable
fun FullScreenLoading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(64.dp),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 4.dp
        )
    }
}

@Composable
fun LoadingDialog(
    onDismissRequest: () -> Unit = {}
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(64.dp),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 4.dp
        )
    }
}
```

### Paso 5.6: ErrorMessage - Mensajes de error
Explicación detallada: Componente estandarizado para mostrar errores de forma amigable y consistente.

```
package mx.edu.utng.alertavecinal.ui.components

/*
Clase ErrorMessage: Este componente muestra un mensaje de error estilizado
dentro de un contenedor con fondo de color de error del tema de Material
Design. Proporciona una forma consistente y visualmente clara de mostrar
mensajes de error al usuario en toda la aplicación.
*/

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ErrorMessage(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.errorContainer,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(12.dp)
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.onErrorContainer,
            style = MaterialTheme.typography.bodySmall
        )
    }
}
```

### Paso 5.7: EmptyState - Estados vacíos
Analogía: Como las páginas "No hay resultados" en un catálogo, que guían al usuario sobre qué hacer a continuación.

```
package mx.edu.utng.alertavecinal.ui.components

/*
Clase EmptyState (Componentes de estados vacíos): Este archivo contiene componentes
reutilizables para mostrar estados vacíos o sin datos en diferentes partes de la
aplicación. Proporciona pantallas visualmente atractivas con íconos y mensajes
descriptivos que se muestran cuando no hay contenido disponible, mejorando la
experiencia del usuario durante situaciones de datos vacíos.
*/

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun EmptyState(
    icon: ImageVector = Icons.Default.Search,
    title: String,
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )

        Spacer(modifier = Modifier.size(16.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.size(8.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun NoReportsState(
    modifier: Modifier = Modifier
) {
    EmptyState(
        icon = Icons.Default.Report,
        title = "No hay reportes",
        message = "No se encontraron reportes en esta área. Sé el primero en reportar un incidente.",
        modifier = modifier
    )
}

@Composable
fun NoPendingReportsState(
    modifier: Modifier = Modifier
) {
    EmptyState(
        icon = Icons.Default.Report,
        title = "No hay reportes pendientes",
        message = "Todos los reportes han sido revisados. ¡Buen trabajo!",
        modifier = modifier
    )
}
```

### Paso 5.8: ModeratorReportCard - Tarjetas para moderador
Explicación detallada: Vista especializada para moderadores con información detallada y controles de acción.

```
package mx.edu.utng.alertavecinal.ui.components

/*
Clase ModeratorReportCard: Este componente muestra una tarjeta de reporte
especialmente diseñada para moderadores y administradores, con información
detallada del incidente incluyendo tipo, estado, descripción, usuario
reportero, ubicación y fecha. Proporciona visualización clara del estado
de moderación y permite la navegación a detalles del reporte mediante clic.
*/

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import mx.edu.utng.alertavecinal.data.model.Report
import mx.edu.utng.alertavecinal.data.model.ReportStatus
import mx.edu.utng.alertavecinal.data.model.ReportType
import mx.edu.utng.alertavecinal.utils.FormatUtils

@Composable
fun ModeratorReportCard(
    report: Report,
    onClick: () -> Unit,
    showStatus: Boolean = true,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = getReportTypeIcon(report.reportType),
                        contentDescription = "Tipo",
                        modifier = Modifier.size(16.dp),
                        tint = getReportTypeColor(report.reportType)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formatReportType(report.reportType),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = getReportTypeColor(report.reportType)
                    )
                }

                if (showStatus) {
                    ReportStatusBadge(status = report.status)
                }
            }

            Spacer(modifier = Modifier.size(8.dp))

            Text(
                text = report.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.size(4.dp))

            Text(
                text = report.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.size(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Usuario",
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = report.userName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Ubicación",
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = report.address ?: "Ubicación no disponible",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }

            // Información de fecha
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = "Fecha",
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = FormatUtils.formatRelativeTime(report.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.weight(1f))

                // Ícono de acción
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Ver detalles",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun ReportStatusBadge(status: ReportStatus) {
    val (backgroundColor, textColor, text) = when (status) {
        ReportStatus.PENDING -> Triple(Color(0xFFFFA000), Color(0xFF000000), "PENDIENTE")
        ReportStatus.APPROVED -> Triple(Color(0xFF4CAF50), Color(0xFFFFFFFF), "APROBADO")
        ReportStatus.REJECTED -> Triple(Color(0xFFF44336), Color(0xFFFFFFFF), "RECHAZADO")
    }

    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = textColor,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

fun getReportTypeIcon(reportType: ReportType): ImageVector {
    return when (reportType) {
        ReportType.ROBBERY -> Icons.Default.Warning
        ReportType.FIRE -> Icons.Default.Warning
        ReportType.ACCIDENT -> Icons.Default.Warning
        ReportType.SUSPICIOUS_PERSON -> Icons.Default.Warning
        ReportType.FIGHT -> Icons.Default.Warning
        ReportType.VANDALISM -> Icons.Default.Warning
        ReportType.NOISE -> Icons.Default.Warning
        ReportType.LOST_PET -> Icons.Default.Warning
        ReportType.OTHER -> Icons.Default.Warning
    }
}

@Composable
fun getReportTypeColor(reportType: ReportType): Color {
    return when (reportType) {
        ReportType.ROBBERY -> MaterialTheme.colorScheme.error
        ReportType.FIRE -> Color(0xFFFF5722)
        ReportType.ACCIDENT -> Color(0xFFFF9800)
        ReportType.SUSPICIOUS_PERSON -> Color(0xFF9C27B0)
        ReportType.FIGHT -> Color(0xFFF44336)
        ReportType.VANDALISM -> Color(0xFF795548)
        ReportType.NOISE -> Color(0xFF607D8B)
        ReportType.LOST_PET -> Color(0xFF2196F3)
        ReportType.OTHER -> MaterialTheme.colorScheme.secondary
    }
}

fun formatReportType(reportType: ReportType): String {
    return reportType.name
        .replace("_", " ")
        .lowercase()
        .replaceFirstChar { it.uppercase() }
}
```

### Paso 5.9: ReportActionsModal - Modales de acciones
Analogía: Como los paneles de control de un editor de video, con opciones específicas para cada acción de moderación.

```
package mx.edu.utng.alertavecinal.ui.components

/*
Clase ReportActionsModal: Este archivo contiene un conjunto de componentes
modales y diálogos diseñados específicamente para las acciones de
moderación de reportes. Incluye modales para aprobar, rechazar, solicitar
más información y editar reportes, proporcionando interfaces especializadas
para cada acción con campos de comentario y confirmación apropiados.
*/

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

enum class ModeratorAction {
    APPROVE, REJECT, REQUEST_INFO, EDIT
}

data class ActionInfo(
    val title: String,
    val message: String,
    val icon: ImageVector,
    val confirmText: String,
    val requiresComment: Boolean
)

@Composable
fun ReportActionsModal(
    action: ModeratorAction,
    currentComment: String = "",
    onActionConfirmed: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var comment by remember { mutableStateOf(currentComment) }

    val actionInfo = when (action) {
        ModeratorAction.APPROVE -> ActionInfo(
            title = "¿Aprobar este reporte?",
            message = "El reporte será visible para todos los usuarios en el mapa. Puedes agregar un comentario opcional:",
            icon = Icons.Default.CheckCircle,
            confirmText = "Aprobar",
            requiresComment = false
        )
        ModeratorAction.REJECT -> ActionInfo(
            title = "¿Rechazar este reporte?",
            message = "Por favor, especifica el motivo del rechazo. Esto será visible para el usuario:",
            icon = Icons.Default.Close,
            confirmText = "Rechazar",
            requiresComment = true
        )
        ModeratorAction.REQUEST_INFO -> ActionInfo(
            title = "Solicitar más información",
            message = "¿Qué información adicional necesitas del usuario?",
            icon = Icons.Default.Info,
            confirmText = "Enviar solicitud",
            requiresComment = true
        )
        ModeratorAction.EDIT -> ActionInfo(
            title = "Editar reporte",
            message = "Realiza los cambios necesarios. Se notificará al usuario sobre las modificaciones:",
            icon = Icons.Default.Edit,
            confirmText = "Guardar cambios",
            requiresComment = false
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = actionInfo.icon,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = actionInfo.title,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column {
                Text(
                    text = actionInfo.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (action == ModeratorAction.EDIT) {
                    // Campos para edición
                    OutlinedTextField(
                        value = comment,
                        onValueChange = { comment = it },
                        label = { Text("Título (opcional)") },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Dejar vacío para no cambiar") }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = comment,
                        onValueChange = { comment = it },
                        label = { Text("Descripción (opcional)") },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Dejar vacío para no cambiar") },
                        minLines = 3,
                        maxLines = 5
                    )
                } else if (actionInfo.requiresComment || action == ModeratorAction.APPROVE) {
                    // Campo de comentario
                    OutlinedTextField(
                        value = comment,
                        onValueChange = { comment = it },
                        label = {
                            Text(
                                if (action == ModeratorAction.APPROVE)
                                    "Comentario (opcional)"
                                else
                                    "Comentario"
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                when (action) {
                                    ModeratorAction.APPROVE -> "Ej: Verificado por el equipo de moderación"
                                    ModeratorAction.REJECT -> "Ej: Contenido inapropiado o información insuficiente"
                                    ModeratorAction.REQUEST_INFO -> "Ej: ¿Podrías proporcionar más detalles sobre lo sucedido?"
                                    else -> ""
                                }
                            )
                        },
                        minLines = 3,
                        maxLines = 5
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (!actionInfo.requiresComment || comment.isNotBlank()) {
                        onActionConfirmed(comment)
                    }
                },
                enabled = !actionInfo.requiresComment || comment.isNotBlank(),
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text(actionInfo.confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

// Versión simplificada para acciones rápidas
@Composable
fun QuickActionModal(
    title: String,
    message: String,
    confirmText: String,
    cancelText: String = "Cancelar",
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(message)
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text(cancelText)
            }
        }
    )
}

// Modal para confirmar eliminación
@Composable
fun ConfirmDeleteModal(
    title: String = "¿Eliminar reporte?",
    message: String = "Esta acción no se puede deshacer. El reporte será eliminado permanentemente.",
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
        },
        text = {
            Text(message)
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Eliminar")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text("Cancelar")
            }
        }
    )
}
```

## 🧭 CAPA DE NAVEGACIÓN - 1 Clase
### Paso 6.1: AppNavigation - Navegación principal
Analogía: Como el sistema de metro de una ciudad, define todas las rutas posibles, conexiones y restricciones de acceso.

```
package mx.edu.utng.alertavecinal.ui.navigation

/*
Clase Navigation: Esta clase define toda la estructura de navegación
de la aplicación utilizando Jetpack Navigation Compose. Gestiona las
rutas entre todas las pantallas, controla el acceso basado en autenticación
y roles de usuario (usuario regular vs moderador/administrador), y proporciona
funciones de extensión para navegar fácilmente entre pantallas desde cualquier
parte de la app.


*/

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.android.gms.maps.model.LatLng
import mx.edu.utng.alertavecinal.BuildConfig
import mx.edu.utng.alertavecinal.utils.Constants
import mx.edu.utng.alertavecinal.ui.screens.*
import mx.edu.utng.alertavecinal.viewmodel.AuthViewModel

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Constants.ROUTE_WELCOME
) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.authState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        authViewModel.checkCurrentUser()
    }

    // Determinar la pantalla de inicio basada en el rol del usuario
    val initialDestination = when (authState.currentUser?.role?.name) {
        Constants.ROLE_MODERATOR -> Constants.ROUTE_MODERATOR_DASHBOARD
        Constants.ROLE_ADMIN -> Constants.ROUTE_MODERATOR_DASHBOARD
        else -> startDestination
    }

    NavHost(
        navController = navController,
        startDestination = initialDestination
    ) {
        composable(Constants.ROUTE_WELCOME) {
            WelcomeScreen(navController = navController)
        }

        composable(Constants.ROUTE_LOGIN) {
            LoginScreen(navController = navController, viewModel = authViewModel)
        }

        composable(Constants.ROUTE_REGISTER) {
            RegisterScreen(navController = navController)
        }

        composable(Constants.ROUTE_MAP) {
            if (authState.isAuthenticated) {
                MapScreen(navController = navController)
            } else {
                LaunchedEffect(Unit) {
                    navController.navigate(Constants.ROUTE_LOGIN) {
                        popUpTo(Constants.ROUTE_WELCOME) { inclusive = true }
                    }
                }
            }
        }

        composable(Constants.ROUTE_CREATE_REPORT) {
            if (authState.isAuthenticated) {
                CreateReportScreen(navController = navController)
            } else {
                LaunchedEffect(Unit) {
                    navController.navigate(Constants.ROUTE_LOGIN)
                }
            }
        }

        composable(Constants.ROUTE_SELECT_LOCATION) {
            if (authState.isAuthenticated) {
                SelectLocationScreen(
                    navController = navController,
                    onLocationSelected = { latLng ->
                        navController.previousBackStackEntry?.savedStateHandle?.set(
                            "selected_location",
                            latLng
                        )
                        navController.popBackStack()
                    }
                )
            } else {
                LaunchedEffect(Unit) {
                    navController.navigate(Constants.ROUTE_LOGIN)
                }
            }
        }

        composable(Constants.ROUTE_PENDING_REPORTS) {
            if (authState.isAuthenticated) {
                val currentUser = authState.currentUser
                if (currentUser?.role?.name == Constants.ROLE_MODERATOR ||
                    currentUser?.role?.name == Constants.ROLE_ADMIN) {
                    PendingReportsScreen(navController = navController)
                } else {
                    LaunchedEffect(Unit) {
                        navController.popBackStack()
                    }
                }
            } else {
                LaunchedEffect(Unit) {
                    navController.navigate(Constants.ROUTE_LOGIN)
                }
            }
        }

        composable(Constants.ROUTE_PROFILE) {
            if (authState.isAuthenticated) {
                ProfileScreen(navController = navController)
            } else {
                LaunchedEffect(Unit) {
                    navController.navigate(Constants.ROUTE_LOGIN)
                }
            }
        }

        composable("${Constants.ROUTE_REPORT_DETAIL}/{${Constants.KEY_REPORT_ID}}") { backStackEntry ->
            if (authState.isAuthenticated) {
                val reportId = backStackEntry.arguments?.getString(Constants.KEY_REPORT_ID) ?: ""
                ReportDetailScreen(navController = navController, reportId = reportId)
            } else {
                LaunchedEffect(Unit) {
                    navController.navigate(Constants.ROUTE_LOGIN)
                }
            }
        }

        composable(Constants.ROUTE_MODERATOR_DASHBOARD) {
            if (BuildConfig.DEBUG) {
                ModeratorDashboardScreen(
                    navController = navController,
                    authViewModel = authViewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            } else {
                // En producción, verificar autenticación
                if (authState.isAuthenticated) {
                    val currentUser = authState.currentUser
                    if (currentUser?.role?.name == Constants.ROLE_MODERATOR ||
                        currentUser?.role?.name == Constants.ROLE_ADMIN) {
                        ModeratorDashboardScreen(
                            navController = navController,
                            authViewModel = authViewModel,
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    } else {
                        // Si no es moderador, redirigir al mapa
                        LaunchedEffect(Unit) {
                            navController.navigate(Constants.ROUTE_MAP) {
                                popUpTo(Constants.ROUTE_WELCOME) { inclusive = true }
                            }
                        }
                    }
                } else {
                    // Si no está autenticado, redirigir al login
                    LaunchedEffect(Unit) {
                        navController.navigate(Constants.ROUTE_LOGIN) {
                            popUpTo(Constants.ROUTE_WELCOME) { inclusive = true }
                        }
                    }
                }
            }
        }


        composable(
            "${Constants.ROUTE_MODERATOR_REVIEW}/{${Constants.KEY_REPORT_ID}}/{${Constants.KEY_MODERATOR_ID}}/{${Constants.KEY_MODERATOR_NAME}}",
            arguments = listOf(
                navArgument(Constants.KEY_REPORT_ID) { type = NavType.StringType },
                navArgument(Constants.KEY_MODERATOR_ID) { type = NavType.StringType },
                navArgument(Constants.KEY_MODERATOR_NAME) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val reportId = backStackEntry.arguments?.getString(Constants.KEY_REPORT_ID) ?: ""
            val moderatorId = backStackEntry.arguments?.getString(Constants.KEY_MODERATOR_ID) ?: ""
            val moderatorName = backStackEntry.arguments?.getString(Constants.KEY_MODERATOR_NAME) ?: ""

            ModeratorReportReviewScreen(
                reportId = reportId,
                moderatorId = moderatorId,
                moderatorName = moderatorName,
                onBack = { navController.popBackStack() },
                onReportUpdated = {
                    navController.popBackStack(Constants.ROUTE_MODERATOR_DASHBOARD, false)
                }
            )
        }
    }
}

// Función para navegar a selección de ubicación
fun NavHostController.navigateToSelectLocation() {
    navigate(Constants.ROUTE_SELECT_LOCATION) {
        launchSingleTop = true
    }
}


fun NavHostController.navigateToMap() {
    navigate(Constants.ROUTE_MAP) {
        popUpTo(Constants.ROUTE_WELCOME) { inclusive = true }
    }
}

fun NavHostController.navigateToLogin() {
    navigate(Constants.ROUTE_LOGIN) {
        launchSingleTop = true
    }
}

fun NavHostController.navigateToRegister() {
    navigate(Constants.ROUTE_REGISTER) {
        launchSingleTop = true
    }
}

fun NavHostController.navigateToCreateReport() {
    navigate(Constants.ROUTE_CREATE_REPORT) {
        launchSingleTop = true
    }
}

fun NavHostController.navigateToProfile() {
    navigate(Constants.ROUTE_PROFILE) {
        launchSingleTop = true
    }
}

fun NavHostController.navigateToPendingReports() {
    navigate(Constants.ROUTE_PENDING_REPORTS) {
        launchSingleTop = true
    }
}

fun NavHostController.navigateToReportDetail(reportId: String) {
    navigate("${Constants.ROUTE_REPORT_DETAIL}/$reportId") {
        launchSingleTop = true
    }
}

fun NavHostController.navigateToModeratorDashboard() {
    navigate(Constants.ROUTE_MODERATOR_DASHBOARD) {
        // Limpiar stack si venimos de login
        popUpTo(Constants.ROUTE_LOGIN) { inclusive = true }
        launchSingleTop = true
    }
}

fun NavHostController.navigateToModeratorReview(
    reportId: String,
    moderatorId: String,
    moderatorName: String
) {
    navigate("${Constants.ROUTE_MODERATOR_REVIEW}/$reportId/$moderatorId/$moderatorName") {
        launchSingleTop = true
    }
}

fun NavHostController.navigateBack() {
    popBackStack()
}

fun NavHostController.navigateToWelcomeAndClearStack() {
    navigate(Constants.ROUTE_WELCOME) {
        popUpTo(0) { inclusive = true }
    }
}

fun getStartDestinationByRole(userRole: String?): String {
    return when (userRole) {
        Constants.  ROLE_MODERATOR, Constants.ROLE_ADMIN -> Constants.ROUTE_MODERATOR_DASHBOARD
        else -> Constants.ROUTE_MAP
    }
}
```

## 📱 CAPA DE PANTALLAS (SCREENS) - 11 Clases
### Paso 7.1: WelcomeScreen - Pantalla de bienvenida
Analogía: Como la recepción de un hotel, da la bienvenida y presenta las opciones principales de acceso.

```
package mx.edu.utng.alertavecinal.ui.screens

/*
Clase WelcomeScreen: Esta es la pantalla inicial de la aplicación que da
la bienvenida a los usuarios y presenta las opciones principales de acceso:
iniciar sesión para usuarios existentes o registrarse para nuevos usuarios.
Sirve como punto de entrada a la aplicación y establece el tono visual y
de funcionalidad para el resto de la experiencia de usuario
*/

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import mx.edu.utng.alertavecinal.BuildConfig
import mx.edu.utng.alertavecinal.R
import mx.edu.utng.alertavecinal.ui.components.CustomButton
import mx.edu.utng.alertavecinal.utils.Constants
import mx.edu.utng.alertavecinal.viewmodel.AuthViewModel

@Composable
fun WelcomeScreen(
    navController: NavController
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Logo Alerta Vecinal",
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Alerta Vecinal",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Mantente informado y protege tu comunidad reportando incidentes en tu área",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(64.dp))

            CustomButton(
                text = "Iniciar Sesión",
                onClick = { navController.navigate("login") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            CustomButton(
                text = "Registrarse",
                onClick = { navController.navigate("register") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                backgroundColor = MaterialTheme.colorScheme.secondary
            )

        }
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    WelcomeScreen(navController = rememberNavController())
}
```

### Paso 7.2: LoginScreen - Inicio de sesión
Explicación detallada: Pantalla de autenticación con validación en tiempo real y manejo de diferentes escenarios.

```
package mx.edu.utng.alertavecinal.ui.screens

/*
Clase LoginScreen: Esta pantalla permite a los usuarios autenticarse
en la aplicación mediante correo electrónico y contraseña. Valida las
credenciales con Firebase Authentication, maneja diferentes roles de usuario
(regular, moderador, administrador) y redirige a la pantalla apropiada según
el rol después de un inicio de sesión exitoso.
*/

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import mx.edu.utng.alertavecinal.data.model.UserRole
import mx.edu.utng.alertavecinal.ui.components.CustomButton
import mx.edu.utng.alertavecinal.ui.components.CustomTextField
import mx.edu.utng.alertavecinal.ui.components.ErrorMessage
import mx.edu.utng.alertavecinal.utils.Constants
import mx.edu.utng.alertavecinal.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsState()
    val shouldNavigate by viewModel.shouldNavigate.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showDebugPanel by remember { mutableStateOf(false) }

    LaunchedEffect(shouldNavigate) {
        shouldNavigate?.let { destination ->
            println("🟢 LoginScreen - Redirigiendo a: $destination")

            when (destination) {
                Constants.ROUTE_MODERATOR_DASHBOARD -> {
                    navController.navigate(destination) {
                        popUpTo(Constants.ROUTE_LOGIN) { inclusive = true }
                    }
                }
                Constants.ROUTE_MAP -> {
                    navController.navigate(destination) {
                        popUpTo(Constants.ROUTE_WELCOME) { inclusive = true }
                    }
                }
            }

            // Limpiar el estado de navegación
            viewModel.clearNavigation()
        }
    }

    LaunchedEffect(authState.isAuthenticated) {
        if (authState.isAuthenticated && authState.currentUser != null && shouldNavigate == null) {
            println("⚠️ LoginScreen - Redirección alternativa activada")
            val userRole = authState.currentUser?.role

            val destination = when (userRole) {
                UserRole.MODERATOR, UserRole.ADMIN -> {
                    println("🎯 Usuario es moderador/admin")
                    Constants.ROUTE_MODERATOR_DASHBOARD
                }
                else -> {
                    println("🎯 Usuario es normal")
                    Constants.ROUTE_MAP
                }
            }

            println("🟢 Redirigiendo a: $destination")
            navController.navigate(destination) {
                popUpTo(Constants.ROUTE_WELCOME) { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Iniciar Sesión") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                actions = {

                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            MaterialTheme.shapes.medium
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Login",
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Bienvenido de nuevo",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Ingresa a tu cuenta",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(48.dp))

                CustomTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Correo electrónico",
                    leadingIcon = Icons.Default.Email,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                CustomTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Contraseña",
                    leadingIcon = Icons.Default.Lock,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                authState.error?.let { error ->
                    ErrorMessage(message = error)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (authState.isAuthenticated) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .background(
                                            Color.Green,
                                            MaterialTheme.shapes.small
                                        )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "✅ Autenticado",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            authState.currentUser?.let { user ->
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Usuario: ${user.name}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Email: ${user.email}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Rol: ",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = user.role.getDisplayName(),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = when (user.role) {
                                            UserRole.MODERATOR -> Color(0xFF4CAF50)
                                            UserRole.ADMIN -> Color(0xFF2196F3)
                                            else -> MaterialTheme.colorScheme.onSurface
                                        }
                                    )
                                }

                                Text(
                                    text = "Redirección: ${shouldNavigate ?: "Determinando..."}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }

                CustomButton(
                    text = if (authState.isLoading) "Iniciando sesión..." else "Iniciar Sesión",
                    onClick = {
                        println("🔍 LoginScreen - Iniciando login para: $email")
                        viewModel.login(email, password)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = email.isNotEmpty() && password.isNotEmpty() && !authState.isLoading,
                    isLoading = authState.isLoading
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "¿No tienes cuenta? ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "Regístrate",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .clickable {
                                navController.navigate(Constants.ROUTE_REGISTER)
                            }
                            .padding(horizontal = 4.dp)
                    )
                }

                if (authState.isLoading) {
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    MaterialTheme {
        LoginScreen(navController = rememberNavController())
    }
}
```

### Paso 7.3: MapScreen - Mapa principal
Analogía: Como el tablero de control de una central de emergencias, muestra todos los incidentes en tiempo real.

```
package mx.edu.utng.alertavecinal.ui.screens

/*
Clase MapScreen: Esta es la pantalla principal de la aplicación que muestra
un mapa interactivo con todos los reportes de incidentes aprobados y pendientes
en el área. Permite a los usuarios ver reportes cercanos, navegar a detalles,
crear nuevos reportes, actualizar la ubicación actual y acceder a configuraciones
del perfil, proporcionando una vista general de la actividad de seguridad en la comunidad.
*/

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import mx.edu.utng.alertavecinal.data.model.ReportStatus
import mx.edu.utng.alertavecinal.data.model.ReportType
import mx.edu.utng.alertavecinal.ui.components.LoadingIndicator
import mx.edu.utng.alertavecinal.utils.Constants
import mx.edu.utng.alertavecinal.utils.NotificationUtils
import mx.edu.utng.alertavecinal.viewmodel.AuthViewModel
import mx.edu.utng.alertavecinal.viewmodel.MapViewModel

fun getMarkerColor(reportType: ReportType): Float {
    return when (reportType) {
        ReportType.ROBBERY -> BitmapDescriptorFactory.HUE_RED
        ReportType.FIRE -> BitmapDescriptorFactory.HUE_ORANGE
        ReportType.ACCIDENT -> BitmapDescriptorFactory.HUE_YELLOW
        ReportType.SUSPICIOUS_PERSON -> BitmapDescriptorFactory.HUE_VIOLET
        ReportType.FIGHT -> BitmapDescriptorFactory.HUE_ROSE
        ReportType.VANDALISM -> BitmapDescriptorFactory.HUE_MAGENTA
        ReportType.NOISE -> BitmapDescriptorFactory.HUE_BLUE
        ReportType.LOST_PET -> BitmapDescriptorFactory.HUE_CYAN
        ReportType.OTHER -> BitmapDescriptorFactory.HUE_GREEN
    }
}

fun getMarkerColorByStatus(status: ReportStatus): Float {
    return when (status) {
        ReportStatus.APPROVED -> BitmapDescriptorFactory.HUE_GREEN
        ReportStatus.PENDING -> BitmapDescriptorFactory.HUE_ORANGE
        ReportStatus.REJECTED -> BitmapDescriptorFactory.HUE_RED
    }
}

fun getReportTypeName(reportType: ReportType): String {
    return when (reportType) {
        ReportType.ROBBERY -> "Robo"
        ReportType.FIRE -> "Incendio"
        ReportType.ACCIDENT -> "Accidente"
        ReportType.SUSPICIOUS_PERSON -> "Persona Sospechosa"
        ReportType.FIGHT -> "Pelea"
        ReportType.VANDALISM -> "Vandalismo"
        ReportType.NOISE -> "Ruido"
        ReportType.LOST_PET -> "Mascota Perdida"
        ReportType.OTHER -> "Otro"
    }
}

fun getStatusEmoji(status: ReportStatus): String {
    return when (status) {
        ReportStatus.APPROVED -> "✅"
        ReportStatus.PENDING -> "⏳"
        ReportStatus.REJECTED -> "❌"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    navController: NavController,
    viewModel: MapViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val mapState by viewModel.mapState.collectAsState()
    val context = LocalContext.current

    var showMenu by remember { mutableStateOf(false) }

    var cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            mapState.currentLocation ?: LatLng(0.0, 0.0), // Ubicación neutral
            2f // Zoom global si no hay ubicación
        )
    }

    LaunchedEffect(Unit) {
        NotificationUtils.createNotificationChannels(context)
    }

    LaunchedEffect(mapState.currentLocation) {
        mapState.currentLocation?.let { location ->
            cameraPositionState.position = CameraPosition.fromLatLngZoom(location, 12f)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadAllReports()
        viewModel.getCurrentLocation()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (mapState.filterType != null) {
                            "Mapa - ${mapState.filterType}"
                        } else {
                            "Mapa de Alertas Vecinales"
                        }
                    )
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.refreshData()
                            NotificationUtils.showSimpleNotification(
                                context,
                                "Mapa Actualizado",
                                "Reportes sincronizados correctamente"
                            )
                        }
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualizar")
                    }

                    // Botón de menú
                    IconButton(
                        onClick = { showMenu = true }
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "Menú")
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Perfil") },
                            onClick = {
                                navController.navigate("profile")
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Cerrar Sesión") },
                            onClick = {
                                authViewModel.logout()
                                navController.navigate("welcome") {
                                    popUpTo(0) { inclusive = true }
                                }
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(Icons.Default.LocationOn, contentDescription = null)
                            }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            Box {
                ExtendedFloatingActionButton(
                    onClick = {
                        navController.navigate("create_report")
                    },
                    icon = { Icon(Icons.Default.Add, contentDescription = "Crear reporte") },
                    text = { Text("Reportar") }
                )

                // FAB pequeño para centrar en ubicación actual (solo si hay ubicación)
                if (mapState.currentLocation != null) {
                    FloatingActionButton(
                        onClick = {
                            mapState.currentLocation?.let { location ->
                                cameraPositionState.position = CameraPosition.fromLatLngZoom(location, 15f)
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(bottom = 72.dp)
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Mi ubicación")
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (mapState.isLoading) {
                LoadingIndicator()
            } else {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(
                        isMyLocationEnabled = mapState.isLocationEnabled,
                        isTrafficEnabled = false
                    ),
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = true,
                        compassEnabled = true,
                        myLocationButtonEnabled = false,
                        mapToolbarEnabled = true
                    )
                ) {
                    // Marcador de ubicación actual (solo si existe)
                    mapState.currentLocation?.let { location ->
                        Marker(
                            state = MarkerState(position = location),
                            title = "Mi ubicación",
                            snippet = "Estás aquí",
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                        )
                    }

                    // Mostrar TODOS los reportes APPROVED y PENDING (de todos los usuarios)
                    mapState.reports.forEach { report ->
                        val reportLocation = LatLng(
                            report.latitude,
                            report.longitude
                        )

                        // SOLO mostrar reportes APPROVED y PENDING (no mostrar REJECTED)
                        if (report.status == ReportStatus.APPROVED || report.status == ReportStatus.PENDING) {
                            Marker(
                                state = MarkerState(position = reportLocation),
                                title = "${getStatusEmoji(report.status)} ${report.title}",
                                snippet = "Por: ${report.userName} - ${getReportTypeName(report.reportType)}",
                                icon = BitmapDescriptorFactory.defaultMarker(
                                    getMarkerColorByStatus(report.status)
                                ),
                                onInfoWindowClick = {
                                    navController.navigate("${Constants.ROUTE_REPORT_DETAIL}/${report.id}")
                                }
                            )
                        }
                    }
                }

                // Mostrar mensaje si no hay ubicación
                if (mapState.currentLocation == null && !mapState.isLoading) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "📍 Ubicación no disponible",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                // Mostrar estadísticas de reportes (de TODOS los usuarios)
                val approvedCount = mapState.reports.count { it.status == ReportStatus.APPROVED }
                val pendingCount = mapState.reports.count { it.status == ReportStatus.PENDING }
                val totalCount = mapState.reports.size

                if ((approvedCount > 0 || pendingCount > 0) && !mapState.isLoading) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "📊 Total: $totalCount | ✅ $approvedCount | ⏳ $pendingCount",
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

                // Mostrar mensaje si no hay reportes
                if (mapState.reports.isEmpty() && !mapState.isLoading) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "No hay reportes en el mapa\n¡Sé el primero en reportar!",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }

            mapState.error?.let { error ->
                Snackbar(
                    action = {
                        TextButton(
                            onClick = { viewModel.clearError() }
                        ) {
                            Text("OK")
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text(error)
                }
            }
        }
    }
}


@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun MapScreenPreview() {
    // Para preview, necesitarías un NavController mock
    // MapScreen(navController = rememberNavController())
}
```

### Paso 7.4: CreateReportScreen - Crear reporte
Explicación detallada: Formulario completo con validación, selección de ubicación y carga de imágenes.

```
package mx.edu.utng.alertavecinal.ui.screens

/*
Clase CreateReportScreen: Esta pantalla permite a los usuarios crear nuevos
reportes de incidentes en la aplicación. Proporciona un formulario completo
con campos para tipo de incidente, título, descripción y selección de ubicación
(ya sea automática mediante GPS o manual en el mapa). Valida todos los campos
requeridos antes de enviar el reporte a la base de datos.
*/

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.maps.model.LatLng
import mx.edu.utng.alertavecinal.data.model.ReportType
import mx.edu.utng.alertavecinal.ui.components.CustomButton
import mx.edu.utng.alertavecinal.ui.components.CustomTextField
import mx.edu.utng.alertavecinal.ui.components.ErrorMessage
import mx.edu.utng.alertavecinal.utils.Constants
import mx.edu.utng.alertavecinal.viewmodel.AuthViewModel
import mx.edu.utng.alertavecinal.viewmodel.MapViewModel
import mx.edu.utng.alertavecinal.viewmodel.ReportViewModel
import kotlin.String

data class CreateReportFormState(
    val adrees:String="",
    val title: String = "",
    val description: String = "",
    val selectedType: ReportType? = null,
    val currentLocation: String? = null,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val isLoadingLocation: Boolean = false,
    val locationError: String? = null,
    val isSubmitted: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateReportScreen(
    navController: NavController,
    reportViewModel: ReportViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    mapViewModel: MapViewModel = hiltViewModel()
) {
    val reportState by reportViewModel.reportState.collectAsState()
    val authState by authViewModel.authState.collectAsState()
    val mapState by mapViewModel.mapState.collectAsState()
    val context = LocalContext.current

    // Estado local del formulario
    var formState by remember {
        mutableStateOf(CreateReportFormState())
    }

    var isTypeExpanded by remember { mutableStateOf(false) }
    var showSuccessMessage by remember { mutableStateOf(false) }
    var locationAttempts by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        val savedState = reportViewModel.createReportState
        if (savedState.title.isNotEmpty() || savedState.description.isNotEmpty() || savedState.selectedType != null) {
            formState = formState.copy(

                title = savedState.title,
                description = savedState.description,
                selectedType = savedState.selectedType,
                currentLocation = savedState.currentLocation,
                latitude = savedState.latitude,
                longitude = savedState.longitude
            )
        }
    }

    fun checkForSelectedLocation() {
        println("📍 DEBUG CreateReport: Verificando ubicación seleccionada...")

        val latLngFromPrevious = navController.previousBackStackEntry?.savedStateHandle?.get<LatLng>("selected_location")
        val latLngFromCurrent = navController.currentBackStackEntry?.savedStateHandle?.get<LatLng>("selected_location")

        val latLng = latLngFromPrevious ?: latLngFromCurrent

        if (latLng != null) {
            println("📍 DEBUG CreateReport: ¡UBICACIÓN ENCONTRADA! ${latLng.latitude}, ${latLng.longitude}")

            formState = formState.copy(
                currentLocation = "Ubicación seleccionada en el mapa",
                latitude = latLng.latitude,
                longitude = latLng.longitude,
                locationError = null,
                isLoadingLocation = false
            )

            reportViewModel.updateCreateReportState(

                title = formState.title,
                description = formState.description,
                selectedType = formState.selectedType,
                currentLocation = "Ubicación seleccionada en el mapa",
                latitude = latLng.latitude,
                longitude = latLng.longitude
            )

            // Limpiar TODOS los lugares donde podría estar guardada
            navController.previousBackStackEntry?.savedStateHandle?.set("selected_location", null)
            navController.currentBackStackEntry?.savedStateHandle?.set("selected_location", null)

            println("📍 DEBUG CreateReport: Ubicación PROCESADA y LIMPIADA")
        } else {
            println("📍 DEBUG CreateReport: No se encontró ubicación seleccionada")
        }
    }

    LaunchedEffect(Unit) {
        // Verificar inmediatamente al cargar la pantalla
        checkForSelectedLocation()
    }

    // También verificar cuando la pantalla recibe foco
    LaunchedEffect(navController.currentBackStackEntry) {
        // Este se ejecuta cuando regresas de otra pantalla
        checkForSelectedLocation()
    }

    // VERIFICACIÓN ROBUSTA DEL USUARIO
    LaunchedEffect(Unit) {
        authViewModel.checkCurrentUser()
    }

    // Obtener ubicación cuando el usuario la solicita
    LaunchedEffect(locationAttempts) {
        if (locationAttempts > 0) {
            mapViewModel.getCurrentLocation()
        }
    }

    // Obtener usuario actual de manera segura
    val currentUser = authState.currentUser
    val userId = currentUser?.id ?: ""
    val userName = currentUser?.name ?: "Usuario"

    // Detectar problema de autenticación
    val hasAuthProblem = authState.isAuthenticated && currentUser == null

    // Usar ubicación real del MapViewModel
    LaunchedEffect(mapState.currentLocation) {
        mapState.currentLocation?.let { location ->
            // SOLO actualizar si no hay una ubicación manual seleccionada
            if (formState.currentLocation != "Ubicación seleccionada en el mapa") {
                formState = formState.copy(
                    currentLocation = "Ubicación actual obtenida",
                    latitude = location.latitude,
                    longitude = location.longitude,
                    locationError = null,
                    isLoadingLocation = false
                )

                reportViewModel.updateCreateReportState(
                    title = formState.title,
                    description = formState.description,
                    selectedType = formState.selectedType,
                    currentLocation = "Ubicación actual obtenida",
                    latitude = location.latitude,
                    longitude = location.longitude
                )
            }
        }
    }

    LaunchedEffect(mapState.error) {
        mapState.error?.let { error ->
            if (error.contains("ubicación", ignoreCase = true) ||
                error.contains("location", ignoreCase = true)) {
                formState = formState.copy(
                    locationError = "No se pudo obtener la ubicación. Verifica que tengas los permisos activados y el GPS encendido.",
                    isLoadingLocation = false
                )
            }
        }
    }

    // Navegar de regreso si el reporte se creó exitosamente
    LaunchedEffect(reportState.reports) {
        if (formState.isSubmitted && reportState.error == null && !reportState.isLoading) {
            showSuccessMessage = true
            kotlinx.coroutines.delay(1500)
            navController.popBackStack()
        }
    }

    // Limpiar error cuando se desmonta el componente
    LaunchedEffect(Unit) {
        reportViewModel.clearError()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reportar Incidente") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Título
                Text(
                    text = "Nuevo Reporte",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )

                Text(
                    text = "Completa la información del incidente",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // MOSTRAR ERROR DE AUTENTICACIÓN SI HAY PROBLEMA
                if (hasAuthProblem) {
                    ErrorMessage(message = authState.error ?: "Problema con tu sesión. Por favor, cierra sesión y vuelve a iniciar.")

                    CustomButton(
                        text = "Cerrar Sesión y Volver a Login",
                        onClick = {
                            authViewModel.logout()
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = MaterialTheme.colorScheme.error
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Solo mostrar el formulario si no hay problemas de autenticación
                if (!hasAuthProblem) {
                    // Campo de tipo de incidente
                    Text(
                        text = "Tipo de Incidente *",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    ExposedDropdownMenuBox(
                        expanded = isTypeExpanded,
                        onExpandedChange = { isTypeExpanded = !isTypeExpanded }
                    ) {
                        TextField(
                            value = formState.selectedType?.let { getReportTypeDisplayName(it) } ?: "Selecciona un tipo",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isTypeExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            leadingIcon = {
                                Icon(Icons.Default.Warning, contentDescription = "Tipo")
                            },
                            placeholder = {
                                Text("Selecciona el tipo de incidente")
                            },
                            isError = formState.selectedType == null && formState.isSubmitted
                        )

                        ExposedDropdownMenu(
                            expanded = isTypeExpanded,
                            onDismissRequest = { isTypeExpanded = false }
                        ) {
                            ReportType.values().forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(getReportTypeDisplayName(type)) },
                                    onClick = {
                                        formState = formState.copy(selectedType = type)
                                        reportViewModel.updateCreateReportState(
                                            title = formState.title,
                                            description = formState.description,
                                            selectedType = type,
                                            currentLocation = formState.currentLocation,
                                            latitude = formState.latitude,
                                            longitude = formState.longitude
                                        )
                                        isTypeExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Mostrar error si no se seleccionó tipo
                    if (formState.selectedType == null && formState.isSubmitted) {
                        Text(
                            text = "Debes seleccionar un tipo de incidente",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    CustomTextField(
                        value = formState.title,
                        onValueChange = { newTitle ->
                            formState = formState.copy(title = newTitle)
                            reportViewModel.updateCreateReportState(
                                title = newTitle,
                                description = formState.description,
                                selectedType = formState.selectedType,
                                currentLocation = formState.currentLocation,
                                latitude = formState.latitude,
                                longitude = formState.longitude
                            )
                        },
                        label = "Título del reporte *",
                        leadingIcon = Icons.Default.Description,
                        modifier = Modifier.fillMaxWidth(),
                        isError = formState.title.isEmpty() && formState.isSubmitted
                    )

                    if (formState.title.isEmpty() && formState.isSubmitted) {
                        Text(
                            text = "El título es obligatorio",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    CustomTextField(
                        value = formState.description,
                        onValueChange = { newDescription ->
                            formState = formState.copy(description = newDescription)
                            reportViewModel.updateCreateReportState(
                                title = formState.title,
                                description = newDescription,
                                selectedType = formState.selectedType,
                                currentLocation = formState.currentLocation,
                                latitude = formState.latitude,
                                longitude = formState.longitude
                            )
                        },
                        label = "Descripción detallada *",
                        leadingIcon = Icons.Default.Description,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        isError = formState.description.isEmpty() && formState.isSubmitted
                    )

                    if (formState.description.isEmpty() && formState.isSubmitted) {
                        Text(
                            text = "La descripción es obligatoria",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Text(
                        text = "Ubicación *",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    CustomButton(
                        text = when {
                            formState.isLoadingLocation -> "Obteniendo ubicación..."
                            formState.currentLocation != null -> "📍 ${formState.currentLocation}"
                            formState.locationError != null -> "⚠️ Reintentar ubicación automática"
                            else -> "Usar mi ubicación actual"
                        },
                        onClick = {
                            locationAttempts++
                        },
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = when {
                            formState.currentLocation == "Ubicación seleccionada en el mapa" -> MaterialTheme.colorScheme.tertiary
                            formState.currentLocation != null -> MaterialTheme.colorScheme.primary
                            formState.locationError != null -> MaterialTheme.colorScheme.errorContainer
                            else -> MaterialTheme.colorScheme.secondary
                        },
                        enabled = !formState.isLoadingLocation,
                        isLoading = formState.isLoadingLocation
                    )

                    CustomButton(
                        text = "🗺️ Elegir ubicación en el mapa",
                        onClick = {
                            reportViewModel.updateCreateReportState(
                                title = formState.title,
                                description = formState.description,
                                selectedType = formState.selectedType,
                                currentLocation = formState.currentLocation,
                                latitude = formState.latitude,
                                longitude = formState.longitude
                            )
                            navController.navigate(Constants.ROUTE_SELECT_LOCATION)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = MaterialTheme.colorScheme.tertiary,
                        leadingIcon = Icons.Default.Map
                    )

                    formState.currentLocation?.let { location ->
                        Column(
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text(
                                text = "📍 $location",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Text(
                                text = "Coordenadas: ${String.format("%.6f", formState.latitude)}, ${String.format("%.6f", formState.longitude)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }

                    formState.locationError?.let { error ->
                        Column(
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text(
                                text = error,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )

                            TextButton(
                                onClick = {
                                    val intent = android.content.Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                                    context.startActivity(intent)
                                }
                            ) {
                                Text("Abrir configuración de ubicación")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    reportState.error?.let { error ->
                        ErrorMessage(message = error)
                    }

                    CustomButton(
                        text = if (reportState.isLoading) "Enviando..." else "Enviar Reporte",
                        onClick = {
                            if (formState.title.isNotEmpty() &&
                                formState.description.isNotEmpty() &&
                                formState.selectedType != null &&
                                formState.currentLocation != null) {

                                formState = formState.copy(isSubmitted = true)
                                reportViewModel.createReport(
                                    title = formState.title,
                                    description = formState.description,
                                    reportType = formState.selectedType!!,
                                    latitude = formState.latitude,
                                    longitude = formState.longitude,
                                    address = formState.currentLocation,
                                    imageUrl = null,
                                    userId = userId,
                                    userName = userName
                                )
                            } else {
                                formState = formState.copy(isSubmitted = true)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = formState.title.isNotEmpty() &&
                                formState.description.isNotEmpty() &&
                                formState.selectedType != null &&
                                formState.currentLocation != null &&
                                !reportState.isLoading,
                        isLoading = reportState.isLoading
                    )

                    // Loading
                    if (reportState.isLoading) {
                        Spacer(modifier = Modifier.height(16.dp))
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(48.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }

            // Snackbar para éxito
            if (showSuccessMessage) {
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text("✅ Reporte creado exitosamente")
                }
            }
        }
    }
}

// Función auxiliar para obtener nombre legible del tipo de reporte
private fun getReportTypeDisplayName(reportType: ReportType): String {
    return when (reportType) {
        ReportType.ROBBERY -> "Robo"
        ReportType.FIRE -> "Incendio"
        ReportType.ACCIDENT -> "Accidente"
        ReportType.SUSPICIOUS_PERSON -> "Persona Sospechosa"
        ReportType.FIGHT -> "Pelea"
        ReportType.VANDALISM -> "Vandalismo"
        ReportType.NOISE -> "Ruido"
        ReportType.LOST_PET -> "Mascota Perdida"
        ReportType.OTHER -> "Otro"
    }
}

@Preview(showBackground = true)
@Composable
fun CreateReportScreenPreview() {
    CreateReportScreen(navController = rememberNavController())
}
```

### Paso 7.5: ReportDetailScreen - Detalles de reporte
Analogía: Como el expediente completo de un caso policial, muestra toda la información relevante en un solo lugar.

```
package mx.edu.utng.alertavecinal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import mx.edu.utng.alertavecinal.data.model.ReportStatus
import mx.edu.utng.alertavecinal.utils.FormatUtils
import mx.edu.utng.alertavecinal.viewmodel.ReportViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportDetailScreen(
    navController: NavController,
    reportId: String,
    viewModel: ReportViewModel = hiltViewModel()
) {
    // Obtener el estado del ViewModel
    val reportState by viewModel.reportState.collectAsState()

    // Variables locales para el reporte
    val selectedReport = reportState.selectedReport
    val isLoading = reportState.isLoading
    val error = reportState.error

    // Estado para controlar si ya intentamos cargar
    var hasAttemptedLoad by remember { mutableStateOf(false) }

    // Efecto para cargar el reporte cuando se inicia la pantalla
    LaunchedEffect(reportId) {
        if (reportId.isNotEmpty() && !hasAttemptedLoad) {
            println("🔍 ReportDetailScreen - Iniciando carga para reportId: $reportId")

            // 1. Primero buscar en las listas ya cargadas
            val currentReport = selectedReport
            if (currentReport?.id == reportId) {
                println("✅ ReportDetailScreen - Ya tiene el reporte seleccionado")
                return@LaunchedEffect
            }

            // 2. Buscar en todas las listas disponibles
            val allReports = reportState.reports
            println("📊 ReportDetailScreen - Total reportes en lista: ${allReports.size}")

            val foundInList = allReports.find { it.id == reportId }
            if (foundInList != null) {
                println("✅ ReportDetailScreen - Encontrado en lista local: ${foundInList.title}")
                viewModel.selectReport(foundInList)
            } else {
                println("🔍 ReportDetailScreen - No en lista, llamando a loadReportById")
                viewModel.loadReportById(reportId)
            }

            hasAttemptedLoad = true
        }
    }

    // También observar cambios en las listas
    LaunchedEffect(reportState.reports) {
        if (reportId.isNotEmpty() && selectedReport?.id != reportId) {
            val foundInUpdatedList = reportState.reports.find { it.id == reportId }
            foundInUpdatedList?.let {
                println("📈 ReportDetailScreen - Nueva lista contiene el reporte")
                viewModel.selectReport(it)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when {
                            isLoading -> "Cargando..."
                            selectedReport != null -> "Detalles del Reporte"
                            else -> "Reporte no encontrado"
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Estado de carga
            if (isLoading) {
                LoadingState(reportId = reportId)
            }
            // Estado de error
            else if (error != null) {
                ErrorState(
                    error = error,
                    reportId = reportId,
                    onRetry = { viewModel.loadReportById(reportId) },
                    onBack = { navController.popBackStack() }
                )
            }
            // Reporte no encontrado
            else if (selectedReport == null) {
                NotFoundState(
                    reportId = reportId,
                    onRetry = { viewModel.loadReportById(reportId) },
                    onBack = { navController.popBackStack() }
                )
            }
            // Reporte rechazado (mostrar mensaje especial)
            else if (selectedReport.status == ReportStatus.REJECTED) {
                RejectedState(
                    report = selectedReport,
                    onBack = { navController.popBackStack() }
                )
            }
            // Mostrar detalles del reporte (aprobado o pendiente)
            else {
                ReportContent(
                    report = selectedReport,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun LoadingState(reportId: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                strokeWidth = 4.dp
            )
            Text(
                text = "Cargando reporte...",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "ID: $reportId",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun ErrorState(
    error: String,
    reportId: String,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = "Error",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Ocurrió un error",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.error,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = error,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        Text(
            text = "ID: $reportId",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onRetry,
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                Text("Reintentar")
            }

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                Text("Volver al mapa")
            }
        }
    }
}

@Composable
private fun NotFoundState(
    reportId: String,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.QuestionMark,
            contentDescription = "No encontrado",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Reporte no encontrado",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "El reporte que buscas no está disponible o fue eliminado",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        Text(
            text = "ID: $reportId",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(top = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onRetry,
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                Text("Intentar nuevamente")
            }

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                Text("Volver al mapa")
            }
        }
    }
}

@Composable
private fun RejectedState(
    report: mx.edu.utng.alertavecinal.data.model.Report,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Rechazado",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Reporte Rechazado",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.error,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Este reporte fue revisado por un moderador y no cumple con las políticas de la comunidad",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        // Mostrar motivo de rechazo si existe
        report.rejectionReason?.let { reason ->
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Motivo del rechazo:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = reason,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            Text("Volver al mapa")
        }
    }
}

@Composable
private fun ReportContent(
    report: mx.edu.utng.alertavecinal.data.model.Report,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        // Header con título y estado
        ReportHeaderSection(report = report)

        Spacer(modifier = Modifier.height(16.dp))

        // Información básica
        ReportInfoSection(report = report)

        Spacer(modifier = Modifier.height(16.dp))

        // Imagen (si existe)
        report.imageUrl?.let { imageUrl ->
            ReportImageSection(imageUrl = imageUrl)
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Información de moderación (si fue moderado)
        if (report.approvedBy != null || report.rejectionReason != null) {
            ModerationHistorySection(report = report)
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Espacio al final
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun ReportHeaderSection(report: mx.edu.utng.alertavecinal.data.model.Report) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = report.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    modifier = Modifier.weight(1f)
                )

                // Badge de estado
                val (backgroundColor, textColor) = when (report.status) {
                    ReportStatus.APPROVED -> Pair(Color(0xFF4CAF50), Color.White)
                    ReportStatus.PENDING -> Pair(Color(0xFFFFA000), Color.Black)
                    ReportStatus.REJECTED -> Pair(Color(0xFFF44336), Color.White)
                }

                Text(
                    text = when (report.status) {
                        ReportStatus.APPROVED -> "APROBADO"
                        ReportStatus.PENDING -> "PENDIENTE"
                        ReportStatus.REJECTED -> "RECHAZADO"
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(backgroundColor)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = report.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Tipo de incidente
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Tipo",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Tipo: ${report.reportType.name.replace("_", " ")}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun ReportInfoSection(report: mx.edu.utng.alertavecinal.data.model.Report) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Usuario que reportó
            InfoRow(
                icon = Icons.Default.Person,
                title = "Reportado por",
                value = report.userName
            )

            Divider()

            // Ubicación
            InfoRow(
                icon = Icons.Default.LocationOn,
                title = "Ubicación",
                value = report.address ?: "No especificada"
            )

            Divider()

            // Fecha y hora
            InfoRow(
                icon = Icons.Default.CalendarToday,
                title = "Fecha y Hora",
                value = FormatUtils.formatDate(report.createdAt)
            )

            Divider()

            // Coordenadas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Latitud",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = String.format("%.6f", report.latitude),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Longitud",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = String.format("%.6f", report.longitude),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
private fun ReportImageSection(imageUrl: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Photo,
                    contentDescription = "Imagen",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Imagen Adjunta",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }

            AsyncImage(
                model = imageUrl,
                contentDescription = "Imagen del reporte",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
private fun ModerationHistorySection(report: mx.edu.utng.alertavecinal.data.model.Report) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Historial de Moderación",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            report.approvedBy?.let { approver ->
                InfoRow(
                    icon = Icons.Default.Person,
                    title = "Moderado por",
                    value = approver
                )
            }

            report.rejectionReason?.let { reason ->
                Spacer(modifier = Modifier.height(8.dp))
                InfoRow(
                    icon = Icons.Default.Warning,
                    title = "Motivo de rechazo",
                    value = reason
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Última actualización: ${FormatUtils.formatRelativeTime(report.updatedAt)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}
```

### Paso 7.6: ModeratorDashboardScreen - Panel de moderador
Explicación detallada: Dashboard especializado con estadísticas en tiempo real y herramientas de moderación.

```
package mx.edu.utng.alertavecinal.ui.screens

/*
Clase ModeratorDashboardScreen: Esta pantalla proporciona un panel de control
especializado para moderadores y administradores, mostrando estadísticas en tiempo
real de reportes (pendientes, aprobados, rechazados) y permitiendo gestionar todos
los reportes de incidentes. Ofrece funciones de filtrado, ordenación y acciones
rápidas de moderación para revisar y tomar decisiones sobre los reportes enviados
por los usuarios.
*/

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import mx.edu.utng.alertavecinal.R
import mx.edu.utng.alertavecinal.data.model.Report
import mx.edu.utng.alertavecinal.data.model.ReportType
import mx.edu.utng.alertavecinal.ui.components.EmptyState
import mx.edu.utng.alertavecinal.ui.components.ErrorMessage
import mx.edu.utng.alertavecinal.ui.components.LoadingIndicator
import mx.edu.utng.alertavecinal.ui.components.ModeratorReportCard
import mx.edu.utng.alertavecinal.utils.Constants
import mx.edu.utng.alertavecinal.viewmodel.AuthViewModel
import mx.edu.utng.alertavecinal.viewmodel.ModeratorStats
import mx.edu.utng.alertavecinal.viewmodel.ModeratorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModeratorDashboardScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    onNavigateBack: () -> Unit = {}
) {
    val viewModel: ModeratorViewModel = hiltViewModel()
    val pendingReports by viewModel.pendingReports.collectAsStateWithLifecycle()
    val approvedReports by viewModel.approvedReports.collectAsStateWithLifecycle()
    val rejectedReports by viewModel.rejectedReports.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val moderatorStats by viewModel.moderatorStats.collectAsStateWithLifecycle()

    val authState by authViewModel.authState.collectAsState()
    val context = LocalContext.current

    var selectedFilter by remember { mutableStateOf<String?>(null) }
    var showFilterMenu by remember { mutableStateOf(false) }
    var activeTab by remember { mutableStateOf(ModeratorTab.PENDING) }

    fun handleLogoutAndNavigateToLogin() {
        Log.d("ModeratorDashboard", "Cerrando sesión y navegando al Login...")

        // 1. Cerrar sesión en el ViewModel de autenticación
        authViewModel.logout()

        // 2. Navegar al Login y limpiar el back stack completamente
        navController.navigate(Constants.ROUTE_LOGIN) {
            popUpTo(0) { inclusive = true }
            launchSingleTop = true
        }

        Log.d("ModeratorDashboard", "Sesión cerrada y navegación al Login exitosa")
    }



    // Función para manejar la navegación al Login
    fun navigateToLogin() {
        try {
            Log.d("ModeratorDashboard", "Navegando al Login desde ModeratorDashboard...")

            // Navegar al Login y limpiar el back stack
            navController.navigate(Constants.ROUTE_LOGIN) {
                // Remover todas las pantallas del stack incluyendo esta
                popUpTo(0) { inclusive = true }
                // Evitar múltiples instancias de Login
                launchSingleTop = true
            }

            Log.d("ModeratorDashboard", "Navegación al Login exitosa")
        } catch (e: Exception) {
            Log.e("ModeratorDashboard", "Error al navegar al Login: ${e.message}")
        }
    }

    LaunchedEffect(Unit) {
        Log.d("ModeratorDashboard", "Iniciando dashboard...")
        viewModel.loadAllModeratorData()

        Log.d("ModeratorDashboard", "Auth State: ${authState.isAuthenticated}")
        Log.d("ModeratorDashboard", "User: ${authState.currentUser?.name}")
        Log.d("ModeratorDashboard", "Role: ${authState.currentUser?.role?.name}")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Panel del Moderador")
                        if (authState.isAuthenticated) {
                            Text(
                                text = "(${authState.currentUser?.name ?: "Moderador"})",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        handleLogoutAndNavigateToLogin()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Cerrar sesión y volver al Login")
                    }
                },
                actions = {
                    IconButton(onClick = { showFilterMenu = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filtrar")
                    }

                    IconButton(onClick = { /* TODO: Implementar ordenación */ }) {
                        Icon(Icons.Default.Sort, contentDescription = "Ordenar")
                    }

                    IconButton(
                        onClick = {
                            viewModel.loadAllModeratorData()
                            Log.d("ModeratorDashboard", "Refrescando datos...")
                        },
                        enabled = !isLoading
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refrescar")
                    }
                }
            )
        },
        floatingActionButton = {
            if (activeTab == ModeratorTab.PENDING && pendingReports.isNotEmpty()) {
                FloatingActionButton(
                    onClick = {
                        // Acción rápida: aprobar el primer reporte pendiente
                        if (pendingReports.isNotEmpty()) {
                            val firstReport = pendingReports.first()
                            val moderatorId = authState.currentUser?.id ?: "unknown"
                            val moderatorName = authState.currentUser?.name ?: "Moderador"

                            viewModel.approveReport(
                                reportId = firstReport.id,
                                moderatorId = moderatorId,
                                moderatorName = moderatorName,
                                comment = "Aprobado desde acción rápida"
                            )
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = "Revisar todos")
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                // Mostrar mensajes de error
                errorMessage?.let { message ->
                    ErrorMessage(
                        message = message,
                        onDismiss = { viewModel.clearError() }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Estadísticas
                StatisticsSection(stats = moderatorStats)

                Spacer(modifier = Modifier.height(16.dp))

                ModeratorTabs(
                    activeTab = activeTab,
                    onTabSelected = { tab ->
                        activeTab = tab
                        Log.d("ModeratorDashboard", "Cambiando a tab: $tab")
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                when (activeTab) {
                    ModeratorTab.PENDING -> {
                        if (isLoading) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                LoadingIndicator()
                            }
                        } else {
                            ReportsListSection(
                                reports = pendingReports,
                                emptyMessage = "No hay reportes pendientes de revisión",
                                emptyIcon = Icons.Default.CheckCircle,
                                onReportSelected = { reportId ->
                                    Log.d("ModeratorDashboard", "Click en reporte pendiente: $reportId")

                                    // Verificar que tenemos datos del moderador
                                    val moderatorId = authState.currentUser?.id ?: run {
                                        Log.e("ModeratorDashboard", "No hay ID de moderador disponible")
                                        "unknown_moderator_id"
                                    }

                                    val moderatorName = authState.currentUser?.name ?: run {
                                        Log.e("ModeratorDashboard", "No hay nombre de moderador disponible")
                                        "Moderador"
                                    }

                                    Log.d("ModeratorDashboard", "Navegando con:")
                                    Log.d("ModeratorDashboard", "  - Report ID: $reportId")
                                    Log.d("ModeratorDashboard", "  - Moderator ID: $moderatorId")
                                    Log.d("ModeratorDashboard", "  - Moderator Name: $moderatorName")

                                    // Navegar a la pantalla de revisión del moderador
                                    try {
                                        navController.navigate(
                                            "${Constants.ROUTE_MODERATOR_REVIEW}/$reportId/$moderatorId/$moderatorName"
                                        ) {
                                            launchSingleTop = true
                                        }
                                        Log.d("ModeratorDashboard", "Navegación exitosa")
                                    } catch (e: Exception) {
                                        Log.e("ModeratorDashboard", "Error en navegación: ${e.message}")
                                    }
                                }
                            )
                        }
                    }

                    ModeratorTab.APPROVED -> {
                        ReportsListSection(
                            reports = approvedReports,
                            emptyMessage = "No hay reportes aprobados",
                            emptyIcon = Icons.Default.CheckCircle,
                            onReportSelected = { reportId ->
                                Log.d("ModeratorDashboard", "Click en reporte aprobado: $reportId")
                                // Navegar a la pantalla de detalle normal
                                navController.navigate("${Constants.ROUTE_REPORT_DETAIL}/$reportId") {
                                    launchSingleTop = true
                                }
                            }
                        )
                    }

                    ModeratorTab.REJECTED -> {
                        ReportsListSection(
                            reports = rejectedReports,
                            emptyMessage = "No hay reportes rechazados",
                            emptyIcon = Icons.Default.Close,
                            onReportSelected = { reportId ->
                                Log.d("ModeratorDashboard", "Click en reporte rechazado: $reportId")
                                // Navegar a la pantalla de detalle normal
                                navController.navigate("${Constants.ROUTE_REPORT_DETAIL}/$reportId") {
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                }
            }
        }

        if (showFilterMenu) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 56.dp), // Debajo del TopAppBar
                contentAlignment = Alignment.TopEnd
            ) {
                FilterDropdownMenu(
                    selectedFilter = selectedFilter,
                    onFilterSelected = { filter ->
                        selectedFilter = filter
                        viewModel.filterReportsByType(filter)
                        showFilterMenu = false
                        Log.d("ModeratorDashboard", "Filtro aplicado: $filter")
                    },
                    onClearFilter = {
                        selectedFilter = null
                        viewModel.filterReportsByType(null)
                        showFilterMenu = false
                        Log.d("ModeratorDashboard", "Filtro limpiado")
                    },
                    onDismiss = {
                        showFilterMenu = false
                        Log.d("ModeratorDashboard", "Menú de filtros cerrado")
                    }
                )
            }
        }
    }
}


@Composable
private fun StatisticsSection(stats: ModeratorStats) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Estadísticas del Día",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    icon = Icons.Default.Pending,
                    value = stats.pendingCount.toString(),
                    label = "Pendientes",
                    color = MaterialTheme.colorScheme.secondary
                )

                StatItem(
                    icon = Icons.Default.CheckCircle,
                    value = stats.approvedCount.toString(),
                    label = "Aprobados",
                    color = MaterialTheme.colorScheme.primary
                )

                StatItem(
                    icon = Icons.Default.Close,
                    value = stats.rejectedCount.toString(),
                    label = "Rechazados",
                    color = MaterialTheme.colorScheme.error
                )

                StatItem(
                    icon = Icons.Default.Warning,
                    value = "${stats.approvalRate}%",
                    label = "Tasa Aprob.",
                    color = MaterialTheme.colorScheme.tertiary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tiempo promedio de respuesta: ${stats.averageResponseTime} min",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun StatItem(
    icon: ImageVector,
    value: String,
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(70.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(color.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )
    }
}

@Composable
private fun ModeratorTabs(
    activeTab: ModeratorTab,
    onTabSelected: (ModeratorTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ModeratorTab.values().forEach { tab ->
            val isActive = activeTab == tab

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (isActive) MaterialTheme.colorScheme.primary
                        else Color.Transparent
                    )
                    .clickable {
                        onTabSelected(tab)
                        Log.d("ModeratorTabs", "Tab seleccionado: ${tab.title}")
                    }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.title,
                        tint = if (isActive) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )

                    Text(
                        text = tab.title,
                        color = if (isActive) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ReportsListSection(
    reports: List<Report>,
    emptyMessage: String,
    emptyIcon: ImageVector,
    onReportSelected: (String) -> Unit
) {
    if (reports.isEmpty()) {
        EmptyState(
            title = emptyMessage,
            message = "Cuando haya nuevos reportes, aparecerán aquí",
            icon = emptyIcon
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(reports, key = { it.id }) { report ->
                ModeratorReportCard(
                    report = report,
                    onClick = {
                        Log.d("ReportsListSection", "Click en reporte: ${report.id}")
                        onReportSelected(report.id)
                    }
                )
            }
        }
    }
}

@Composable
private fun FilterDropdownMenu(
    selectedFilter: String?,
    onFilterSelected: (String?) -> Unit,
    onClearFilter: () -> Unit,
    onDismiss: () -> Unit
) {
    DropdownMenu(
        expanded = true,
        onDismissRequest = onDismiss,
        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
    ) {
        Text(
            text = "Filtrar por tipo",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        Divider()

        if (selectedFilter != null) {
            DropdownMenuItem(
                text = {
                    Text(
                        "Mostrar todos",
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                onClick = onClearFilter,
                modifier = Modifier.background(
                    if (selectedFilter == null) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    else Color.Transparent
                )
            )

            Divider()
        }

        // Todos los tipos de reporte
        ReportType.values().forEach { reportType ->
            DropdownMenuItem(
                text = {
                    Text(
                        text = reportType.name.replace("_", " "),
                        color = if (selectedFilter == reportType.name)
                            MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface
                    )
                },
                onClick = { onFilterSelected(reportType.name) },
                modifier = Modifier.background(
                    if (selectedFilter == reportType.name)
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    else Color.Transparent
                )
            )
        }
    }
}

@Composable
private fun ErrorMessage(
    message: String,
    onDismiss: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Error",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.weight(1f)
            )

            if (onDismiss != null) {
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar",
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }
}

enum class ModeratorTab(
    val title: String,
    val icon: ImageVector
) {
    PENDING("Pendientes", Icons.Default.Pending),
    APPROVED("Aprobados", Icons.Default.CheckCircle),
    REJECTED("Rechazados", Icons.Default.Close)
}
```

### Paso 7.7: ProfileScreen - Perfil de usuario
Analogía: Como el perfil de un usuario en una red social, muestra información personal, estadísticas y configuraciones.

```
package mx.edu.utng.alertavecinal.ui.screens

/*
Clase ProfileScreen: Esta pantalla muestra el perfil del usuario
autenticado, incluyendo información personal, estadísticas de sus
reportes (total, aprobados, pendientes, rechazados) y configuración de
notificaciones. También proporciona acceso al historial completo de
reportes del usuario con opción para eliminar reportes pendientes y la
funcionalidad para cerrar sesión.
*/

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Report
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import mx.edu.utng.alertavecinal.data.model.ReportStatus
import mx.edu.utng.alertavecinal.ui.components.CustomButton
import mx.edu.utng.alertavecinal.viewmodel.AuthViewModel
import mx.edu.utng.alertavecinal.viewmodel.ProfileViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val profileState by profileViewModel.userProfile.collectAsState()
    val authState by authViewModel.authState.collectAsState()
    val isLoading by profileViewModel.isLoading.collectAsState()
    val error by profileViewModel.error.collectAsState()
    val userReports by profileViewModel.userReports.collectAsState()
    val deleteState by profileViewModel.deleteState.collectAsState()

    val currentUserId = authState.currentUser?.id ?: ""

    var notificationsEnabled by remember { mutableStateOf(true) }
    var notificationRadius by remember { mutableStateOf(1000) }
    var showReportHistory by remember { mutableStateOf(false) }

    LaunchedEffect(authState.currentUser) {
        println("🔍 DEBUG ProfileScreen - authState.currentUser cambiado: ${authState.currentUser}")
        val currentUser = authState.currentUser
        if (currentUser != null) {
            println("🔍 DEBUG ProfileScreen - Cargando perfil para usuario: ${currentUser.id}")
            profileViewModel.loadUserProfile(currentUser.id)
        } else {
            println("🔍 DEBUG ProfileScreen - No hay usuario autenticado")
        }
    }

    LaunchedEffect(Unit) {
        // Verificar si ya hay un usuario pero no se ha cargado el perfil
        if (authState.currentUser != null && profileState == null) {
            println("🔍 DEBUG ProfileScreen - Cargando perfil en inicio")
            profileViewModel.loadUserProfile(authState.currentUser!!.id)
        }
    }

    LaunchedEffect(deleteState) {
        when (deleteState) {
            is ProfileViewModel.DeleteState.Success -> {
                // Se eliminó exitosamente, ya se recargaron los datos
            }
            is ProfileViewModel.DeleteState.Error -> {
                println("❌ Error al eliminar: ${(deleteState as ProfileViewModel.DeleteState.Error).message}")
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (showReportHistory) "Mi Historial" else "Mi Perfil")
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (showReportHistory) {
                            showReportHistory = false
                        } else {
                            navController.popBackStack()
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (showReportHistory) {
                ReportHistorySection(
                    userReports = userReports,
                    onReportClick = { report ->
                        navController.navigate("report_detail/${report.id}")
                    },
                    onBackClick = { showReportHistory = false },
                    profileViewModel = profileViewModel,
                    currentUserId = currentUserId
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Usuario",
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.size(12.dp))
                                Text(
                                    text = profileState?.name ?: "Usuario",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = "Email",
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.size(12.dp))
                                Text(
                                    text = profileState?.email ?: "email@ejemplo.com",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            profileState?.phone?.let { phone ->
                                if (phone.isNotEmpty()) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Phone,
                                            contentDescription = "Teléfono",
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.size(12.dp))
                                        Text(
                                            text = phone,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Configuración de Notificaciones",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Notificaciones activadas",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Switch(
                                    checked = notificationsEnabled,
                                    onCheckedChange = {
                                        notificationsEnabled = it
                                        profileState?.let { user ->
                                            profileViewModel.updateNotificationSettings(
                                                userId = user.id,
                                                radius = notificationRadius,
                                                enabled = it
                                            )
                                        }
                                    }
                                )
                            }

                            Divider()

                            Text(
                                text = "Radio de notificaciones: ${notificationRadius}m",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Mis Reportes",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                            )

                            val totalCount = userReports.size
                            val approvedCount = userReports.count { it.status == ReportStatus.APPROVED }
                            val pendingCount = userReports.count { it.status == ReportStatus.PENDING }
                            val rejectedCount = userReports.count { it.status == ReportStatus.REJECTED }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Total de reportes:",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = totalCount.toString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "✅ Aprobados:",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = approvedCount.toString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "⏳ Pendientes:",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = pendingCount.toString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "❌ Rechazados:",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = rejectedCount.toString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Botones de acción
                    CustomButton(
                        text = "Ver Historial de Reportes",
                        onClick = {
                            showReportHistory = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = MaterialTheme.colorScheme.secondary,
                        leadingIcon = Icons.Default.History
                    )

                    CustomButton(
                        text = "Cerrar Sesión",
                        onClick = {
                            authViewModel.logout()
                            navController.navigate("welcome") {
                                popUpTo(0)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = MaterialTheme.colorScheme.error
                    )
                }
            }

            error?.let { errorMessage ->
                Snackbar(
                    action = {
                        TextButton(
                            onClick = { profileViewModel.clearError() }
                        ) {
                            Text("OK")
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text(errorMessage)
                }
            }
        }
    }
}

// Sección de historial de reportes CON FUNCIONALIDAD DE ELIMINAR
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportHistorySection(
    userReports: List<mx.edu.utng.alertavecinal.data.model.Report>,
    onReportClick: (mx.edu.utng.alertavecinal.data.model.Report) -> Unit,
    onBackClick: () -> Unit,
    profileViewModel: ProfileViewModel,
    currentUserId: String
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var reportToDelete by remember { mutableStateOf<mx.edu.utng.alertavecinal.data.model.Report?>(null) }

    val deleteState by profileViewModel.deleteState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Encabezado del historial
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
            }
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = "Mis Reportes (${userReports.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
        }

        LaunchedEffect(deleteState) {
            when (deleteState) {
                is ProfileViewModel.DeleteState.Success -> {
                    showDeleteDialog = false
                    reportToDelete = null
                    profileViewModel.resetDeleteState()
                }
                is ProfileViewModel.DeleteState.Error -> {
                    profileViewModel.resetDeleteState()
                }
                else -> {}
            }
        }

        if (userReports.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Report,
                        contentDescription = "Sin reportes",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Text(
                        text = "No tienes reportes aún",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    CustomButton(
                        text = "Crear Primer Reporte",
                        onClick = onBackClick,
                        backgroundColor = MaterialTheme.colorScheme.primary
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(userReports) { report ->
                    Card(
                        onClick = { onReportClick(report) },
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = report.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )

                                    Text(
                                        text = report.description,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(top = 4.dp),
                                        maxLines = 2
                                    )

                                    Text(
                                        text = "Estado: ${report.status.name}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = when (report.status) {
                                            ReportStatus.APPROVED -> MaterialTheme.colorScheme.primary
                                            ReportStatus.PENDING -> MaterialTheme.colorScheme.secondary
                                            ReportStatus.REJECTED -> MaterialTheme.colorScheme.error
                                        },
                                        modifier = Modifier.padding(top = 8.dp)
                                    )

                                    Text(
                                        text = "Fecha: ${report.createdAt?.let {
                                            SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                                                .format(Date(it))
                                        } ?: "N/A"}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }

                                if (report.status == ReportStatus.PENDING && report.userId == currentUserId) {
                                    IconButton(
                                        onClick = {
                                            reportToDelete = report
                                            showDeleteDialog = true
                                        }
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Eliminar",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showDeleteDialog && reportToDelete != null) {
            AlertDialog(
                onDismissRequest = {
                    if (deleteState !is ProfileViewModel.DeleteState.Loading) {
                        showDeleteDialog = false
                        reportToDelete = null
                        profileViewModel.resetDeleteState()
                    }
                },
                title = { Text("Confirmar eliminación") },
                text = {
                    Column {
                        Text("¿Estás seguro de eliminar este reporte?")
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = "Título: ${reportToDelete?.title}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        Text(
                            "Esta acción no se puede deshacer.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (reportToDelete != null && currentUserId.isNotEmpty()) {
                                profileViewModel.deleteReport(reportToDelete!!.id, currentUserId)
                            }
                        },
                        enabled = deleteState !is ProfileViewModel.DeleteState.Loading
                    ) {
                        if (deleteState is ProfileViewModel.DeleteState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Eliminar")
                        }
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            if (deleteState !is ProfileViewModel.DeleteState.Loading) {
                                showDeleteDialog = false
                                reportToDelete = null
                                profileViewModel.resetDeleteState()
                            }
                        },
                        enabled = deleteState !is ProfileViewModel.DeleteState.Loading
                    ) {
                        Text("Cancelar")
                    }
                }
            )
        }

        // Mostrar loading global al eliminar
        if (deleteState is ProfileViewModel.DeleteState.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    MaterialTheme {
        ProfileScreen(navController = rememberNavController())
    }
}
```

## 🔧 CAPA DE UTILIDADES (UTILS) - 6 Clases
### Paso 8.1: Constants - Constantes globales
Analogía: Como el manual de procedimientos de una empresa, define todas las reglas y configuraciones estándar.

```
package mx.edu.utng.alertavecinal.utils

/*
Clase Constants: Este archivo centraliza todas las constantes y configuraciones
de la aplicación en un solo lugar, incluyendo rutas de navegación, configuraciones
de Firebase, formatos de fecha, parámetros de UI, límites de datos y valores por
defecto. Sirve como fuente única de verdad para mantener consistencia y facilitar
el mantenimiento en toda la aplicación.
*/

object Constants {
    // Rutas de navegación
    const val ROUTE_WELCOME = "welcome"
    const val ROUTE_LOGIN = "login"
    const val ROUTE_REGISTER = "register"
    const val ROUTE_MAP = "map"
    const val ROUTE_CREATE_REPORT = "create_report"
    const val ROUTE_SELECT_LOCATION = "select_location"
    const val ROUTE_PENDING_REPORTS = "pending_reports"
    const val ROUTE_PROFILE = "profile"
    const val ROUTE_REPORT_DETAIL = "report_detail"

    // NUEVAS RUTAS PARA MODERADOR
    const val ROUTE_MODERATOR_DASHBOARD = "moderator_dashboard"
    const val ROUTE_MODERATOR_REVIEW = "moderator_review"

    // Parámetros de navegación
    const val KEY_REPORT_ID = "reportId"
    const val KEY_USER_ID = "userId"
    const val KEY_MODERATOR_ID = "moderatorId"
    const val KEY_MODERATOR_NAME = "moderatorName"

    // Formatos de fecha
    const val DATE_FORMAT_DISPLAY = "dd/MM/yyyy HH:mm"
    const val DATE_FORMAT_STORAGE = "yyyy-MM-dd HH:mm:ss"
    const val TIME_FORMAT = "HH:mm"

    const val MAX_REPORT_TITLE_LENGTH = 100
    const val MAX_REPORT_DESCRIPTION_LENGTH = 250
    // Configuración de la aplicación
    const val APP_NAME = "Alerta Vecinal"
    const val MIN_PASSWORD_LENGTH = 6
    const val MAX_TITLE_LENGTH = 100
    const val MAX_DESCRIPTION_LENGTH = 500

    // Radio de notificaciones (en metros)
    const val DEFAULT_NOTIFICATION_RADIUS = 1000 // 1km
    const val MIN_NOTIFICATION_RADIUS = 100 // 100m
    const val MAX_NOTIFICATION_RADIUS = 5000 // 5km

    // Tiempos de actualización (en milisegundos)
    const val MAP_UPDATE_INTERVAL = 30000L // 30 segundos
    const val NOTIFICATION_CHECK_INTERVAL = 60000L // 1 minuto

    // Configuración de Firebase
    const val USERS_COLLECTION = "users"
    const val REPORTS_COLLECTION = "reports"
    const val NOTIFICATIONS_COLLECTION = "notifications"
    const val MODERATION_HISTORY_COLLECTION = "moderation_history"

    // Estados de reporte
    const val STATUS_PENDING = "PENDING"
    const val STATUS_APPROVED = "APPROVED"
    const val STATUS_REJECTED = "REJECTED"

    // Tipos de notificación
    const val NOTIFICATION_TYPE_REPORT_APPROVED = "REPORT_APPROVED"
    const val NOTIFICATION_TYPE_REPORT_REJECTED = "REPORT_REJECTED"
    const val NOTIFICATION_TYPE_NEW_INCIDENT = "NEW_INCIDENT_NEARBY"
    const val NOTIFICATION_TYPE_INFO_REQUESTED = "INFO_REQUESTED"

    // Roles de usuario
    const val ROLE_USER = "USER"
    const val ROLE_MODERATOR = "MODERATOR"
    const val ROLE_ADMIN = "ADMIN"

    // Acciones de moderación
    const val MODERATION_ACTION_APPROVE = "APPROVE"
    const val MODERATION_ACTION_REJECT = "REJECT"
    const val MODERATION_ACTION_EDIT = "EDIT"
    const val MODERATION_ACTION_REQUEST_INFO = "REQUEST_INFO"

    // Configuración de almacenamiento
    const val STORAGE_REPORTS_PATH = "reports/"
    const val STORAGE_PROFILES_PATH = "profiles/"

    // Tiempos de cache (en segundos)
    const val CACHE_DURATION_SHORT = 60L // 1 minuto
    const val CACHE_DURATION_MEDIUM = 300L // 5 minutos
    const val CACHE_DURATION_LONG = 3600L // 1 hora

    // Configuración de mapas
    const val DEFAULT_MAP_ZOOM = 15f
    const val DEFAULT_LATITUDE = 19.4326 // Ciudad de México
    const val DEFAULT_LONGITUDE = -99.1332
    const val MAP_PADDING = 100

    // Colores para tipos de reporte (en formato hex)
    const val COLOR_ROBBERY = "#F44336"
    const val COLOR_FIRE = "#FF5722"
    const val COLOR_ACCIDENT = "#FF9800"
    const val COLOR_SUSPICIOUS = "#9C27B0"
    const val COLOR_FIGHT = "#F44336"
    const val COLOR_VANDALISM = "#795548"
    const val COLOR_NOISE = "#607D8B"
    const val COLOR_LOST_PET = "#2196F3"
    const val COLOR_OTHER = "#FFC107"

    // Configuración de paginación
    const val PAGE_SIZE = 20
    const val INITIAL_LOAD_SIZE = 10

    // Nombres de preferencias
    const val PREFS_NAME = "alerta_vecina_prefs"
    const val PREFS_FIRST_LAUNCH = "first_launch"
    const val PREFS_NOTIFICATIONS_ENABLED = "notifications_enabled"
    const val PREFS_NOTIFICATION_RADIUS = "notification_radius"
    const val PREFS_DARK_MODE = "dark_mode"

    // URLs y enlaces
    const val PRIVACY_POLICY_URL = "https://tusitio.com/privacy"
    const val TERMS_CONDITIONS_URL = "https://tusitio.com/terms"
    const val SUPPORT_EMAIL = "soporte@tusitio.com"
}
```

### Paso 8.2: FormatUtils - Utilidades de formato
Explicación detallada: Biblioteca de funciones para formatear datos de manera consistente en toda la aplicación.

```
package mx.edu.utng.alertavecinal.utils

/*
Clase FormatUtils: Este objeto proporciona funciones de utilidad para
formatear diferentes tipos de datos en toda la aplicación, incluyendo
fechas, distancias, direcciones, nombres de usuario, números telefónicos
y textos. Centraliza toda la lógica de formato para asegurar consistencia
en la presentación de datos a lo largo de la interfaz de usuario.
*/

import mx.edu.utng.alertavecinal.data.model.ReportType
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
```

### Paso 8.3: ImageUtils - Utilidades de imágenes
Analogía: Como el laboratorio de fotografía de un periódico, procesa y optimiza imágenes para diferentes usos.

```
package mx.edu.utng.alertavecinal.utils

/*
Clase ImageUtils: Este objeto proporciona funciones especializadas
para el procesamiento y optimización de imágenes en la aplicación,
incluyendo redimensionamiento, compresión, corrección de orientación,
validación de formatos y conversión entre diferentes formatos de imagen.
Ayuda a mantener un tamaño de archivo óptimo y un rendimiento adecuado
cuando los usuarios suben imágenes de reportes.
*/

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import java.io.ByteArrayOutputStream
import java.io.InputStream

object ImageUtils {

    private const val MAX_IMAGE_SIZE = 1024
    private const val COMPRESSION_QUALITY = 80
    private const val MAX_FILE_SIZE = 2 * 1024 * 1024

    fun resizeImage(bitmap: Bitmap, maxSize: Int = MAX_IMAGE_SIZE): Bitmap {
        var width = bitmap.width
        var height = bitmap.height

        if (width <= maxSize && height <= maxSize) {
            return bitmap
        }

        val ratio = width.toFloat() / height.toFloat()

        if (ratio > 1) {
            width = maxSize
            height = (maxSize / ratio).toInt()
        } else {
            height = maxSize
            width = (maxSize * ratio).toInt()
        }

        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }

    fun compressImage(bitmap: Bitmap, quality: Int = COMPRESSION_QUALITY): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        return outputStream.toByteArray()
    }

    fun decodeSampledBitmapFromStream(
        inputStream: InputStream,
        reqWidth: Int = MAX_IMAGE_SIZE,
        reqHeight: Int = MAX_IMAGE_SIZE
    ): Bitmap? {
        return try {
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeStream(inputStream, null, options)

            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

            options.inJustDecodeBounds = false
            inputStream.reset()
            BitmapFactory.decodeStream(inputStream, null, options)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    fun correctImageOrientation(bitmap: Bitmap, imagePath: String): Bitmap {
        return try {
            val exif = ExifInterface(imagePath)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )

            val matrix = Matrix()
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
                ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
                else -> return bitmap
            }

            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } catch (e: Exception) {
            e.printStackTrace()
            bitmap
        }
    }

    fun isImageTooLarge(fileSize: Long): Boolean {
        return fileSize > MAX_FILE_SIZE
    }

    fun getFileExtension(uri: Uri): String {
        val path = uri.toString()
        return path.substring(path.lastIndexOf(".") + 1).lowercase()
    }

    fun isValidImageFormat(extension: String): Boolean {
        return extension in listOf("jpg", "jpeg", "png", "gif", "bmp", "webp")
    }

    fun generateImageName(prefix: String = "report"): String {
        val timestamp = System.currentTimeMillis()
        return "${prefix}_${timestamp}.jpg"
    }

    fun bytesToBitmap(bytes: ByteArray): Bitmap? {
        return try {
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun bitmapToBytes(bitmap: Bitmap, format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(format, COMPRESSION_QUALITY, outputStream)
        return outputStream.toByteArray()
    }
}
```

### Paso 8.4: LocationUtils - Utilidades de ubicación
Explicación detallada: Herramientas para cálculos geográficos y procesamiento de ubicaciones.

```
package mx.edu.utng.alertavecinal.utils

/*
Clase LocationUtils: Este objeto proporciona funciones especializadas
para el cálculo y procesamiento de ubicaciones geográficas en la aplicación,
incluyendo cálculo de distancias, verificación de radios, conversión entre
formatos de ubicación, validación de coordenadas y cálculos de bounding boxes.
Sirve como capa de utilidad para todas las operaciones relacionadas con
geolocalización en el sistema.
*/

import android.location.Location
import com.google.android.gms.maps.model.LatLng

object LocationUtils {

    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]
    }

    fun isLocationInRadius(
        userLat: Double,
        userLon: Double,
        targetLat: Double,
        targetLon: Double,
        radiusMeters: Int
    ): Boolean {
        val distance = calculateDistance(userLat, userLon, targetLat, targetLon)
        return distance <= radiusMeters
    }

    fun calculateCenter(locations: List<LatLng>): LatLng {
        if (locations.isEmpty()) return LatLng(0.0, 0.0)

        var sumLat = 0.0
        var sumLng = 0.0

        locations.forEach { location ->
            sumLat += location.latitude
            sumLng += location.longitude
        }

        return LatLng(sumLat / locations.size, sumLng / locations.size)
    }

    fun locationToLatLng(location: Location): LatLng {
        return LatLng(location.latitude, location.longitude)
    }

    fun latLngToLocation(latLng: LatLng): Location {
        return Location("").apply {
            latitude = latLng.latitude
            longitude = latLng.longitude
        }
    }

    fun formatCoordinates(latitude: Double, longitude: Double): String {
        val latDirection = if (latitude >= 0) "N" else "S"
        val lonDirection = if (longitude >= 0) "E" else "O"

        return "%.6f°$latDirection, %.6f°$lonDirection".format(
            Math.abs(latitude),
            Math.abs(longitude)
        )
    }

    fun isValidCoordinates(latitude: Double, longitude: Double): Boolean {
        return latitude in -90.0..90.0 && longitude in -180.0..180.0
    }

    fun calculateZoomLevel(radiusMeters: Int): Float {
        return when {
            radiusMeters <= 500 -> 16f
            radiusMeters <= 1000 -> 15f
            radiusMeters <= 2000 -> 14f
            radiusMeters <= 5000 -> 13f
            else -> 12f
        }
    }

    fun getRadiusForAreaType(areaType: String): Int {
        return when (areaType) {
            "street" -> 200
            "neighborhood" -> 1000
            "district" -> 3000
            "city" -> 10000
            else -> 1000 // Por defecto
        }
    }

    fun calculateBoundingBox(
        centerLat: Double,
        centerLon: Double,
        radiusMeters: Int
    ): Pair<LatLng, LatLng> {
        val latDelta = (radiusMeters / 111320.0) // 1 grado ≈ 111.32 km
        val lonDelta = (radiusMeters / (111320.0 * Math.cos(Math.toRadians(centerLat))))

        val southwest = LatLng(centerLat - latDelta, centerLon - lonDelta)
        val northeast = LatLng(centerLat + latDelta, centerLon + lonDelta)

        return Pair(southwest, northeast)
    }
}
```

### Paso 8.5: NetworkUtils - Utilidades de red
Analogía: Como el monitor de tráfico de red de un administrador de sistemas.

```
package mx.edu.utng.alertavecinal.utils

/*
Clase NetworkUtils: Este objeto proporciona funciones para monitorear
y gestionar el estado de conectividad de red en la aplicación. Incluye verificación
de disponibilidad de internet, detección del tipo de conexión (WiFi, datos móviles),
registro de callbacks para cambios en la red y utilidades para simular condiciones
de red durante pruebas.
*/

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object NetworkUtils {

    private val _isNetworkAvailable = MutableStateFlow(true)
    val isNetworkAvailable: StateFlow<Boolean> = _isNetworkAvailable

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            capabilities != null && (
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                    )
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo
            networkInfo != null && networkInfo.isConnected
        }
    }

    fun registerNetworkCallback(context: Context) {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                _isNetworkAvailable.value = true
            }

            override fun onLost(network: Network) {
                _isNetworkAvailable.value = false
            }

            override fun onUnavailable() {
                _isNetworkAvailable.value = false
            }
        })
    }

    /**
     * Obtiene el tipo de conexión actual
     */
    fun getConnectionType(context: Context): String {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)

            when {
                capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true -> "WiFi"
                capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> "Datos móviles"
                capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true -> "Ethernet"
                else -> "Desconocido"
            }
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo
            when (networkInfo?.type) {
                ConnectivityManager.TYPE_WIFI -> "WiFi"
                ConnectivityManager.TYPE_MOBILE -> "Datos móviles"
                ConnectivityManager.TYPE_ETHERNET -> "Ethernet"
                else -> "Desconocido"
            }
        }
    }

    fun isConnectionGoodForUpload(context: Context): Boolean {
        val connectionType = getConnectionType(context)
        return connectionType == "WiFi" || connectionType == "Ethernet"
    }

    suspend fun simulateNetworkDelay() {
        kotlinx.coroutines.delay(1000L) // 1 segundo de delay simulado
    }
}
```

### Paso 8.6: NotificationUtils - Utilidades de notificaciones
Explicación detallada: Sistema completo para gestionar notificaciones push, canales y envíos.

```
package mx.edu.utng.alertavecinal.utils

/*
Clase NotificationUtils: Este objeto maneja toda la lógica relacionada con
notificaciones push en la aplicación, incluyendo la creación de canales de
notificación (para Android 8+), el envío de diferentes tipos de notificaciones
(aprobación/rechazo de reportes, alertas de incidentes cercanos) y la gestión
del estado de las notificaciones. Proporciona una API unificada para todas las
operaciones de notificación en el sistema.
*/

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
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

    // Permiso para notificaciones (requerido desde Android 13/API 33)
    private const val PERMISSION_POST_NOTIFICATIONS = android.Manifest.permission.POST_NOTIFICATIONS

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
     * Verifica si la aplicación tiene permiso para mostrar notificaciones
     */
    fun checkNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Para Android 13+ necesitamos verificar explícitamente el permiso
            ActivityCompat.checkSelfPermission(
                context,
                PERMISSION_POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // Para versiones anteriores, las notificaciones están habilitadas por defecto
            true
        }
    }

    fun showSimpleNotification(
        context: Context,
        title: String,
        message: String,
        channelId: String = CHANNEL_ID_GENERAL
    ) {
        // Verificar permiso antes de mostrar la notificación
        if (!checkNotificationPermission(context)) {
            // Opcional: Registrar el intento fallido o pedir el permiso
            return
        }

        val notificationId = getNextNotificationId()

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        try {
            with(NotificationManagerCompat.from(context)) {
                notify(notificationId, builder.build())
            }
        } catch (securityException: SecurityException) {
            // Manejar la excepción de seguridad
            // Podrías registrar el error o intentar solicitar el permiso
            println("Error de seguridad al mostrar notificación: ${securityException.message}")
        }
    }

    fun showReportApprovedNotification(context: Context, reportTitle: String) {
        if (!checkNotificationPermission(context)) {
            return
        }

        val notificationId = getNextNotificationId()

        val builder = NotificationCompat.Builder(context, CHANNEL_ID_REPORTS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Reporte Aprobado")
            .setContentText("Tu reporte '$reportTitle' ha sido aprobado")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        try {
            with(NotificationManagerCompat.from(context)) {
                notify(notificationId, builder.build())
            }
        } catch (securityException: SecurityException) {
            println("Error de seguridad al mostrar notificación de aprobación: ${securityException.message}")
        }
    }

    fun showReportRejectedNotification(context: Context, reportTitle: String, reason: String?) {
        if (!checkNotificationPermission(context)) {
            return
        }

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

        try {
            with(NotificationManagerCompat.from(context)) {
                notify(notificationId, builder.build())
            }
        } catch (securityException: SecurityException) {
            println("Error de seguridad al mostrar notificación de rechazo: ${securityException.message}")
        }
    }

    fun showNearbyIncidentAlert(
        context: Context,
        incidentType: String,
        distance: Float,
        address: String?
    ) {
        if (!checkNotificationPermission(context)) {
            return
        }

        val notificationId = getNextNotificationId()

        val distanceText = FormatUtils.formatDistance(distance)
        val locationText = address ?: "Cerca de tu ubicación"

        val builder = NotificationCompat.Builder(context, CHANNEL_ID_ALERTS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Incidente Cercano: $incidentType")
            .setContentText("A $distanceText - $locationText")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setTimeoutAfter(300000)

        try {
            with(NotificationManagerCompat.from(context)) {
                notify(notificationId, builder.build())
            }
        } catch (securityException: SecurityException) {
            println("Error de seguridad al mostrar alerta de incidente: ${securityException.message}")
        }
    }

    fun areNotificationsEnabled(context: Context): Boolean {
        return NotificationManagerCompat.from(context).areNotificationsEnabled() &&
                checkNotificationPermission(context)
    }

    private fun getNextNotificationId(): Int {
        return notificationId++.also {
            if (it > 9999) notificationId = 1000
        }
    }

    fun cancelNotification(context: Context, notificationId: Int) {
        NotificationManagerCompat.from(context).cancel(notificationId)
    }

    fun cancelAllNotifications(context: Context) {
        NotificationManagerCompat.from(context).cancelAll()
    }
}
```

