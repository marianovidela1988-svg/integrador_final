@echo off
REM ============================================================
REM  start-dev.bat  -  Levanta todo el entorno de desarrollo
REM  (MySQL de XAMPP + backend Spring Boot + ngrok + n8n)
REM  Equivalente Windows de start-dev.command
REM  Pone este archivo en la raiz del repo y hace doble clic.
REM ============================================================
setlocal
cd /d "%~dp0"

REM ====== EDITA ESTAS LINEAS ======
set "NGROK_DOMAIN=SU-DOMINIO.ngrok-free.dev"
set "TELEGRAM_BOT_TOKEN=SU-TOKEN-DE-BOTFATHER"
set "XAMPP_DIR=C:\xampp"
REM ================================

REM Variables de entorno que hereda la ventana de n8n
set "WEBHOOK_URL=https://%NGROK_DOMAIN%"
set "N8N_EDITOR_BASE_URL=https://%NGROK_DOMAIN%"
set "N8N_BLOCK_ENV_ACCESS_IN_NODE=false"
set "N8N_API_KEY=dev-only-change-me"

echo Arrancando MySQL de XAMPP...
start "MySQL" cmd /k "cd /d %XAMPP_DIR% && mysql_start.bat"

echo Esperando a que MySQL levante...
timeout /t 8 /nobreak >nul

echo Arrancando backend...
start "backend" cmd /k "mvnw.cmd spring-boot:run"

echo Arrancando ngrok...
REM Si NO tenes dominio fijo de ngrok, cambia la linea de abajo por:
REM   start "ngrok" cmd /k "ngrok http 5678"
REM y despues pega la URL que te da en WEBHOOK_URL / N8N_EDITOR_BASE_URL y reinicia n8n.
start "ngrok" cmd /k "ngrok http --url=https://%NGROK_DOMAIN% 5678"

echo Arrancando n8n...
start "n8n" cmd /k "n8n"

echo.
echo Listo. Se abrieron 4 ventanas: MySQL, backend, ngrok y n8n.
echo Abri http://localhost:5678 y activa el workflow.
echo Para frenar todo: stop-dev.bat
echo.
pause
endlocal
