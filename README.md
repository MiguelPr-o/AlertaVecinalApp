## 📱 Alerta Vecinal
# Documentación de Arquitectura del Proyecto

Aplicación móvil para el **reporte y visualización de incidentes en tiempo real**, con enfoque comunitario, moderación y soporte offline.

---

## 🏗️ Arquitectura General

La aplicación sigue una arquitectura **MVVM + Repository**, garantizando una correcta separación de responsabilidades:

- **UI Layer** → Jetpack Compose  
- **Presentation Layer** → ViewModels  
- **Domain Layer** → Modelos y estados  
- **Data Layer** → Room + Firebase  
- **DI Layer** → Dagger Hilt  

---

## 📂 Estructura del Proyecto

```mx.edu.utng.alertavecinal/
├── data/
│ ├── local/ # Room database, DAOs y entidades
│ ├── model/ # Modelos de dominio y enums
│ └── repository/ # Implementaciones de repositorios
├── di/ # Inyección de dependencias (Hilt)
├── ui/
│ ├── components/ # Componentes reutilizables
│ ├── navigation/ # Configuración de navegación
│ └── screens/ # Pantallas de la aplicación
├── utils/ # Utilidades generales
└── viewmodel/ # ViewModels
```

---

## 📂 Capa de Datos Local (Room)

| Clase | Descripción | Responsabilidad |
|------|------------|----------------|
| AppDatabase | Base de datos Room | Configuración y acceso |
| Converters | Convertidores de tipos | Manejo de tipos complejos |
| NotificationDao | DAO de notificaciones | CRUD de notificaciones |
| NotificationEntity | Entidad de notificaciones | Tabla `notifications` |
| ReportDao | DAO de reportes | CRUD y consultas |
| ReportEntity | Entidad de reportes | Tabla `reports` |
| UserDao | DAO de usuarios | CRUD de usuarios |
| UserEntity | Entidad de usuarios | Tabla `users` |

---

## 📦 Capa de Modelos (Domain Layer)

| Clase | Descripción |
|------|------------|
| Enums.kt | Enumeraciones del sistema |
| LocationData | Datos de ubicación |
| LocationSelectionState | Estado de selección |
| MapState | Estado del mapa |
| NotificationPrefs | Preferencias |
| Report | Modelo de dominio |
| ReportState | Estado de reportes |
| UiState | Estados genéricos |
| AuthState | Estado de autenticación |
| User | Modelo de usuario |

---

## 🔄 Repositorios

| Repositorio | Función |
|------------|--------|
| AuthRepository | Autenticación |
| MapRepository | Ubicación y GPS |
| ReportRepository | Gestión de reportes |
| UserRepository | Gestión de usuarios |

---

## 💉 Inyección de Dependencias

| Clase | Función |
|------|--------|
| AppModule | Configuración de Dagger Hilt |

---

## 🎨 Componentes UI (Compose)

| Componente | Función |
|-----------|--------|
| CustomButtons | Botones reutilizables |
| CustomTextField | Campos de texto |
| EmptyState | Estados vacíos |
| ErrorMessage | Mensajes de error |
| IncidentMarker | Marcadores en mapa |
| LoadingIndicator | Indicadores de carga |
| ModeratorReportCard | Tarjetas de moderación |
| ReportActionsModal | Modales |
| ReportFilter | Filtros |

---

## 🧭 Navegación

| Clase | Función |
|------|--------|
| AppNavigation | Rutas y navegación |

---

## 📱 Pantallas

| Pantalla | Función |
|---------|--------|
| WelcomeScreen | Pantalla inicial |
| LoginScreen | Inicio de sesión |
| RegisterScreen | Registro |
| MapScreen | Mapa principal |
| CreateReportScreen | Crear reporte |
| ReportDetailScreen | Detalles |
| SelectLocationScreen | Selección de ubicación |
| ProfileScreen | Perfil |
| ModeratorDashboardScreen | Panel de moderador |
| PendingReportsScreen | Reportes pendientes |
| ModeratorReportReviewScreen | Revisión de reportes |

---

## 🔧 Utilidades

| Clase | Función |
|------|--------|
| Constants | Constantes globales |
| FormatUtils | Formateo |
| ImageUtils | Imágenes |
| LocationUtils | Ubicación |
| NetworkUtils | Conectividad |
| NotificationUtils | Notificaciones |

---

## 🛠️ Tecnologías Utilizadas

| Tecnología | Uso |
|-----------|----|
| Kotlin | Lenguaje principal |
| Jetpack Compose | UI declarativa |
| Room | Base de datos local |
| Firebase | Auth, Firestore, Storage |
| Dagger Hilt | Inyección de dependencias |
| Coroutines | Asincronía |
| Google Maps | Mapas |
| Coil | Carga de imágenes |

---

## 📊 Diagrama de Arquitectura

UI (Compose)
   ↓
ViewModels (Presentation)
   ↓
Domain (Models & States)
   ↓
Repositories
   ↓
Room Database / Firebase

---

## 🚀 Características

### 👥 Usuarios
- Reporte de incidentes en tiempo real
- Mapa interactivo
- Notificaciones cercanas
- Perfil con historial

### 🛡️ Moderadores
- Panel de control
- Revisión de reportes
- Estadísticas
- Edición de reportes

### 🔧 Técnicas
- Sincronización Firebase ↔ Room
- Funcionalidad offline
- Material Design 3
- Autenticación segura

---

## 🔐 Permisos

```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

---

## 📈 Métricas del Proyecto

- **Clases:** 52  
- **Líneas de código:** ~8,000  
- **Pantallas:** 11  
- **Componentes reutilizables:** 9  
- **Repositorios:** 4  
- **Utilidades:** 6  

---

## 🎯 Patrones de Diseño Implementados

- **MVVM**  
- **Repository**  
- **Singleton**  
- **Factory (Dagger Hilt)**  
- **Observer (StateFlow)**  

---

## 🔥 Configuración de Firebase

- **Authentication:** Email / Password  
- **Firestore:** `users`, `reports`, `notifications`  
- **Storage:** Imágenes de reportes  
- **Rules:** Seguridad basada en roles  

---

