# Documentación de Integración de Stripe en Android

## Resumen

Se ha implementado un sistema completo de suscripciones con Stripe en la aplicación Android de Netflix. El sistema verifica si el usuario tiene una suscripción activa después de seleccionar su perfil, y si no la tiene, muestra una pantalla de suscripción.

## Flujo de Suscripción

1. **Usuario inicia sesión** → Pantalla de Login
2. **Selecciona perfil** → Pantalla de Perfiles
3. **Verificación automática de suscripción** → Pantalla de Suscripción (si no tiene)
4. **Usuario ve catálogo** → Pantalla Home (si tiene suscripción activa)

## Archivos Creados

### 1. Modelos de Datos

**Archivo**: `data/model/SubscriptionModels.kt`

Contiene las clases de datos para las respuestas de la API:

- `CreateCheckoutSessionRequest`: Solicitud para crear sesión de pago
- `CheckoutSessionResponse`: Respuesta con URL de Stripe Checkout
- `SubscriptionStatusResponse`: Estado actual de la suscripción
- `SubscriptionVerifyResponse`: Verificación de validez de suscripción

### 2. Repositorio

**Archivo**: `data/repository/SubscriptionRepository.kt`

Gestiona todas las operaciones relacionadas con suscripciones:

**Métodos principales**:

- `createCheckoutSession(plan: String)`: Crea una sesión de pago en Stripe
- `getSubscriptionStatus()`: Obtiene el estado de la suscripción del usuario
- `verifySubscription()`: Verifica si la suscripción es válida
- `getUserInfo()`: Obtiene información del usuario incluyendo estado de suscripción
- `hasActiveSubscription()`: Verifica si tiene suscripción activa (Boolean)

**Características**:
- Manejo automático del token de autenticación
- Gestión de errores con Result<T>
- Operaciones asíncronas con corrutinas
- Inyección de dependencias con Hilt

### 3. ViewModel

**Archivo**: `feature/subscription/SubscriptionViewModel.kt`

Gestiona el estado de la UI de suscripción:

**Estado (SubscriptionUiState)**:
- `isLoading`: Indica si está cargando
- `hasActiveSubscription`: Si tiene suscripción activa
- `subscriptionStatus`: Estado completo de la suscripción
- `checkoutUrl`: URL para abrir Stripe Checkout
- `error`: Mensaje de error si existe
- `daysRemaining`: Días restantes de la suscripción

**Métodos**:
- `checkSubscriptionStatus()`: Verifica el estado actual
- `createCheckoutSession()`: Crea sesión de pago
- `verifySubscription()`: Verifica validez
- `clearError()`: Limpia errores
- `refresh()`: Refresca todo el estado

### 4. Pantalla de Suscripción

**Archivo**: `feature/subscription/SubscriptionScreen.kt`

Pantalla Compose completa con:

**Componentes**:
- `SubscriptionScreen`: Pantalla principal
- `SubscriptionContent`: Contenido con información de suscripción
- `PricingCard`: Tarjeta con precio ($9.90 USD/mes)
- `FeaturesSection`: Lista de características incluidas
- `FeatureItem`: Item individual de característica
- `LoadingContent`: Indicador de carga animado

**Características**:
- Navegación automática si ya tiene suscripción
- Abre URL de Stripe Checkout en navegador externo
- Manejo de errores con UI amigable
- Animaciones suaves
- Opción temporal para omitir (desarrollo)

### 5. API Service

**Archivo**: `data/network/ApiService.kt`

Se agregaron los siguientes endpoints:

```kotlin
@POST("api/stripe/create-checkout-session")
suspend fun createCheckoutSession(
    @Header("Authorization") authHeader: String,
    @Body request: CreateCheckoutSessionRequest
): CheckoutSessionResponse

@GET("api/subscription/status")
suspend fun getSubscriptionStatus(
    @Header("Authorization") authHeader: String
): SubscriptionStatusResponse

@POST("api/subscription/verify")
suspend fun verifySubscription(
    @Header("Authorization") authHeader: String
): SubscriptionVerifyResponse

@GET("api/user")
suspend fun getUserInfo(
    @Header("Authorization") authHeader: String
): User
```

### 6. Navegación

**Archivo**: `MainActivity.kt`

Se modificó el flujo de navegación:

