#!/bin/bash

set -e

echo "==> Subindo ClassHub..."
docker compose up -d

echo "==> Configurando Tailscale Funnel..."
sudo tailscale funnel --bg --set-path=/ http://localhost:5173
sudo tailscale funnel --bg --set-path=/api http://localhost:8080

echo ""
echo "==> ClassHub iniciado!"
echo ""
sudo tailscale funnel status
