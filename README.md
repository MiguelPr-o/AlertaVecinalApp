# 📱 Alerta Vecinal
## Documentación de Arquitectura del Proyecto

Aplicación móvil para el **reporte y visualización de incidentes en tiempo real**, con enfoque comunitario, moderación y soporte offline.

---

## 🏗️ Arquitectura General

Esta sección presenta la arquitectura general del sistema.  
La aplicación sigue el patrón **MVVM + Repository**, lo que permite una **separación clara de responsabilidades**, facilitando el mantenimiento, escalabilidad y pruebas.

- **UI Layer** → Jetpack Compose (interfaz de usuario)  
- **Presentation Layer** → ViewModels (lógica de presentación)  
- **Domain Layer** → Modelos y estados (reglas de negocio)  
- **Data Layer** → Room + Firebase (persistencia y backend)  
- **DI Layer** → Dagger Hilt (inyección de dependencias)  

---

## 📂 Estructura del Proyecto

Esta sección muestra la **organización de paquetes del proyecto**, siguiendo buenas prácticas de desarrollo Android para mantener el código modular, ordenado y fácil de entender.

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
Esta capa se encarga del almacenamiento local de la información, permitiendo que la aplicación funcione incluso sin conexión a internet mediante el uso de Room Database.

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
La capa de dominio contiene los modelos principales del sistema, así como los estados de la UI y enumeraciones, representando las reglas de negocio de la aplicación.

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
Los repositorios actúan como una capa intermedia entre la UI y las fuentes de datos, abstrayendo el origen de la información (Room o Firebase).

| Repositorio | Función |
|------------|--------|
| AuthRepository | Autenticación |
| MapRepository | Ubicación y GPS |
| ReportRepository | Gestión de reportes |
| UserRepository | Gestión de usuarios |

---

## 💉 Inyección de Dependencias
Esta sección describe la configuración de Dagger Hilt, utilizada para proporcionar dependencias de manera automática y segura en toda la aplicación.

| Clase | Función |
|------|--------|
| AppModule | Configuración de Dagger Hilt |

---

## 🎨 Componentes UI (Compose)
Aquí se agrupan los componentes reutilizables de la interfaz, desarrollados con Jetpack Compose para mantener una UI consistente y modular.

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
Esta sección define la gestión de rutas y navegación entre pantallas, centralizando el flujo de la aplicación.

| Clase | Función |
|------|--------|
| AppNavigation | Rutas y navegación |

---

## 📱 Pantallas
Aquí se listan todas las pantallas principales de la aplicación, tanto para usuarios como para moderadores.

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
Esta sección contiene clases auxiliares que apoyan distintas funcionalidades del sistema como formato, red, ubicación e imágenes.

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
Listado de las tecnologías principales empleadas en el desarrollo del proyecto.

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
Este diagrama representa el flujo de comunicación entre capas dentro de la aplicación.

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
Funciones disponibles para usuarios finales.
- Reporte de incidentes en tiempo real
- Mapa interactivo
- Notificaciones cercanas
- Perfil con historial

### 🛡️ Moderadores
Funciones exclusivas para moderadores.
- Panel de control
- Revisión de reportes
- Estadísticas
- Edición de reportes

### 🔧 Técnicas
Características técnicas del sistema.
- Sincronización Firebase ↔ Room
- Funcionalidad offline
- Material Design 3
- Autenticación segura

---

## 🔐 Permisos
Permisos requeridos por la aplicación para su correcto funcionamiento.

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
Esta sección presenta un resumen cuantitativo del tamaño y alcance del proyecto.

- **Clases:** 52  
- **Líneas de código:** ~8,000  
- **Pantallas:** 11  
- **Componentes reutilizables:** 9  
- **Repositorios:** 4  
- **Utilidades:** 6  

---

## 🎯 Patrones de Diseño Implementados
Patrones utilizados para mejorar la calidad y mantenibilidad del código.

- **MVVM**  
- **Repository**  
- **Singleton**  
- **Factory (Dagger Hilt)**  
- **Observer (StateFlow)**  

---

## 🔥 Configuración de Firebase
Configuración del backend en Firebase utilizado por la aplicación.

- **Authentication:** Email / Password  
- **Firestore:** `users`, `reports`, `notifications`  
- **Storage:** Imágenes de reportes  
- **Rules:** Seguridad basada en roles  

---

