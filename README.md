# VORA Coach — Parcial TP3

App Android (Kotlin + Jetpack Compose) basada en el diseño de VORA Coach.
Desarrollo de Aplicaciones Móviles — ORT 2026.

**Figma:** https://www.figma.com/design/1nvbbfYNYmKAevuaG28J85/Parcial-Domiciliario-7-Oct-26
**API:** https://tp3.martinrivas.ar/

## Integrantes
- Daiana Gimenez Torrico
- Francesca Rocío Massollo
- Nahuel Ezequiel Di Paolo

## Cómo ejecutar
Abrir en Android Studio, sincronizar Gradle y ejecutar `app` (API 24+).

### Servidor de la API
Por defecto la app usa la API publicada: `https://tp3.martinrivas.ar/`.

Para usar el mock local del paquete del docente:
1. Con Python 3.9 o posterior, desde la carpeta del paquete: `python mock/server.py` (en macOS/Linux, `python3`).
2. Verificar que responde en `http://localhost:8000/`.
3. Agregar en `local.properties` (no se sube al repositorio):
   ```
   vora.apiUrl=http://10.0.2.2:8000/
   ```
   `10.0.2.2` es la dirección de la compu vista desde el emulador.
4. Sincronizar Gradle y volver a ejecutar la app.

La conexión `http` (sin cifrar) se permite solo en la versión debug y solo hacia `10.0.2.2` y `localhost`.

## Estructura del proyecto
Paquete base: `app/src/main/java/com/example/voracoach/`

| Carpeta | Contenido |
|---|---|
| `navigation/` | Rutas (`AppDestination`), `AppNavHost`, barra inferior y pantalla provisoria |
| `ui/theme/` | Colores, tipografía y tema de la app |
| `ui/components/` | Componentes reutilizables (botones, tarjetas, campos) |
| `ui/screens/<área>/` | Pantallas de cada flujo: `auth`, `home`, `coach`, `workout`, `nutrition`, `progress`, `profile` |
| `viewmodel/` | ViewModels con el estado de cada pantalla |
| `data/remote/` | Retrofit: interfaz de la API y DTO |
| `data/local/` | Room: Entity, DAO y base de datos |
| `data/repository/` | Repositorios: única fuente de datos para los ViewModels |
| `model/` | Modelos que usa la app |
| `di/` | Módulos de Hilt que proveen Retrofit, Room y los DAO |

## Navegación
- La app arranca en **Sign In**. Al entrar se borra el acceso del historial: el botón atrás desde Home cierra la app.
- **Barra inferior** con 5 pestañas: Home, Workout, Nutrition, Progress y Profile. Cambiar de pestaña no apila pantallas: el botón atrás desde cualquiera vuelve a Home.
- La barra solo se muestra en las pestañas. Sign In, Sign Up, Notifications, Coach y las sub-pantallas (detalle, agregar, etc.) ocupan la pantalla completa.
- Las rutas que todavía no tienen pantalla real muestran una pantalla provisoria, para que todo el recorrido sea navegable.

### Decisiones de navegación (el Figma no define estos destinos)
Se eligieron según el contenido de cada pantalla:
- **Progress / 01 — Today's Overview:** se abre desde "View details" en la sección "Today's Overview" de Home. Tiene botón atrás porque no es una pestaña.
- **Progress / 02 — Dashboard:** "View" de Workout, Nutrition y Activity abre Workout Progress, Nutrition Progress y Health Trends; "See weekly review" abre Weekly Review.
- **Progress / 06 — Weekly Review:** "Get dinner ideas" lleva a la pestaña Nutrition, "View next week's plan" a Weekly Plan, "Ask VORA" a Coach y "Done" vuelve al Dashboard.
- Las sub-pantallas de Progress se muestran sin barra inferior, como las de Workout.
- El botón "•••" de las sub-pantallas de Progress no tiene menú en el Figma: muestra un aviso de función de demostración.

