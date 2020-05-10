### Start environment with two demo-services

```
mvn clean package
docker-compose -f demo-compose.yml -f docker-compose.yml up -d --scale demo-service=2 --build
```

### Stop environment

```
docker-compose -f demo-compose.yml -f docker-compose.yml down --remove-orphans
```

### Start just Consul cluster 

```
docker-compose up -d
```

### Stop Consul cluster

```
docker-compose down
```