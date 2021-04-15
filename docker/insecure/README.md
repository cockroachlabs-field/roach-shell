# Insecure CockroachDB Cluster with HAProxy
Simple 3 node CockroachDB cluster with HAProxy acting as load balancer

## Services
* `crdb-0` - CockroachDB node
* `crdb-1` - CockroachDB node
* `crdb-2` - CockroachDB node
* `workload-client` - CockroachDB node serving as the `workload` client
* `lb` - HAProxy acting as load balancer

## Helpful Commands
### Open Interactive Shells
```bash
docker-compose exec crdb-0 /bin/bash
docker-compose exec crdb-1 /bin/bash
docker-compose exec crdb-2 /bin/bash
docker-compose exec workload-client /bin/bash
docker-compose exec lb /bin/sh
```

### Stop Individual nodes
```bash
docker-compose stop crdb-0
docker-compose stop crdb-1
docker-compose stop crdb-2
```