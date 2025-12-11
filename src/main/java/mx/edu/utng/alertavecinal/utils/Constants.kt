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