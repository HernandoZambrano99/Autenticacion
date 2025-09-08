#!/usr/bin/env bash
set -e

echo "Construyendo imágenes Docker para microservicios..."

# Construye Autenticacion (ruta un nivel arriba)
docker build -t autenticacion:latest -f Dockerfile ..

# Construye Solicitudes (contexto en la carpeta raíz de solicitudes)
docker build \
  -t solicitudes:latest \
  -f ../../solicitudes/deployment/Dockerfile \
  ../../solicitudes

echo "Levantando contenedores con docker-compose..."
docker-compose -f docker-compose.yml up -d

echo "Contenedores levantados:"
docker-compose ps
