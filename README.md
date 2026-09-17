---- Desarrollo local (perfil "dev", activo por defecto) ----
Este perfil es exclusivamente para desarrollar en la máquina local con
XAMPP y no se usa en ningún entorno desplegado. Sus valores están
fijados en application-dev.properties, separados del resto de perfiles.

Crear base de datos MySQL:
nombre  : proyecto_final
username: root
password: (vacío — válido únicamente para una instalación local de XAMPP
          sin contraseña de root configurada; no es una credencial real
          ni se reutiliza fuera de este contexto)

---- Despliegue real ----
Cualquier perfil distinto de "dev" (SPRING_PROFILES_ACTIVE=prod, por ejemplo)
exige definir por variable de entorno: DB_URL, DB_USERNAME, DB_PASSWORD,
JWT_SECRET, N8N_API_KEY, y opcionalmente JWT_COOKIE_SECURE=true si el
servicio corre detrás de HTTPS. Sin esas variables, el arranque falla:
los valores de conveniencia del perfil "dev" nunca se usan fuera de
desarrollo local.
