GYM MONSTER — Kubernetes-Native Gym Management Platform

**Kubernetes-Native Microservices**: I designed a 5-service system (gateway, server-administrator, server-informations, server-register, web-page) built on Spring Cloud Kubernetes — services discover and load-balance each other through the Kubernetes API directly (`spring-cloud-starter-kubernetes-client` + `-loadbalancer`), not a separate service registry, and every service is fully reactive (Spring WebFlux) end to end. Deployed as raw Kubernetes manifests: a Deployment + ConfigMap per service, plus MySQL, Redis, Keycloak, and a Kafka/Zookeeper broker.

**Reactive API Gateway with Redis-Backed Sessions**: I built the edge gateway on Spring Cloud Gateway with a custom `RedisTokenValidationFilter` that validates session tokens against Redis before proxying to the appropriate backend service, keeping token validation off the hot path of every downstream service.

**Keycloak-Backed Identity Across Services**: I integrated Keycloak as the identity provider — `server-register` and `server-informations` both provision and read Keycloak users (`KeycloakServiceImpl`, `FillinKeycloakUser`), and services validate incoming JWTs as OAuth2 resource servers (`spring-security-oauth2-jose`) rather than trusting the gateway alone.

**Event-Driven Registration Flow**: I decoupled member registration from the public-facing service using Kafka: `web-page` produces registration events (`KafkaProducerConfig`) that `server-register` consumes (`KafkaConsumerConfig`) to complete Keycloak user provisioning and persist membership/client records asynchronously, instead of blocking the user-facing request on that work.

**Hexagonal Domain Layer, Consistent Across All Five Services**: every service follows the same `application/Ports` (Drivers = inbound use-case interfaces, Drivens = outbound repository interfaces) / `infraestructure/Adapters` (Drivers = functional router endpoints, Drivens = repositories, DTOs, Keycloak/email/Kafka/Redis adapters) split, covering the full gym domain: clients, trainers, memberships, pools, work classes, schedules, specialties, promotions, and equipment.

Technologies: Java, Spring Boot 3.3 (Spring WebFlux, Spring Cloud Gateway, Spring Cloud Kubernetes, Spring Security OAuth2 Resource Server), Keycloak, Apache Kafka + Zookeeper, Redis, MySQL, Kubernetes (Deployments, ConfigMaps, Secrets).
