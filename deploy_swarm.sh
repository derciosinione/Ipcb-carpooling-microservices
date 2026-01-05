#!/bin/bash

# Ensure script stops on first error
set -e

echo "=================================================="
echo "      Deploying to Docker Swarm"
echo "=================================================="

# 1. Initialize Swarm if not already active
if ! docker info | grep -q "Swarm: active"; then
  echo "Initializing Docker Swarm..."
  docker swarm init || true
else
  echo "Docker Swarm is already active."
fi

# 2. Build Docker Images (Required before stack deploy)
echo "--------------------------------------------------"
echo "Building Images..."

echo "Building Service Discovery..."
docker build -t car-pooling/service-discovery:latest ./service-discovery

echo "Building Identity Service..."
docker build -t car-pooling/identity:latest ./identity

echo "Building Vehicles Service..."
docker build -t car-pooling/vehicles:latest ./vehicles

echo "Building Cloud Gateway..."
docker build -t car-pooling/cloud-gateway:latest ./cloud-gateway-service

# 3. Deploy Stack
echo "--------------------------------------------------"
echo "Deploying Stack 'carpooling_stack'..."
docker stack deploy -c docker-stack.yml carpooling_stack

echo "=================================================="
echo "      Deployment Initiated!"
echo "=================================================="
echo "Monitor status with: docker stack ps carpooling_stack"
echo "Portainer UI:        http://localhost:9000"
echo "Eureka Dashboard:    http://localhost:8761"
echo "Gateway API:         http://localhost:8888"
