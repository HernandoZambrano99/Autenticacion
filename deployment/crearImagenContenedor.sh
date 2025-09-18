#!/usr/bin/env bash
set -e

# Procesar argumentos de línea de comandos
while [[ $# -gt 0 ]]; do
    key="$1"
    case $key in
        -awskey|--aws_access_key)
            aws_access_key="$2"; shift 2 ;;
        -awssecret|--aws_secret_access_key)
            aws_secret_access_key="$2"; shift 2 ;;
        -awsregion|--aws_region)
            aws_region="$2"; shift 2 ;;
        -sqsaprobada|--sqs_listener_aprobada)
            sqs_listener_aprobada="$2"; shift 2 ;;
        -sqscalc|--sqs_calculator)
            sqs_calculator="$2"; shift 2 ;;
        -sqsnotif|--sqs_notification)
            sqs_notification="$2"; shift 2 ;;
        -sqssolicitud|--sqs_solicitud)
            sqs_solicitud="$2"; shift 2 ;;
        -sqslistener|--sqs_listener)
            sqs_listener="$2"; shift 2 ;;
        *)
            shift ;;
    esac
done
echo "Construyendo imágenes Docker para microservicios..."

# Exportar credenciales AWS antes del compose
export AWS_ACCESS_KEY_ID="$aws_access_key"
export AWS_SECRET_ACCESS_KEY="$aws_secret_access_key"
export AWS_REGION="$aws_region"
export SQS_LISTENER_APROBADA="$sqs_listener_aprobada"
export SQS_CALCULATOR="$sqs_calculator"
export SQS_NOTIFICATION="$sqs_notification"
export SQS_SOLICITUD="$sqs_solicitud"
export SQS_LISTENER="$sqs_listener"


# Construye Autenticacion
docker build -t autenticacion:latest -f Dockerfile ..

# Construye Solicitudes
docker build \
  -t solicitudes:latest \
  -f ../../solicitudes/deployment/Dockerfile \
  ../../solicitudes

# Construye Reportes
docker build \
  -t reportes:latest \
  -f ../../reportes/deployment/Dockerfile \
  ../../reportes

echo "Levantando contenedores con docker-compose..."
docker-compose -f docker-compose.yml up -d

echo "Contenedores levantados:"
docker-compose ps

