## Spring Boot microservices with Consul cluster and Traefik

Demo of working Spring Boot microservices with [Consul](https://www.consul.io/) cluster, for service discovering, and 
[Traefik](https://docs.traefik.io/) for external automatic and dynamic routing and load balancing.

1. Build project

```
mvn clean package
```

2. Run Consul cluster and Traefik 

```
docker-compose up -d
```

3. Run microservices (two instances per service)

```
docker-compose -f services-compose.yml up -d --scale first-service=2 --scale second-service=2 --build
```

4. Watch how they 'talk' with each other:

```
docker-compose -f services-compose.yml logs -f
```

5. Run external requests

- manually:

```
curl 'http://localhost/demo/one'
curl 'http://localhost/demo/two'
```

- or with [k6](https://k6.io/)

```
k6 run src/test/js/load-test.js --duration 1m
```

Then watch reflection of their work in the logs. 

6. Stop the environment

```
docker-compose -f services-compose.yml down
docker-compose down
```

