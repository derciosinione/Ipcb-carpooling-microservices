#!/bin/bash

echo "=================================================="
echo "      Removing Carpooling Stack"
echo "=================================================="

docker stack rm carpooling_stack

docker compose -f carpooling_docker_compose_db/docker-compose.yml down

echo "=================================================="
echo "      Stack removal initiated!"
echo "=================================================="
echo "It may take a few moments for all containers to stop."
echo "Check status with: docker ps"
