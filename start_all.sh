#!/bin/bash

# Ensure the script stops on first error
set -e

echo "=================================================="
echo "      Starting Carpooling Microservices"
echo "=================================================="

# 1. Create shared network if it doesn't exist
if ! docker network ls | grep -q "carpooling_network"; then
  echo "Creating network 'carpooling_network'..."
  docker network create carpooling_network
else
  echo "Network 'carpooling_network' already exists."
fi

# 2. Start Database
echo "--------------------------------------------------"
echo "Starting Database Service..."
docker compose -f carpooling_docker_compose_db/docker-compose.yml up -d --build

# 3. Start Service Discovery
echo "--------------------------------------------------"
echo "Starting Service Discovery..."
docker compose -f service-discovery/docker-compose.yml up -d --build

# 4. Start Microservices
echo "--------------------------------------------------"
echo "Starting Identity Service..."
docker compose -f identity/docker-compose.yml up -d --build

echo "--------------------------------------------------"
echo "Starting Vehicles Service..."
docker compose -f vehicles/docker-compose.yml up -d --build

echo "--------------------------------------------------"
echo "Starting Cloud Gateway..."
docker compose -f cloud-gateway-service/docker-compose.yml up -d --build

echo "=================================================="
echo "   All services are up and running!"
echo "=================================================="
echo "Eureka Directory: http://localhost:8761"
echo "Gateway:          http://localhost:8888"
