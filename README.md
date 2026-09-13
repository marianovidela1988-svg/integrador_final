---- Desarrollo local (perfil "dev", activo por defecto) ----
Crear base de datos MySQL:
nombre : proyecto_final
username: root
password: (vacío; valores de application-dev.properties, solo para XAMPP local)

---- Despliegue real ----
Cualquier perfil distinto de "dev" (SPRING_PROFILES_ACTIVE=prod, por ejemplo)
exige definir por variable de entorno: DB_URL, DB_USERNAME, DB_PASSWORD,
JWT_SECRET, N8N_API_KEY, y opcionalmente JWT_COOKIE_SECURE=true si el
servicio corre detrás de HTTPS. Sin esas variables, el arranque falla.
