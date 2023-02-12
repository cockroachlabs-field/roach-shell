#!/bin/bash

docker-compose up --no-start

docker-compose start roach-cert

sleep 5

docker-compose start roach-0

sleep 5

docker-compose start roach-1
docker-compose start roach-2
docker-compose start lb

sleep 5

docker-compose start roach-init

sleep 30

docker-compose start workload-client

sleep 10

docker-compose exec workload-client /cockroach/cockroach workload init --warehouses=3 tpcc "postgresql://root@lb.crdb.io:26257?sslmode=verify-full&sslrootcert=/certs/ca.crt&sslcert=/certs/client.root.crt&sslkey=/certs/client.root.key"

sleep 10

docker-compose exec workload-client /cockroach/cockroach workload run --warehouses=3 tpcc --tolerate-errors --duration=30m "postgresql://root@lb.crdb.io:26257?sslmode=verify-full&sslrootcert=/certs/ca.crt&sslcert=/certs/client.root.crt&sslkey=/certs/client.root.key"
