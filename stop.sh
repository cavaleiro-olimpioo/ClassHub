#!/bin/bash

set -euo pipefail

if docker compose version >/dev/null 2>&1; then
    COMPOSE=(docker compose)
elif command -v docker-compose >/dev/null 2>&1; then
    COMPOSE=(docker-compose)
else
    echo "Erro: Docker Compose nao esta instalado. Nao foi possivel parar os containers do ClassHub." >&2
    exit 1
fi

echo "==> Desligando Tailscale Funnel..."
sudo tailscale funnel --https=443 off

echo "==> Parando ClassHub..."
"${COMPOSE[@]}" down

echo ""
echo "==> ClassHub parado!"
