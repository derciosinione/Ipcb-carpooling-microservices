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

# 2. Create shared overlay network if it doesn't exist or is not overlay
# This is required for Swarm stacks to use external networks
if ! docker network ls --filter driver=overlay | grep -q "carpooling_network"; then
  echo "Ensuring 'carpooling_network' is an overlay network..."
  
  # Check if any containers are using the existing bridge network
  CONTAINERS_IN_NET=$(docker ps -a --filter network=carpooling_network --format '{{.ID}}')
  if [ ! -z "$CONTAINERS_IN_NET" ]; then
    echo "Stopping containers using the old network..."
    docker rm -f $CONTAINERS_IN_NET || true
  fi

  # Remove if it exists as a local bridge
  docker network rm carpooling_network 2>/dev/null || true
  
  # Create as overlay
  docker network create --driver overlay --attachable carpooling_network
  echo "Attachable overlay network 'carpooling_network' created."
else
  echo "Overlay network 'carpooling_network' already exists."
fi


# 3. Build Docker Images (Required before stack deploy)

echo "--------------------------------------------------"
echo "Building Images..."

echo "Building and Starting Database..."
docker compose -f carpooling_docker_compose_db/docker-compose.yml up --build -d

echo "Building Service Discovery..."
docker build -t car-pooling/service-discovery:latest ./service-discovery

echo "Building Identity Service..."
docker build -t car-pooling/identity:latest ./identity

echo "Building Vehicles Service..."
docker build -t car-pooling/vehicles:latest ./vehicles

echo "Building Trips Service..."
docker build -t car-pooling/trips:latest ./trips

echo "Building Payments Service..."
docker build -t car-pooling/payments:latest ./payments

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
