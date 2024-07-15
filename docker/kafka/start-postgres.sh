#!/bin/bash

echo "Starting postgres"
# start postgres
docker-compose -f common.yml -f postgres_debezium.yml up -d

# Function to check if PostgreSQL is ready
check_postgres_ready() {
  docker-compose -f common.yml -f postgres_debezium.yml exec postgres pg_isready -U postgres
}
# Wait until PostgreSQL is ready
echo "Waiting for postgres to be ready..."
until check_postgres_ready; do
  echo "Postgres is not ready yet. Waiting..."
  sleep 2
done
