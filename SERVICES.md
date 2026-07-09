# Gym Monster — Service Descriptions

## gateway
Reactive edge gateway (Spring Cloud Gateway on WebFlux). `RedisTokenValidationFilter` checks the caller's session token against Redis before routing; `SessionController` manages session lifecycle; `WebClientConfig`/`ServicesUrl` hold the downstream service locations (resolved via Spring Cloud Kubernetes in-cluster). Has its own integration test suite for the Redis token-validation filter.

## server-administrator
Back-office CRUD API for gym operations staff: clients, equipment, memberships, pools, promotions, schedules, specialties, trainers, and work classes, exposed as WebFlux functional routers (`ClientRouter`, `EquipamentRouter`, `RouterMembership`, `RouterPool`, `RouterSpecialty`, `RouterWorkClass`, `ScheduleRouter`, `PromotionRouter`, `RouterClassTrainer`, `PreTrainerRouter`).

## server-informations
Read-facing information API for the same domain (clients, trainers, memberships, pools, promotions, schedules, specialties, equipment), plus Keycloak user lookups and outbound email (`MailConfiguration`, `EmailServiceImpl`). Has integration tests covering the Keycloak service and a client repository.

## server-register
Handles new-member registration end to end: validates registration data (`RegisterGeneralValidations`, `ValidatePassword`), provisions a Keycloak user, persists the client/membership records, and sends confirmation email. Consumes registration events from Kafka (`KafkaConsumerConfig`) published by `web-page`, so the public-facing request doesn't block on this work.

## web-page
The public-facing service: exposes membership, pool, promotion, specialty, trainer, and work-class information plus the registration entry point (`RouterRegister`). Publishes registration events to Kafka (`KafkaProducerConfig`) for `server-register` to process, and uses Redis (`redisConfiguration`) for its own caching/session needs.

## Infrastructure (Kubernetes manifests)
`deployments/` and `configMaps/` provision: the 5 application services, MySQL (+ phpMyAdmin for management), Redis, Keycloak, and a Kafka + Zookeeper broker — each with its own Deployment and ConfigMap; `Secrets/` holds credentials.
