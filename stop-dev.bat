@echo off
REM ============================================================
REM  stop-dev.bat  -  Detiene todo el entorno de desarrollo
REM  Equivalente Windows de stop-dev.command
REM ============================================================
setlocal
set "XAMPP_DIR=C:\xampp"

echo Deteniendo ngrok...
taskkill /F /IM ngrok.exe >nul 2>&1

echo Deteniendo backend y n8n...
powershell -NoProfile -Command "Get-CimInstance Win32_Process | Where-Object { $_.CommandLine -and ($_.CommandLine -match 'spring-boot:run' -or $_.CommandLine -match 'integrador_final' -or $_.CommandLine -match 'node_modules.n8n' -or $_.CommandLine -match '[\\/]n8n[\\/]bin') } | ForEach-Object { try { Stop-Process -Id $_.ProcessId -Force -ErrorAction SilentlyContinue } catch {} }"

echo Deteniendo MySQL de XAMPP...
call "%XAMPP_DIR%\mysql_stop.bat"

echo.
echo Todo detenido. Cerra las ventanas de consola que hayan quedado abiertas.
echo.
pause
endlocal