```kotlin
// Antes: profiles → home
// Ahora: profiles → subscription → home

composable("profiles") {
    ProfilesScreen(
        onProfileSelected = {
            navController.navigate("subscription") {
                popUpTo("profiles") { inclusive = true }
            }
        }
    )
}

composable("subscription") {
    SubscriptionScreen(navController = navController)
}
```

## Endpoints de Backend Necesarios

**IMPORTANTE**: Estos endpoints deben ser implementados en el backend Laravel.

### 1. Crear Sesión de Checkout

```
POST /api/stripe/create-checkout-session
Headers: Authorization: Bearer {token}
Body: { "plan": "monthly" }

Response (200):
{
  "success": true,
  "session_id": "cs_test_xxxxx",
  "session_url": "https://checkout.stripe.com/xxxxx"
}
```

### 2. Estado de Suscripción

```
GET /api/subscription/status
Headers: Authorization: Bearer {token}

Response (200):
{
  "success": true,
  "active": true,
  "expires_at": "2026-02-02 10:30:00",
  "plan": "monthly"
}
```

### 3. Verificar Suscripción

```
POST /api/subscription/verify
Headers: Authorization: Bearer {token}

Response (200):
{
  "success": true,
  "valid": true,
  "days_remaining": 28
}
```

### 4. Información del Usuario

```
GET /api/user
Headers: Authorization: Bearer {token}

Response (200):
{
  "id": 1,
  "name": "Juan Pérez",
  "email": "juan@example.com",
  "suscripcion_activa": true,
  "created_at": "2025-12-30T10:00:00"
}
```

## Modelo de Datos en Backend

El modelo `User` en la base de datos debe tener estos campos:

```php
// Migración Laravel
Schema::table('users', function (Blueprint $table) {
    $table->boolean('suscripcion_activa')->default(false);
    $table->timestamp('suscripcion_expira')->nullable();
});
```

## Flujo de Pago Completo

### Paso 1: Usuario presiona "Suscribirse"
```kotlin
subscriptionViewModel.createCheckoutSession { url ->
    // Se abre el navegador con la URL de Stripe
}
```

### Paso 2: Backend crea sesión de Stripe
```php
$session = \Stripe\Checkout\Session::create([
    'payment_method_types' => ['card'],
    'line_items' => [[
        'price_data' => [
            'currency' => 'usd',
            'product_data' => ['name' => 'Suscripción mensual'],
            'unit_amount' => 990, // $9.90
        ],
        'quantity' => 1,
    ]],
    'mode' => 'payment',
    'success_url' => url('/stripe/success'),
    'cancel_url' => url('/stripe/cancel'),
]);
```

### Paso 3: Usuario completa pago en Stripe
- Se abre Chrome Custom Tabs o navegador
- Usuario ingresa datos de tarjeta
- Stripe procesa el pago

### Paso 4: Stripe redirige a success_url
```php
// StripeController@success
$user = auth()->user();
$user->suscripcion_activa = true;
$user->suscripcion_expira = now()->addMonth();
$user->save();
```

### Paso 5: App verifica estado
```kotlin
// Usuario cierra navegador y regresa a la app
subscriptionViewModel.checkSubscriptionStatus()
// Si tiene suscripción activa, navega automáticamente a Home
```

## Verificación de Suscripción

El sistema verifica automáticamente:

1. **Al abrir la pantalla de suscripción**:
   - `SubscriptionViewModel.init()` → `checkSubscriptionStatus()`
   - Si `hasActiveSubscription == true` → navega a Home

2. **Campo del modelo User**:
   - `user.suscripcionActiva`: Boolean que indica si tiene suscripción

3. **Método del repositorio**:
   ```kotlin
   suspend fun hasActiveSubscription(): Boolean {
       val user = apiService.getUserInfo(authHeader = "Bearer $token")
       return user.suscripcionActiva
   }
   ```

## Características de la Pantalla de Suscripción

### Diseño
- Icono principal con gradiente
- Tarjeta de precio destacada ($9.90 USD/mes)
- Lista de características incluidas:
  - Acceso ilimitado al catálogo
  - Sin anuncios
  - Calidad HD
  - Hasta 5 perfiles
  - Contenido exclusivo
  - Cancela cuando quieras

