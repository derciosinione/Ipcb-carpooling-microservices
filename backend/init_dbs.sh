#!/bin/bash

DATABASES=("identity_db" "vehicles_db" "trips_db" "carpooling_payments" "gps_db" "notifications_db")

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

echo "Ensuring trips_db schema is up to date..."
docker exec postgres_server psql -U postgres -d trips_db -c "ALTER TABLE IF EXISTS bookings ADD COLUMN IF NOT EXISTS paid boolean NOT NULL DEFAULT false;"
docker exec postgres_server psql -U postgres -d trips_db -c "ALTER TABLE IF EXISTS bookings ADD COLUMN IF NOT EXISTS paid_at timestamp;"
docker exec postgres_server psql -U postgres -d trips_db -c "ALTER TABLE IF EXISTS bookings ADD COLUMN IF NOT EXISTS payment_reference varchar(255);"

echo "Ensuring identity_db schema is up to date..."
docker exec postgres_server psql -U postgres -d identity_db -c "ALTER TABLE IF EXISTS users ADD COLUMN IF NOT EXISTS active boolean NOT NULL DEFAULT true;"

echo "Seeding identity_db (only if missing)..."
docker exec postgres_server psql -U postgres -d identity_db -c "CREATE EXTENSION IF NOT EXISTS \"pgcrypto\";"
docker exec postgres_server psql -U postgres -d identity_db -c "INSERT INTO profiles (id, name, description, created_at) SELECT gen_random_uuid(), 'Condutor', 'Condutor', now() WHERE NOT EXISTS (SELECT 1 FROM profiles WHERE name = 'Condutor');"
docker exec postgres_server psql -U postgres -d identity_db -c "INSERT INTO profiles (id, name, description, created_at) SELECT gen_random_uuid(), 'Passageiro', 'Passageiro', now() WHERE NOT EXISTS (SELECT 1 FROM profiles WHERE name = 'Passageiro');"
docker exec postgres_server psql -U postgres -d identity_db -c "INSERT INTO profiles (id, name, description, created_at) SELECT gen_random_uuid(), 'Admin', 'Administração', now() WHERE NOT EXISTS (SELECT 1 FROM profiles WHERE name = 'Admin');"

echo "Seeding vehicles_db brands (only if missing)..."
docker exec postgres_server psql -U postgres -d vehicles_db -c "INSERT INTO public.brands (id, name, description, created_at, updated_at) SELECT gen_random_uuid(), v.name, v.description, now(), now() FROM (VALUES ('Toyota','Japanese manufacturer'), ('Honda','Japanese manufacturer'), ('Nissan','Japanese manufacturer'), ('Mazda','Japanese manufacturer'), ('Subaru','Japanese manufacturer'), ('Mitsubishi','Japanese manufacturer'), ('BMW','German manufacturer'), ('Mercedes-Benz','German manufacturer'), ('Audi','German manufacturer'), ('Volkswagen','German manufacturer'), ('Porsche','German manufacturer'), ('Opel','German brand (EU market)'), ('Ford','American manufacturer'), ('Chevrolet','American manufacturer'), ('Tesla','American EV manufacturer'), ('Jeep','American brand'), ('Hyundai','Korean manufacturer'), ('Kia','Korean manufacturer'), ('Volvo','Swedish manufacturer'), ('Peugeot','French manufacturer'), ('Renault','French manufacturer'), ('Citroën','French manufacturer'), ('Fiat','Italian manufacturer'), ('Alfa Romeo','Italian manufacturer'), ('Ferrari','Italian manufacturer'), ('Lamborghini','Italian manufacturer'), ('SEAT','Spanish brand'), ('Škoda','Czech brand'), ('Land Rover','British manufacturer'), ('Jaguar','British manufacturer'), ('Mini','British brand'), ('Suzuki','Japanese manufacturer'), ('Dacia','Budget EU brand'), ('BYD','Chinese EV manufacturer'), ('Geely','Chinese manufacturer'), ('Lexus','Toyota premium brand'), ('Acura','Honda premium brand'), ('Infiniti','Nissan premium brand')) AS v(name, description) WHERE NOT EXISTS (SELECT 1 FROM public.brands b WHERE b.name = v.name);"

echo "=================================================="
echo "   All databases are verified and ready!"
echo "=================================================="


# Run this in the identity_db

# CREATE EXTENSION IF NOT EXISTS "pgcrypto";

# insert into profiles (id, name, description, created_at)
# values (gen_random_uuid(),'Condutor', 'Condutor', now());

# insert into profiles (id, name, description, created_at)
# values (gen_random_uuid(),'Passageiro', 'Passageiro', now());

# insert into profiles (id, name, description, created_at)
# values (gen_random_uuid(),'Admin', 'Administração', now());

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
