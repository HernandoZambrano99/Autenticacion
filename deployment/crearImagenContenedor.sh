#!/usr/bin/env bash
set -e

echo "Construyendo imágenes Docker para microservicios..."

# Construye Autenticacion (ruta un nivel arriba)
docker build -t autenticacion:latest -f Dockerfile ..

echo "Levantando contenedores con docker-compose..."
docker-compose -f docker-compose.yml up -d

echo "Contenedores levantados:"
docker-compose ps

echo "Puedes ver logs con:"
echo "  docker-compose logs -f autenticacion"
echo "  docker-compose logs -f solicitudes"
