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