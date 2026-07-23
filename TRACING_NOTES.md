# Tracing Notes — GymMonster

Doc 8 deliverable. This is the project where tracing matters most so far - a real request can cross gateway → server-administrator/server-informations synchronously (Doc 9 covers this), plus the async web-page → Kafka → server-register hop - all invisible to each other without it.

Applied the same pattern as food-ordering-system and furniture_store: `micrometer-tracing-bridge-brave` + `zipkin-reporter-brave` added to all 5 services + gateway, `management.tracing.sampling.probability=1.0` (local dev), `management.zipkin.tracing.endpoint` wired to a `zipkin` service added to `docker-compose-dev.yaml` via `ZIPKIN_ENDPOINT`.

## What this covers here specifically

- **Gateway → downstream service** calls (WebFlux `WebClient`, auto-instrumented once the tracing bridge is present) - a request entering at the gateway and getting proxied to `server-administrator`/`server-informations` now shows as one continuous trace instead of two disconnected ones.
- **web-page → Kafka → server-register**: fixed. Both `KafkaProducerConfig` (web-page) and `KafkaConsumerConfig` (server-register) build their factories by hand (`new DefaultKafkaProducerFactory<>(...)`, `new ConcurrentKafkaListenerContainerFactory<>()`), so Spring Boot's Kafka tracing auto-instrumentation never sees them - added `template.setObservationEnabled(true)` and `factory.getContainerProperties().setObservationEnabled(true)` to each, same one-line-each fix as food-ordering-system's Doc 8. A registration now traces continuously from the web-page request through to server-register's processing instead of appearing as two disconnected traces.

## Not covered

Frontend correlation (`gateway/frontend`, `monster-gym-frontend`) - same category of gap as furniture_store's Next.js frontend: real end-to-end correlation needs an OpenTelemetry SDK in the frontend's own stack, not a Spring config change. Not investigated further in this pass given the depth already spent on backend correctness issues in this project.
