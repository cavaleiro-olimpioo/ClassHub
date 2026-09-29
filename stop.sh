#!/bin/bash

set -euo pipefail

if docker compose version >/dev/null 2>&1; then
    COMPOSE=(docker compose)
elif command -v docker-compose >/dev/null 2>&1; then
    COMPOSE=(docker-compose)
else
    echo "Erro: Docker Compose nao esta instalado. Nao foi possivel parar os containers do PhonkHub." >&2
    exit 1
fi

echo "==> Desligando Tailscale Funnel..."
sudo tailscale funnel --https=443 off

echo "==> Parando PhonkHub (Lá ele!)..."
"${COMPOSE[@]}" down

echo ""
echo "==> PhonkHub parado! Valeu Kirk, Floyd, Jennifer e Bora Bill!"
