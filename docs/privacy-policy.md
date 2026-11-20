# Política de privacidad

## 1. Información que recolectamos
La aplicación puede recopilar:
- nombre y datos básicos del emprendedor
- correo y un identificador de cuenta de Google
- fecha de nacimiento para aplicar la regla de acceso por edad
- cédula de identidad para evitar registros duplicados
- correo del familiar adulto cuando se solicita aprobación
- información de negocio o vendedor
- historial de ventas y productos publicados
- código del vendedor y código de venta
- datos necesarios para la búsqueda y catálogo

## 2. Uso de la información
Usamos esta información para:
- gestionar el catálogo de productos
- calcular niveles de vendedor
- registrar ventas y validar transacciones
- mejorar la experiencia de compra y venta
- detectar fraude o inconsistencias de conectividad

## 3. Protección de datos
- La cédula no debe guardarse en texto plano ni en el dispositivo. El servidor debe normalizarla y guardar únicamente un HMAC con una clave secreta que no esté en la app ni en el repositorio.
- El intercambio de la cédula debe ocurrir por HTTPS/TLS.
- Los datos de perfil y las solicitudes familiares deben tener acceso restringido por usuario mediante políticas RLS.
- La aprobación de un familiar debe validarse en el servidor: cuenta autenticada, correo destinatario coincidente y edad de al menos 18 años.
- Los códigos de venta no deben exponerse innecesariamente.

## 4. No se usan pasarelas de pago en este prototipo
Este proyecto no integra APIs de pago de forma obligatoria. La venta se registra mediante:
- código de vendedor
- código de venta
- validación por conectividad

Esto permite un flujo funcional sin depender de una pasarela en la primera etapa.

## 5. Consentimiento del usuario
El usuario debe aceptar:
- el uso de datos mínimos para operar la app
- la verificación de ventas
- la posibilidad de sincronización con servidores y base de datos
- la política de almacenamiento y privacidad
- el tratamiento de la cédula y fecha de nacimiento para registro y seguridad

## 6. Inicio de sesión y acceso de menores
El inicio de sesión usa una cuenta de Google verificada mediante Supabase Auth. Google autentica la cuenta, pero no verifica la cédula ni la fecha de nacimiento declarada. La edad solo puede considerarse comprobada si se integra un proveedor legítimo de verificación de identidad.

Las cuentas de personas menores de 17 años quedan pendientes y no pueden entrar a la app hasta que un usuario familiar con cuenta activa y edad registrada de 18 años o más apruebe la solicitud dirigida a su correo. La aplicación no debe interpretar una solicitud enviada como una aprobación.

La cédula y la fecha de nacimiento son datos personales. Antes de habilitar el registro en producción se debe configurar el proveedor de Google, el almacenamiento seguro del HMAC de cédula y las políticas de acceso del servidor.

## 7. Derechos del usuario
El usuario puede solicitar:
- revisión de sus datos
- corrección de información incorrecta
- eliminación de información relevante dentro del marco legal
- explicación del uso de sus datos

## 8. Actualizaciones
Esta política puede cambiar con nuevos módulos, integraciones o mejoras funcionales.
