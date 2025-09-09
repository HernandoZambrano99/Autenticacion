#!/usr/bin/env bash
set -e

echo "Construyendo imágenes Docker para microservicios..."

# Construye Autenticacion
docker build -t autenticacion:latest -f Dockerfile ..

# Construye Solicitudes
docker build \
  -t solicitudes:latest \
  -f ../../solicitudes/deployment/Dockerfile \
  ../../solicitudes

echo "Levantando contenedores con docker-compose..."
docker-compose -f docker-compose.yml up -d

echo "Contenedores levantados:"
docker-compose ps
