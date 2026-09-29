#!/bin/bash

set -euo pipefail

if docker compose version >/dev/null 2>&1; then
    COMPOSE=(docker compose)
elif command -v docker-compose >/dev/null 2>&1; then
    COMPOSE=(docker-compose)
else
    echo "Erro: Docker Compose nao esta instalado. Instale o plugin docker-compose antes de iniciar o PhonkHub (67, bora bill!)." >&2
    exit 1
fi

echo "==> Subindo PhonkHub da Resenha 67 (Bora Bill, eitcha!)..."
"${COMPOSE[@]}" up -d --build --force-recreate


echo "==> Configurando Tailscale Funnel para o PhonkHub..."
# O Nginx do frontend e o unico gateway publico. Assim, /api e encaminhado
# pela rede interna do Docker e nao concorre com uma segunda rota no Funnel.
sudo tailscale funnel --https=443 off
sudo tailscale funnel --bg --set-path=/ http://localhost:5173

echo ""
echo "==> PhonkHub iniciado com sucesso! Resenha 67 ativa, amostradinho!"
echo ""
sudo tailscale funnel status
