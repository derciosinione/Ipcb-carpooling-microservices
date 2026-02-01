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


# Run this in the vehicles_db

# INSERT INTO public.brands (id, name, description, created_at, updated_at)
# VALUES
#     (gen_random_uuid(), 'Toyota', 'Japanese manufacturer', now(), now()),
#     (gen_random_uuid(), 'Honda', 'Japanese manufacturer', now(), now()),
#     (gen_random_uuid(), 'Nissan', 'Japanese manufacturer', now(), now()),
#     (gen_random_uuid(), 'Mazda', 'Japanese manufacturer', now(), now()),
#     (gen_random_uuid(), 'Subaru', 'Japanese manufacturer', now(), now()),
#     (gen_random_uuid(), 'Mitsubishi', 'Japanese manufacturer', now(), now()),

#     (gen_random_uuid(), 'BMW', 'German manufacturer', now(), now()),
#     (gen_random_uuid(), 'Mercedes-Benz', 'German manufacturer', now(), now()),
#     (gen_random_uuid(), 'Audi', 'German manufacturer', now(), now()),
#     (gen_random_uuid(), 'Volkswagen', 'German manufacturer', now(), now()),
#     (gen_random_uuid(), 'Porsche', 'German manufacturer', now(), now()),
#     (gen_random_uuid(), 'Opel', 'German brand (EU market)', now(), now()),

#     (gen_random_uuid(), 'Ford', 'American manufacturer', now(), now()),
#     (gen_random_uuid(), 'Chevrolet', 'American manufacturer', now(), now()),
#     (gen_random_uuid(), 'Tesla', 'American EV manufacturer', now(), now()),
#     (gen_random_uuid(), 'Jeep', 'American brand', now(), now()),

#     (gen_random_uuid(), 'Hyundai', 'Korean manufacturer', now(), now()),
#     (gen_random_uuid(), 'Kia', 'Korean manufacturer', now(), now()),

#     (gen_random_uuid(), 'Volvo', 'Swedish manufacturer', now(), now()),
#     (gen_random_uuid(), 'Peugeot', 'French manufacturer', now(), now()),
#     (gen_random_uuid(), 'Renault', 'French manufacturer', now(), now()),
#     (gen_random_uuid(), 'Citroën', 'French manufacturer', now(), now()),

#     (gen_random_uuid(), 'Fiat', 'Italian manufacturer', now(), now()),
#     (gen_random_uuid(), 'Alfa Romeo', 'Italian manufacturer', now(), now()),
#     (gen_random_uuid(), 'Ferrari', 'Italian manufacturer', now(), now()),
#     (gen_random_uuid(), 'Lamborghini', 'Italian manufacturer', now(), now()),

#     (gen_random_uuid(), 'SEAT', 'Spanish brand', now(), now()),
#     (gen_random_uuid(), 'Škoda', 'Czech brand', now(), now()),

#     (gen_random_uuid(), 'Land Rover', 'British manufacturer', now(), now()),
#     (gen_random_uuid(), 'Jaguar', 'British manufacturer', now(), now()),
#     (gen_random_uuid(), 'Mini', 'British brand', now(), now()),

#     (gen_random_uuid(), 'Suzuki', 'Japanese manufacturer', now(), now()),
#     (gen_random_uuid(), 'Dacia', 'Budget EU brand', now(), now()),

#     (gen_random_uuid(), 'BYD', 'Chinese EV manufacturer', now(), now()),
#     (gen_random_uuid(), 'Geely', 'Chinese manufacturer', now(), now()),

#     (gen_random_uuid(), 'Lexus', 'Toyota premium brand', now(), now()),
#     (gen_random_uuid(), 'Acura', 'Honda premium brand', now(), now()),

#     (gen_random_uuid(), 'Infiniti', 'Nissan premium brand', now(), now())
