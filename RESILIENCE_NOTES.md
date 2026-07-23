# Resilience Notes — GymMonster

Doc 9 deliverable. Unlike food-ordering-system, this system has real synchronous inter-service calls (gateway → downstream services, server-administrator → server-informations, Keycloak provisioning calls) - the classic resilience stack applies directly here.

## Fixed: zero timeouts anywhere, across all 3 shared WebClient configs

`gateway`, `server-administrator`, and `web-page` each declare a shared `@LoadBalanced WebClient.Builder` bean with no timeout configuration at all - every downstream call (gateway → any backend service, server-administrator → server-informations, any → Keycloak) could hang indefinitely on a slow or unresponsive peer. This mattered most at the gateway: a hung downstream call there hangs every client request routed through it, not just one service's own work.

Added connect (3s) and read/write (5s) timeouts via a configured Reactor Netty `HttpClient` on all 3 - simple CRUD proxying and Keycloak calls, not long-running work, so these are reasonable starting numbers rather than tuned-from-load-data ones (same honesty as the other resilience passes this session: real numbers need real traffic to tune, these are deliberately conservative defaults to replace "none at all").

## Found and removed while fixing this: two dead, actively-wrong `keycloakWebClient` beans

Both `web-page` and `gateway` declared a `keycloakWebClient()` bean that nothing in either module actually injects (confirmed by search, not assumed) - and what they had was wrong on its own terms: `@LoadBalanced` combined with a fixed `baseUrl`, which is contradictory (`@LoadBalanced` exists to resolve a logical service name *through* the load balancer; a literal URL bypasses that entirely). `web-page`'s version was worse still - hardcoded to `http://localhost:8111`, which is `server-informations`'s port, not Keycloak's (8181), under a comment claiming otherwise. Removed both rather than fixing them in place, since dead code that's also wrong isn't worth preserving as a template for whoever copies it next.

## Circuit breakers / bulkheads: not applied this pass

Real candidates exist (gateway → downstream services, any → Keycloak), but circuit-breaker/bulkhead configuration needs real traffic and failure-rate data to threshold sensibly - timeouts are the correct first layer (this doc's own Fase 1: everything else in the stack depends on timeouts existing first) and are now in place everywhere they were missing. Circuit breakers are the natural next step once there's production-shaped traffic to calibrate against, not before.

## Kafka consumer resilience: covered in Doc 4

Bounded retry + explicit idempotence already covered there - not duplicating here.
