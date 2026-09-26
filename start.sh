#!/bin/bash

set -euo pipefail

if docker compose version >/dev/null 2>&1; then
    COMPOSE=(docker compose)
elif command -v docker-compose >/dev/null 2>&1; then
    COMPOSE=(docker-compose)
else
    echo "Erro: Docker Compose nao esta instalado. Instale o plugin docker-compose antes de iniciar o ClassHub." >&2
    exit 1
fi

echo "==> Subindo ClassHub..."
"${COMPOSE[@]}" up -d --build --force-recreate


echo "==> Configurando Tailscale Funnel..."
# O Nginx do frontend e o unico gateway publico. Assim, /api e encaminhado
# pela rede interna do Docker e nao concorre com uma segunda rota no Funnel.
sudo tailscale funnel --https=443 off
sudo tailscale funnel --bg --set-path=/ http://localhost:5173

echo ""
echo "==> ClassHub iniciado!"
echo ""
sudo tailscale funnel status