### Funcionalidad
- Botón principal: "Suscribirse Ahora"
- Botón secundario: "Continuar sin suscripción" (temporal)
- Manejo de errores con tarjeta dismissible
- Indicador de carga animado
- Navegación automática si ya tiene suscripción

## Colores Utilizados

Para mantener consistencia con el tema de Netflix:

```kotlin
val NetflixRed = Color(0xFFE50914)      // Rojo Netflix
val ButtonColor = Color(0xFFE50914)     // Botones principales
val SuccessGreen = Color(0xFF4CAF50)    // Iconos de verificación
val ErrorRed = MaterialTheme.colorScheme.error
```

## Manejo de Errores

El sistema maneja los siguientes errores:

1. **Sin token de autenticación**
   - Error: "No hay token de autenticación"
   - Solución: Usuario debe iniciar sesión

2. **Error de red**
   - Error: Mensaje de Retrofit/IOException
   - Solución: Verificar conexión, reintentar

3. **Error del servidor**
   - Error: Mensaje del backend
   - Solución: Revisar logs del servidor

4. **Error de Stripe**
   - Error: Mensaje de Stripe API
   - Solución: Verificar configuración de Stripe

## Testing

### Cómo probar la integración:

1. **Sin suscripción**:
   - Iniciar sesión
   - Seleccionar perfil
   - Debe mostrar pantalla de suscripción
   - Presionar "Suscribirse"
   - Debe abrir navegador con Stripe

2. **Con suscripción**:
   - Marcar `suscripcion_activa = true` en BD
   - Iniciar sesión
   - Seleccionar perfil
   - Debe navegar directamente a Home

3. **Suscripción expirada**:
   - Marcar `suscripcion_activa = false`
   - Debe mostrar pantalla de suscripción

## Configuración de Stripe en Backend

En el archivo `.env` de Laravel:

```env
STRIPE_KEY=pk_test_xxxxx
STRIPE_SECRET=sk_test_xxxxx
```

En `config/services.php`:

```php
'stripe' => [
    'key' => env('STRIPE_KEY'),
    'secret' => env('STRIPE_SECRET'),
],
```

## Webhooks de Stripe (Futuro)

Para renovaciones automáticas, implementar:

```php
Route::post('/stripe/webhook', [StripeController::class, 'webhook']);

// StripeController@webhook
public function webhook(Request $request) {
    $payload = $request->getContent();
    $sig_header = $request->header('Stripe-Signature');
    
    $event = \Stripe\Webhook::constructEvent(
        $payload, $sig_header, env('STRIPE_WEBHOOK_SECRET')
    );
    
    if ($event->type === 'payment_intent.succeeded') {
        // Actualizar suscripción del usuario
    }
}
```

## Próximos Pasos

1. **Implementar endpoints en backend Laravel**
2. **Configurar cuenta de Stripe**
3. **Probar flujo completo end-to-end**
4. **Implementar webhooks para renovaciones**
5. **Agregar planes adicionales (anual, familiar)**
6. **Implementar cancelación de suscripción**
7. **Agregar historial de pagos**
8. **Quitar opción "Continuar sin suscripción"**

## Notas Importantes

- **Precio**: $9.90 USD por mes
- **Tipo de pago**: Pago único (no recurrente automático por ahora)
- **Duración**: 1 mes desde la fecha de pago
- **Verificación**: Se hace en el modelo User, campo `suscripcion_activa`
- **Token**: Se obtiene de SharedPreferences automáticamente
- **Seguridad**: Toda la lógica sensible está en el backend

## Cambios Realizados en el Código

### Archivos Nuevos (5):
1. `data/model/SubscriptionModels.kt`
2. `data/repository/SubscriptionRepository.kt`
3. `feature/subscription/SubscriptionViewModel.kt`
4. `feature/subscription/SubscriptionScreen.kt`
5. `DOCUMENTACION_STRIPE.md`

### Archivos Modificados (2):
1. `data/network/ApiService.kt` - Agregados 4 endpoints
2. `MainActivity.kt` - Modificado flujo de navegación

## Contacto y Soporte

Para dudas o problemas con la implementación:
- Revisar logs de Logcat en Android Studio
- Verificar logs del servidor Laravel
- Consultar documentación de Stripe: https://stripe.com/docs

---

**Última actualización**: 2 de Enero, 2026
**Versión**: 1.0
**Estado**: Implementación completa en Android, pendiente backend

