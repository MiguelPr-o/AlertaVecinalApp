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