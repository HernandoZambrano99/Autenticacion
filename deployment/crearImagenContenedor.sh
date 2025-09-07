#!/usr/bin/env bash

set -e

echo "Construyendo imágenes Docker para microservicios..."

# Construye Autenticacion
docker build -t autenticacion:latest ./autenticacion

# Construye Solicitudes
docker build -t solicitudes:latest ./solicitudes

echo "Levantando contenedores con docker-compose..."
docker-compose up -d

echo "Contenedores levantados:"
docker-compose ps

echo "Puedes ver logs con:"
echo "  docker-compose logs -f autenticacion"
echo "  docker-compose logs -f solicitudes"
