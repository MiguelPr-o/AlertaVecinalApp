📱# Alerta Vecinal - Documentación de Arquitectura
🏗️ ## Estructura del Proyecto
📂 Capa de Datos Local (Room Database)
Clase	Descripción	Responsabilidad
AppDatabase	Base de datos principal Room	Configuración y acceso a la BD
Converters	Convertidores de tipos para Room	Conversión de tipos complejos
NotificationDao	Operaciones con notificaciones	CRUD de notificaciones
NotificationEntity	Entidad de notificaciones	Estructura tabla notifications
ReportDao	Operaciones con reportes	CRUD y consultas de reportes
ReportEntity	Entidad de reportes	Estructura tabla reports
UserDao	Operaciones con usuarios	CRUD de usuarios
UserEntity	Entidad de usuarios	Estructura tabla users
📦 Capa de Modelos (Domain Layer)
Clase	Descripción	Responsabilidad
Enums.kt	Enumerados del sistema	Constantes y tipos enumerados
LocationData	Datos de ubicación	Representación de coordenadas
LocationSelectionState	Estado de selección de ubicación	Gestión de ubicación UI
MapState	Estado del mapa	Estado del componente mapa
NotificationPrefs	Preferencias de notificación	Configuración de notificaciones
Report	Modelo de dominio de reporte	Lógica de negocio reportes
ReportState	Estado de reportes	Estado UI de reportes
UiState	Estados genéricos de UI	Patrón de estados UI
AuthState	Estado de autenticación	Estado de autenticación
User	Modelo de dominio de usuario	Lógica de negocio usuarios
🔄 Capa de Repositorios
Clase	Descripción	Responsabilidad
AuthRepository	Repositorio de autenticación	Login, registro, logout
MapRepository	Repositorio de mapas/ubicación	Gestión de ubicación GPS
ReportRepository	Repositorio de reportes	Operaciones con reportes
UserRepository	Repositorio de usuarios	Operaciones con usuarios
💉 Inyección de Dependencias
Clase	Descripción	Responsabilidad
AppModule	Módulo principal Dagger Hilt	Configuración de DI
🎨 Componentes de UI (Compose)
Clase	Descripción	Responsabilidad
CustomButtons	Botones personalizados	Componentes de botón reutilizables
CustomTextField	Campos de texto personalizados	Inputs de formulario
EmptyState	Estados vacíos	Componentes para datos vacíos
ErrorMessage	Mensajes de error	Mostrar errores al usuario
IncidentMarker	Marcadores de mapa	Marcadores personalizados en mapa
LoadingIndicator	Indicadores de carga	Spinners y loaders
ModeratorReportCard	Tarjetas para moderador	Tarjetas especiales moderación
ReportActionsModal	Modales de acciones	Diálogos de moderación
ReportFilter	Filtros de reportes	Componente de filtrado
🧭 Navegación
Clase	Descripción	Responsabilidad
AppNavigation	Navegación principal	Gestión de rutas y navegación
📱 Pantallas (Screens)
Clase	Descripción	Responsabilidad
CreateReportScreen	Crear reporte	Formulario de creación
LoginScreen	Inicio de sesión	Autenticación de usuarios
MapScreen	Mapa principal	Vista de mapa con incidentes
ModeratorDashboardScreen	Panel de moderador	Dashboard para moderadores
ModeratorReportReviewScreen	Revisión de reportes	Pantalla de moderación detallada
PendingReportsScreen	Reportes pendientes	Lista de reportes por revisar
ProfileScreen	Perfil de usuario	Perfil y configuración
RegisterScreen	Registro	Creación de cuenta
ReportDetailScreen	Detalles de reporte	Vista detallada de reporte
SelectLocationScreen	Selección de ubicación	Mapa para elegir ubicación
WelcomeScreen	Pantalla de bienvenida	Pantalla inicial
🔧 Utilidades (Utils)
Clase	Descripción	Responsabilidad
Constants	Constantes globales	Configuración y constantes
FormatUtils	Utilidades de formato	Formateo de fechas, textos
ImageUtils	Utilidades de imágenes	Procesamiento de imágenes
LocationUtils	Utilidades de ubicación	Cálculos geográficos
NetworkUtils	Utilidades de red	Gestión de conectividad
NotificationUtils	Utilidades de notificaciones	Gestión de notificaciones push
🛠️ Tecnologías Utilizadas
Tecnología	Versión	Uso
Kotlin	1.9+	Lenguaje principal
Jetpack Compose	1.5+	UI declarativa
Room	2.6+	Base de datos local
Firebase	32.0+	Backend (Auth, Firestore, Storage)
Dagger Hilt	2.48+	Inyección de dependencias
Coroutines	1.7+	Programación asíncrona
Google Maps	18.2+	Mapas y ubicación
Coil	2.4+	Carga de imágenes
📊 Diagrama de Arquitectura
text
┌─────────────────────────────────────────────────┐
│                 UI Layer (Compose)               │
│  ┌─────────┐ ┌─────────┐ ┌─────────┐           │
│  │ Screens │ │Componen-│ │Navigation│           │
│  │ (11)    │ │ ts (9)  │ │   (1)    │           │
│  └─────────┘ └─────────┘ └─────────┘           │
└─────────────────┬───────────────────────────────┘
                  │ ViewModel Calls
