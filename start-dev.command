#!/bin/bash
PROJECT_DIR="/Users/juanignaciojuarezmillion/Documents/UTN/TESIS/integrador_final"
NGROK_DOMAIN="gianni-astigmic-semiconventionally.ngrok-free.dev"

# 1. MySQL de XAMPP (si no está ya corriendo)
if ! lsof -nP -iTCP:3306 -sTCP:LISTEN >/dev/null 2>&1; then
  echo "Arrancando MySQL de XAMPP (pide tu contraseña)..."
  sudo /Applications/XAMPP/xamppfiles/xampp startmysql
fi

# 2. Una pestaña de Terminal por servicio
osascript <<EOF
tell application "Terminal"
    activate
    do script "cd '$PROJECT_DIR' && ./mvnw spring-boot:run"
    do script "ngrok http --url=https://$NGROK_DOMAIN 5678"
    do script "set -a; source ~/.n8n/.env; set +a; n8n"
end tell
EOF