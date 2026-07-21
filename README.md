# Mi Pelu (Android)

App Android nativa para peluqueras/estilistas independientes: historial técnico de cada clienta,
fórmulas, productos usados, diagnóstico de cabello, fotos de antes/después y notas profesionales.
No es una app de turnos, agenda, pagos ni inventario — el foco es la memoria profesional del
trabajo con cada clienta.

Backend: [`api-mipelu`](../backs/api-mipelu) (NestJS + PostgreSQL + Prisma), en un repo aparte.

## Stack

- Kotlin + Jetpack Compose (Material 3)
- Hilt (inyección de dependencias)
- Navigation Compose
- Retrofit + OkHttp + `kotlinx.serialization` (networking)
- `androidx.security` (`EncryptedSharedPreferences`) para persistir la sesión
- Coil (carga de imágenes)

## Arquitectura

Capas separadas por responsabilidad, sin frameworks de estado adicionales (todo con
`Flow`/`StateFlow` de Kotlin coroutines):

```
domain/
  model/        Client, WorkRecord, UserProfile, ServiceType, PhotoType — Kotlin puro, sin
                 dependencias de Android ni de la capa de red.
  repository/   Interfaces (AuthRepository, ClientRepository, WorkRecordRepository,
                 UserProfileRepository) que consumen las pantallas/ViewModels.

data/
  remote/       Implementación real: DTOs (kotlinx.serialization), MiPeluApi (Retrofit),
                 TokenStore + AuthInterceptor + TokenAuthenticator (sesión y refresh
                 automático), mappers DTO↔domain, y los Remote*Repository (uno por interfaz
                 de dominio) — bindeados en RepositoryModule.
  local/fake/    Implementaciones en memoria (Fake*Repository) usadas antes de conectar el
                 backend. Quedan en el código sin bindear, útiles para tests/previews.
  di/            Módulos Hilt: RepositoryModule (elige Remote* vs Fake*), NetworkModule,
                 CoroutineScopeModule.

feature/        Una carpeta por feature (auth, home, clients, workrecords, settings), cada una
                 con su(s) Screen composable y ViewModel (@HiltViewModel).

core/           Tema, componentes de UI reutilizables, navegación y utilidades compartidas.
```

Los `Remote*Repository` mantienen internamente una caché en `MutableStateFlow` poblada con
llamadas suspend a la API, exponiendo el mismo contrato reactivo (`observeX(): Flow<...>`) que
tenían los `Fake*Repository` — así que las pantallas/ViewModels no necesitan saber si están
hablando con datos locales o con el backend real.

## Cómo correr el proyecto

1. Backend corriendo localmente (ver README de `api-mipelu`):
   ```bash
   cd ../backs/api-mipelu
   docker compose up -d
   npm install
   npx prisma migrate dev
   npm run start:dev
   ```
2. Abrir este proyecto en Android Studio y correrlo en un **emulador** (build variant `debug`).
   La URL base ya apunta a `http://10.0.2.2:3000/`, el alias que usa el emulador para llegar al
   `localhost` de la PC — no requiere configuración adicional.
3. Para probar en un **dispositivo físico** en la misma red, cambiar `API_BASE_URL` en
   `app/build.gradle.kts` (bloque `buildTypes { debug { ... } }`) por la IP LAN de la PC, por
   ejemplo `http://192.168.1.50:3000/`.

El build `debug` permite tráfico HTTP sin TLS sólo hacia `10.0.2.2`/`localhost`/`127.0.0.1`
(`app/src/debug/res/xml/network_security_config_debug.xml`) — el build `release` no lo permite y
apunta a un placeholder (`API_BASE_URL` en el bloque `release`) que hay que reemplazar por la URL
real una vez que el backend esté deployado.

## Estado actual / limitaciones conocidas

- **Manejo de errores de red**: `AuthViewModel` sí muestra errores (login/signup usan
  `Result<UserProfile>`), pero las pantallas de alta/edición de clientas y trabajos
  (`NewClientViewModel`, `NewWorkViewModel`) todavía no atrapan fallas de red — una desconexión
  al guardar puede terminar en un crash de pantalla en vez de un mensaje. Pendiente: replicar el
  patrón `isLoading`/`errorMessage` de `AuthViewModel`.
- **Refresh de sesión fallido**: si el refresh token vencido/revocado no puede renovarse,
  `TokenAuthenticator` limpia los tokens guardados pero no fuerza a `AuthRepository.currentUser`
  a pasar a `null` de inmediato — el usuario queda "logueado" en la UI hasta el próximo 401 o
  reinicio de la app.
- **Listados**: `GET /clients` y `GET /work-records` están paginados en el backend (tope 100 por
  página); el repositorio trae una sola página y no repagina — suficiente para una profesional
  independiente, no para cientos de registros.
- **Precio**: sigue siendo un campo de texto libre en la UI (`WorkRecord.price: String`); se
  convierte a número para el backend con una heurística simple (`.` separador de miles,
  `,` decimal, estilo es-AR), no es un parser de moneda completo.
- **Sin foto de perfil**: `UserProfile.photoUrl` siempre es `null` — el backend todavía no tiene
  concepto de avatar de la profesional.
