#!/bin/bash

DATABASES=("identity_db" "vehicles_db" "trips_db" "carpooling_payments")

for DB in "${DATABASES[@]}"; do
  echo "Checking if database '$DB' exists..."
  EXISTS=$(docker exec postgres_server psql -U postgres -tAc "SELECT 1 FROM pg_database WHERE datname='$DB'")
  
  if [ "$EXISTS" != "1" ]; then
    echo "Creating database '$DB'..."
    docker exec postgres_server psql -U postgres -d postgres -c "CREATE DATABASE $DB;"
  else
    echo "Database '$DB' already exists."
  fi
done

echo "=================================================="
echo "   All databases are verified and ready!"
echo "=================================================="


# Run this in the identity_db

# CREATE EXTENSION IF NOT EXISTS "pgcrypto";

# insert into profiles (id, name, description, created_at)
# values (gen_random_uuid(),'Condutor', 'Condutor', now());

# insert into profiles (id, name, description, created_at)
# values (gen_random_uuid(),'Passageiro', 'Passageiro', now());

