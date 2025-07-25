# POLIBET

**Aplicación de Pronósticos Deportivos Académicos**

Una aplicación móvil desarrollada en **Jetpack Compose** para Android que permite a los usuarios realizar pronósticos deportivos virtuales con fines educativos. Este proyecto forma parte de la materia de Aplicaciones Móviles de la Escuela Politécnica Nacional.

## 📋 Tabla de Contenidos

- [Descripción del Proyecto](#descripción-del-proyecto)
- [Características Principales](#características-principales)
- [Arquitectura del Proyecto](#arquitectura-del-proyecto)
- [Implementación de Navigation](#implementación-de-navigation)
- [Tecnologías Utilizadas](#tecnologías-utilizadas)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Instalación y Configuración](#instalación-y-configuración)
- [Uso de la Aplicación](#uso-de-la-aplicación)
- [Contribución](#contribución)

## Descripción del Proyecto

POLIBET es una aplicación movil que simula una plataforma de pronósticos deportivos donde los estudiantes pueden:

- Registrarse y gestionar un balance virtual inicial de $1000
- Explorar eventos deportivos de múltiples categorías (Fútbol, Baloncesto, Tenis, Voleibol, Béisbol, Boxeo)
- Realizar pronósticos con dinero virtual
- Hacer seguimiento de sus estadísticas y rendimiento
- Gestionar su perfil y configuraciones

**⚠️ Importante**: Esta aplicación es únicamente para fines académicos y educativos. No involucra dinero real ni promueve apuestas reales.

##  Características Principales

###  Sistema de Autenticación
- Registro de nuevos usuarios con validación
- Login seguro con usuarios de prueba
- Gestión de sesiones y estados de autenticación

###  Gestión de Deportes y Eventos
- 6 categorías deportivas disponibles
- Eventos en tiempo real y programados
- Sistema de cuotas dinámicas
- Información detallada de cada evento

###  Sistema de Pronósticos
- Creación de pronósticos con validación de fondos
- Apuestas rápidas desde el dashboard
- Resolución automática de apuestas (simulada)
- Gestión de balance en tiempo real

###  Estadísticas y Seguimiento
- Dashboard con resumen de actividad
- Historial completo de pronósticos
- Estadísticas de rendimiento (tasa de éxito, ganancias, etc.)
- Cancelación de apuestas pendientes

##  Arquitectura del Proyecto

El proyecto sigue el patrón **MVVM (Model-View-ViewModel)** con las siguientes capas:

\`\`\`
app/
├── data/
│   ├── models/          # Modelos de datos (User, Event, Prediction, etc.)
│   └── repository/      # Repositorios para manejo de datos
├── ui/
│   ├── components/      # Componentes reutilizables de UI
│   ├── screens/         # Pantallas de la aplicación
│   ├── theme/          # Configuración de tema y estilos
│   └── viewmodels/     # ViewModels para lógica de negocio
└── navigation/         # Configuración de navegación
\`\`\`

##  Implementación de Navigation

### Tema de Investigación: Navigation Component en Jetpack Compose

La navegación es uno de los aspectos más críticos en el desarrollo de aplicaciones móviles. En este proyecto, se implementó el **Navigation Component** de Jetpack Compose para gestionar la navegación entre pantallas de manera declarativa y type-safe.

### 🔧 Configuración Base

#### 1. Dependencias Necesarias

```kotlin
// build.gradle (Module: app)
dependencies {
    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.7.6")
    
    // ViewModel Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
}
```
2. Estructura de Navegación
El sistema de navegación se organiza en los siguientes archivos:
```
navigation/
└── PoliBetNavigation.kt    # Configuración principal de navegación
```

Definición de Rutas:
```
object PoliBetDestinations {
    const val LOGIN_ROUTE = "login"
    const val REGISTER_ROUTE = "register"
    const val DASHBOARD_ROUTE = "dashboard"
    const val SPORTS_ROUTE = "sports"
    const val EVENT_DETAIL_ROUTE = "event_detail"
    const val PREDICTIONS_ROUTE = "predictions"
    const val PROFILE_ROUTE = "profile"
}
```
### Flujo de Uso Típico

1. Iniciar Sesión con uno de los usuarios de prueba
2. Explorar el Dashboard para ver eventos destacados y estadísticas
3. Navegar por Deportes para encontrar eventos específicos
4. Crear Pronósticos seleccionando eventos y cuotas
5. Revisar Historial en la sección "Mis Pronósticos"
6. Gestionar Perfil y configuraciones