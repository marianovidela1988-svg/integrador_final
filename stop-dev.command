#!/bin/bash
# Detiene todo el entorno de desarrollo.

echo "Deteniendo backend (spring-boot:run)..."
pkill -f "spring-boot:run"

echo "Deteniendo ngrok..."
pkill -f "ngrok http"

echo "Deteniendo n8n..."
pkill -f "n8n"

echo "Deteniendo MySQL de XAMPP (pide tu contrasena de macOS)..."
sudo /Applications/XAMPP/xamppfiles/xampp stopmysql

echo "Todo detenido. Ya podes cerrar las pestanas de Terminal que hayan quedado abiertas."