### Datos compartidos entre pantallas
- El nombre del perfil se ve en Home y los objetivos en Progress (Dashboard y Today's Overview). Los dos se leen de `ProfileRepository`: si se editan en Profile, se actualizan solos.
- El entrenamiento recomendado de Home y de Today's Overview es el primero del catálogo guardado en Room.
- El período elegido (Week, Month, 3 Months) y el macro de Nutrition Progress se guardan en el ViewModel, así que sobreviven a la rotación.

## Inventario de pantallas
🎭 Demostración: funciona sin servicio real · 🚧 Provisoria: ruta creada, pantalla en construcción · ⏳ Pendiente: sin ruta todavía · ✅ Funcional: con Retrofit o Room

### Acceso
| Pantalla (Figma) | Ruta | Estado |
|---|---|---|
| Login - 01 — SignIn | `sign_in` | 🎭 Demostración |
| Sign Up | `sign_up` | 🎭 Demostración |

### Home
| Pantalla (Figma) | Ruta | Estado |
|---|---|---|
| Home / 01 — Dashboard | `home` | 🎭 Demostración (métricas de ejemplo; nombre del perfil; entrenamiento recomendado real desde Room) |
| Home / 02 — Notifications | `notifications` | 🎭 Demostración (notificaciones de ejemplo; "Mark all as read" funcional) |

### Asistente VORA
Una pantalla con 6 estados simulados.

| Pantalla (Figma) | Ruta | Estado |
|---|---|---|
| Voice / 01 — Idle | `coach` | 🎭 Demostración |
| Voice / 02 — Listening | `coach` | 🎭 Demostración (transcripción simulada) |
| Voice / 03 — Processing | `coach` | 🎭 Demostración |
| Voice / 04 — Speaking | `coach` | 🎭 Demostración (respuesta y reproducción simuladas) |
| Voice / 05 — Conversation | `coach` | 🎭 Demostración (historial de la sesión; respuestas de ejemplo) |
| Voice / 06 — Error | `coach` | 🎭 Demostración (se llega tocando el mic antes de terminar la escucha; no pide permisos) |

### Workout
| Pantalla (Figma) | Ruta | Estado |
|---|---|---|
| Workout / 01 — Dashboard | `workout` | ✅ Funcional (catálogo con Retrofit y caché en Room) |
| Workout / 02 — Weekly Plan | `workout_weekly` | 🎭 Demostración (plan armado con el catálogo real; sugerencias simuladas) |
| Workout / 03 — Workout Details | `workout/{workoutId}` | ✅ Funcional (detalle con Retrofit y caché en Room) |
| Workout / 04 — Pre Workout | `workout/{workoutId}/pre` | 🎭 Demostración (nombre, duración y consejo del entrenamiento real) |
| Workout / 05 — Active Workout | `workout/{workoutId}/active` | 🎭 Demostración (ejercicios reales; series, peso y descanso simulados) |
| Workout / 06 — Exercise Details | `workout/{workoutId}/exercise/{exerciseId}` | 🎭 Demostración (instrucciones y consejo reales del ejercicio) |
| Workout / 07 — Workout Complete | `workout/{workoutId}/complete` | 🎭 Demostración (duración y ejercicios reales; volumen e insight de ejemplo) |

### Nutrition
| Pantalla (Figma) | Ruta | Estado |
|---|---|---|
| Nutrition / 01 — Dashboard | `nutrition` | 🚧 Provisoria |
| Nutrition / 02 — Today's Meals | a definir | ⏳ Pendiente |
| Nutrition / 03 — Add Food | a definir | ⏳ Pendiente |
| Nutrition / 04 — Voice Food Logging | a definir | ⏳ Pendiente |
| Nutrition / 05 — Food Analysis | a definir | ⏳ Pendiente |
| Nutrition / 06 — Photo Analysis | a definir | ⏳ Pendiente |
| Nutrition / 07 — Food Confirmation | a definir | ⏳ Pendiente |
| Nutrition / 08 — Meal Details | a definir | ⏳ Pendiente |

### Progress
| Pantalla (Figma) | Ruta | Estado |
|---|---|---|
| Progress / 01 — Today's Overview | `progress_today` | 🎭 Demostración (métricas del día de ejemplo; entrenamiento real desde Room; objetivo y meta semanal del perfil) |
| Progress / 02 — Dashboard | `progress` | 🎭 Demostración (métricas de ejemplo por período; "Active goal" del perfil) |
| Progress / 03 — Workout Progress | `progress_workout` | 🎭 Demostración (volumen y ejercicios de ejemplo por período) |
| Progress / 04 — Nutrition Progress | `progress_nutrition` | 🎭 Demostración (objetivos por macro y hábitos de ejemplo) |
| Progress / 05 — Health Trends | `progress_health` | 🎭 Demostración (pasos y señales de hábito de ejemplo) |
| Progress / 06 — Weekly Review | `progress_weekly_review` | 🎭 Demostración (revisión de ejemplo; accesos reales a Nutrition, Weekly Plan y Coach) |

### Profile
| Pantalla (Figma) | Ruta | Estado |
|---|---|---|
| Profile / 01 — Dashboard | `profile` | 🚧 Provisoria |
| Profile / 02 — Edit Profile | a definir | ⏳ Pendiente |
| Profile / 03 — Goals & Preferences | a definir | ⏳ Pendiente |
| Profile / 04 — Nutrition Preferences | a definir | ⏳ Pendiente |
| Profile / 05 — Voice & AI Settings | a definir | ⏳ Pendiente |
| Profile / 06 — Settings | a definir | ⏳ Pendiente |

### Funciones simuladas del asistente
El asistente no usa reconocimiento de voz, IA ni audio reales, y nunca pide permiso de micrófono ni de cámara:
- **Escucha:** muestra una frase de ejemplo palabra por palabra. Tocar el mic antes de que termine lleva al estado de error.
- **Respuestas:** cada pregunta sugerida tiene una respuesta fija; lo que se escribe recibe respuestas de ejemplo que se van alternando.
- **Reproducción:** la barra de progreso avanza con un temporizador; pausa, repetir y detener funcionan sobre ese temporizador.
- **"Allow Access" y adjuntar (+):** muestran un aviso de función de demostración.
- La conversación vive en el ViewModel: sobrevive a la rotación, pero se pierde al salir de Coach.

**Para actualizar el inventario:** al agregar una pantalla, completar su ruta y estado en esta tabla en el mismo PR.

## Uso de IA
Se utilizó Claude como asistente para la configuración inicial del proyecto y como apoyo en la navegación y las pantallas de acceso (explicación de conceptos, revisión de errores y propuestas de código, revisadas y adaptadas por el equipo).
