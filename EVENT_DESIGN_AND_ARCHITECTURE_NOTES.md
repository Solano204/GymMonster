# Async Event Design + Architecture Notes — GymMonster

Doc 5 deliverable, plus the saga-applicability and Redis-modeling assessment (they turned out to be closely related once I traced the registration flow, so covering them together rather than splitting into separate thin documents).

## CRITICAL, flagged (not fixed this pass): the registration event carries the plaintext password

`web-page`'s `ClientRepository.registerNewClient` publishes the `AllClient` record - the same record the REST layer receives from the signup form - directly to the `flow` Kafka topic (`postmanRegistration.send("flow", newClient)`), no translation layer. `AllClient` includes the user's plaintext `password` field (confirmed: `RegisterService.createClient` in `server-register` calls `passwordEncoder.encode(client.password())`, meaning the encoding happens on the *consuming* side, after the event has already traveled through Kafka).

This means the plaintext password sits in a Kafka message - persisted to disk per the topic's retention policy, readable by anything with topic access (any future consumer, an ops tool like Kafdrop if one gets added, broker-level log inspection). This is the same root issue as the REST-layer password-in-URL finding (Doc 1), just a different transport, and arguably worse here since Kafka messages are retained by design rather than appearing transiently in a log line.

**Recommended fix** (not implemented this pass - real work, and moving where password hashing happens needs to be done carefully): hash the password in `web-page` *before* publishing, using the same `PasswordEncoder` bean `server-register` already uses (Spring Security's `BCryptPasswordEncoder` or whichever is configured - would need adding as a dependency/bean to `web-page` if not already present there), and have `RegisterService` store the already-hashed value instead of encoding it itself. This is also the textbook fix Doc 5's own "domain event vs integration event" phase calls for: `AllClient` is being reused as both the REST DTO and the wire event with zero translation, so the event happens to carry a field it never should.

## Event taxonomy

`AllClient` is ECST (full payload, no callback needed by `server-register`) - correct choice for a cross-service event, no gap here beyond the password field above.

## Saga applicability: real, but incomplete rather than absent

The registration flow (`web-page` publishes → `server-register` consumes: validate → persist client/detail/inscription rows → provision Keycloak user → send confirmation email) is genuinely a multi-step process with partial-failure exposure, which is what Doc 10 (Saga) would normally apply to. It's simpler than food-ordering-system's choreographed saga (one consumer completing several steps, not a multi-hop exchange across 3+ services), so a full saga-pattern writeup (state machine, compensating transactions per step) would be over-engineering for what's here today.

What's real and already surfaced by tracing this flow for Doc 4: a retry after a *partial* failure (DB rows committed, Keycloak provisioning or email failed) gets misreported as "username already exists" rather than resuming from where it actually stopped - there's no state tracking distinguishing "fully registered" from "partially registered, needs to finish Keycloak+email." If this system grows a second async multi-step flow, or this one needs to become reliably resumable, that's when the full Saga document's step-classification and compensation-design phases would earn their cost. Documenting as the concrete trigger condition rather than applying the pattern preemptively.

## Redis: session/cache store, not a modeled document database

Confirmed by reading both usages (`gateway`'s `RedisTokenValidationFilter`, `web-page`'s `redisConfiguration`): Redis here is pure key-value session/token storage and caching, not something with the kind of schema-design decisions (embedding vs referencing, compound keys, partition design) the NoSQL modeling document exists to review. No dedicated NoSQL document warranted - noting explicitly rather than silently skipping.