┌─────────────────────────────────────────────────┐
│             Presentation Layer                   │
│  ┌─────────────────────────────────────────┐    │
│  │           ViewModels                    │    │
│  │  (Auth, Report, Map, User, Moderator)  │    │
│  └─────────────────────────────────────────┘    │
└─────────────────┬───────────────────────────────┘
                  │ Repository Calls
┌─────────────────────────────────────────────────┐
│              Domain Layer                        │
│  ┌─────────┐ ┌─────────┐ ┌─────────┐           │
│  │ Models  │ │Enums    │ │ States  │           │
│  │ (10)    │ │ (1)     │ │ (4)     │           │
│  └─────────┘ └─────────┘ └─────────┘           │
└─────────────────┬───────────────────────────────┘
                  │ Data Operations
┌─────────────────────────────────────────────────┐
│              Data Layer                          │
│  ┌─────────┐ ┌─────────┐ ┌─────────┐           │
│  │Repositor│ │ Room    │ │ Firebase│           │
│  │ ies (4) │ │ (8)     │ │   -     │           │
│  └─────────┘ └─────────┘ └─────────┘           │
└─────────────────┬───────────────────────────────┘
                  │ DI Configuration
┌─────────────────────────────────────────────────┐
│           Dependency Injection                   │
│  ┌─────────────────────────────────────────┐    │
│  │           AppModule (1)                 │    │
│  │  (Hilt Module with all dependencies)    │    │
│  └─────────────────────────────────────────┘    │
└─────────────────────────────────────────────────┘
🚀 Características Principales
👥 Para Usuarios
📍 Reporte de incidentes en tiempo real

🗺️ Visualización en mapa interactivo

🔔 Notificaciones de incidentes cercanos

👤 Perfil personalizado con historial

🛡️ Para Moderadores
📋 Panel de control dedicado

⚡ Revisión y aprobación de reportes

📊 Estadísticas y métricas

✏️ Edición de reportes existentes

🔧 Técnicas
🔄 Sincronización bidireccional (Firebase ↔ Room)

📱 Funcionalidad offline completa

🎨 UI moderna con Material Design 3

🔐 Autenticación segura con Firebase Auth

📁 Estructura de Paquetes
text
mx.edu.utng.alertavecinal/
├── data/
│   ├── local/          # Room database entities & DAOs
│   ├── model/          # Domain models & enums
│   └── repository/     # Repository implementations
├── di/                 # Dependency injection
├── ui/
│   ├── components/     # Reusable UI components
│   ├── navigation/     # Navigation configuration
│   └── screens/        # All application screens
├── utils/              # Utility classes
└── viewmodel/          # ViewModels
🔐 Permisos Requeridos
xml
<!-- AndroidManifest.xml -->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
📈 Métricas del Proyecto
Total de clases: 52

Líneas de código aproximadas: ~8,000

Pantallas principales: 11

Componentes reutilizables: 9

Repositorios: 4

Utilidades: 6

🎯 Patrones de Diseño Implementados
Patrón	Implementación	Beneficio
MVVM	View + ViewModel + Model	Separación de responsabilidades
Repository	Repositorios por entidad	Abstracción de fuente de datos
Singleton	AppDatabase, ViewModels	Una instancia global
Factory	Dagger Hilt modules	Inyección de dependencias
Observer	StateFlow/LiveData	Actualización reactiva de UI
🛡️ Consideraciones de Seguridad
Autenticación: Firebase Authentication con email/password

Autorización: Roles de usuario (Usuario, Moderador, Admin)

Validación: Validación en cliente y servidor

Permisos: Solicitud granular de permisos en runtime

Cifrado: Room encryption disponible si se requiere

📲 Configuración de Firebase
Authentication: Habilitado (Email/Password)

Firestore: Colecciones: users, reports, notifications

Storage: Bucket para imágenes de reportes

Rules: Configuración segura por roles


