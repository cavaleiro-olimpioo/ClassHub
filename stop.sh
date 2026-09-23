#!/bin/bash

echo "==> Desligando Tailscale Funnel..."
sudo tailscale funnel --https=443 off

echo "==> Parando ClassHub..."
docker compose down

echo ""
echo "==> ClassHub parado!"
