# VENECA APP

App para emprendedores en Venezuela que combina funciones de marketplace, catálogo de productos, niveles de vendedor, búsqueda inteligente y ventas en red local, sin depender de pasarelas de pago complejas.

## Objetivo
Unir la lógica de comercio digital con la realidad de microempresarios y pequeños negocios en Venezuela:
- compra por mayor y menor
- ventas locales y online
- búsqueda de artículos
- gestión de nivel de vendedor
- flujo de venta con código de vendedor y código de venta
- seguridad, trazabilidad y privacidad

## Lo que faltaba y ya se resuelve aquí
El README original dejaba claro que faltaban:
- servidores
- APIs de pago
- base de datos
- escalabilidad
- políticas de privacidad

Esta versión deja la base completa para implementarlo en etapas:
1. Frontend móvil con Kotlin
2. Backend con Java
3. Módulo nativo en C para validación y seguridad
4. Base de datos con Supabase
5. Política de privacidad para uso de datos y ventas

## Arquitectura recomendada
- Frontend: Android + Kotlin
- Backend: Java + servicios REST
- Base de datos: Supabase (primero), luego PostgreSQL escalable
- Validación nativa: C para código de venta, validación de vendedor y nivel de seguridad
- Integración futura: Zelle, Binance, Pasarelas locales o transferencias bancarias cuando haya conectividad real

## Funcionalidades principales
- Inicio de sesión preparado para cuentas de Google verificadas
- Registro con cédula y fecha de nacimiento
- Solicitud de aprobación de un familiar registrado para menores de 17 años
- Búsqueda de artículos por nombre, categoría o palabra clave
- Catálogo para venta por mayor y menor
- Niveles de vendedor por volumen y ventas
- Registro de ventas con código del vendedor y código de venta
- Validación de venta sin pasar por API de pago directa
- Panel para emprendedores con historial de ventas
- Política de privacidad y consentimiento del usuario

## Flujo de venta sugerido
Cuando un usuario vende:
1. Escanea o ingresa el código del producto
2. Ingresa el código del vendedor
3. Genera un código de venta
4. El sistema registra la operación como validada por conectividad local o por sincronización posterior
5. La venta se sincroniza con la base de datos y la auditoría

Esto ayuda a evitar pasar por pagos automáticos mientras se construye la conectividad real.

## Stack sugerido
- Java: backend y servicios
- Kotlin: app móvil / lógica de UX
- C: módulo nativo para validación de códigos y seguridad
- Supabase: base de datos + autenticación + almacenamiento

## Inicio de sesión y registro
El flujo de cuenta está definido en `mobile/src/main/kotlin/com/veneca/app/AuthFlow.kt`:
1. El usuario inicia sesión con Google; el proveedor debe devolver una identidad ya verificada por Supabase Auth.
2. Si todavía no tiene perfil, completa nombre, cédula y fecha de nacimiento.
3. Una persona de 17 años o más obtiene acceso normal. Una persona menor de 17 debe indicar el correo de un familiar adulto con cuenta registrada.
4. La cuenta menor queda pendiente y solo se habilita cuando la persona familiar, autenticada y de al menos 18 años, aprueba la solicitud.

Para producción, configura Google como proveedor OAuth en Supabase Auth y conecta `GoogleAuthGateway` y `AuthRepository` al SDK/servidor. No confíes en datos de edad o identidad calculados solo en el dispositivo. La cédula debe enviarse al servidor por TLS y persistirse solo como HMAC con una clave secreta del servidor; nunca guardes la cédula en texto plano. El esquema Supabase incluye RLS y una función de aprobación que verifica la cuenta adulta y el correo destinatario.

## Estructura del proyecto
- `backend/`: lógica de negocio del marketplace
- `mobile/`: app con flujo principal en Kotlin
- `native/`: módulo C para validación y seguridad
- `database/`: esquema para Supabase
- `docs/`: políticas y documentación

## Política y cumplimiento
La app debe incluir:
- política de privacidad
- consentimiento de datos
- gestión de información de ventas
- reglas de manejo de códigos de vendedor y venta
- trazabilidad para auditoría y prevención de fraude

## Siguiente paso recomendado
Conectar el backend a Supabase y preparar las primeras tablas:
- productos
- usuarios
- vendedores
- ventas
- niveles
- políticas_y_consultas

Este proyecto queda listo para crecer hacia un marketplace funcional de Venezuela con orientación a emprendedores.
