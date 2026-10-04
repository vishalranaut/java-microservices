# Spring Boot microservices starter

Java 21, Spring Boot 3.5, Spring Cloud 2025.0, Maven multi-module.

```
client -> api-gateway (8080) -> user-service  (REST 8081, gRPC 9090)  -> users_db + Redis
                             -> order-service (REST 8082)             -> orders_db
order-service --gRPC--> user-service
order-service --AMQP--> RabbitMQ --> notification-service (8083) --WebSocket--> browser
```

| Module | Purpose |
|---|---|
| `contracts` | `user.proto` and generated gRPC stubs, shared by user and order services |
| `user-service` | Users CRUD, gRPC server, Redis cache-aside |
| `order-service` | Orders, gRPC client with deadline + circuit breaker, RabbitMQ publisher |
| `notification-service` | RabbitMQ consumer with retry + dead-letter queue, STOMP WebSocket push |
| `api-gateway` | Routing, Redis-backed rate limiting |

## Run everything
```bash
docker compose up --build
```
First build is slow (Maven downloads). Then:

```bash
# 1. Open the live feed
open http://localhost:8083          # or just visit it in a browser

# 2. Create a user via the gateway
curl -X POST localhost:8080/users -H 'Content-Type: application/json' \
  -d '{"name":"Asha","email":"asha@example.com"}'

# 3. Place an order (validates the user over gRPC, publishes an event)
curl -X POST localhost:8080/orders -H 'Content-Type: application/json' \
  -d '{"userId":1,"amount":49.99}'
# The order appears in the browser feed.

# Error paths
curl -i -X POST localhost:8080/orders -H 'Content-Type: application/json' \
  -d '{"userId":999,"amount":10}'            # 422, user does not exist
curl -i -X POST localhost:8080/orders -H 'Content-Type: application/json' \
  -d '{"userId":1,"amount":0}'               # 400, validation
```
RabbitMQ UI: http://localhost:15672 (guest / guest). Metrics: `/actuator/prometheus` on each service.

## Run services locally without Docker
Start only infrastructure, then run each module from your IDE or:
```bash
docker compose up postgres redis rabbitmq
mvn -q install -DskipTests
mvn -pl user-service spring-boot:run
mvn -pl order-service spring-boot:run
mvn -pl notification-service spring-boot:run
mvn -pl api-gateway spring-boot:run
```
Defaults in each `application.yml` point at `localhost`.

## Tests
```bash
mvn test
```
Unit tests cover order creation, the user-not-found path and the WebSocket push. There are no integration tests yet.

## Things to know
- Versions were not verified by a build. If something fails to resolve, check these first: `grpc-spring-boot` (net.devh) compatibility with your Spring Boot version, and the Spring Cloud Gateway artifact/property names (renamed in Spring Cloud 2025.0).
- User cache uses JDK serialization, so cached values are not human-readable in Redis. Switch to JSON serialization if you want to inspect them.
- Dual write: `OrderService` saves the order, then publishes. If the broker is down in between, the event is lost. Fix with a transactional outbox.
- No authentication anywhere. Add JWT validation at the gateway first.
- `ddl-auto: update` is for the starter only. Use Flyway migrations.
- The WebSocket connects straight to notification-service, not through the gateway.
