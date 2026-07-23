# Kafka Notes — GymMonster

Doc 4 deliverable. Much simpler topology than food-ordering-system: one topic (`flow`, single partition per the compose `KAFKA_CREATE_TOPICS`), one producer (`web-page`), one consumer (`server-register`), direct produce-on-request rather than an outbox pattern.

## Critical bug, fixed: the consumer was committing offsets before doing the work

`RegisterService.consumer()` (the `@KafkaListener` handling every new-member registration) called `.subscribe()` on its reactive processing chain and returned immediately. `ConcurrentKafkaListenerContainerFactory` (the imperative, non-reactive container actually configured here) commits the Kafka offset as soon as the listener **method** returns - not when a subscribed-but-still-running reactive chain finishes. `subscribe()` kicks work off asynchronously and returns instantly, so in practice: the offset advanced, Kafka considered the registration event fully processed, and the actual client record, Keycloak user, and confirmation email were still being created in the background - or not, if the process crashed or a downstream call failed in that window. A failure showed up only as a `System.out.println` inside the async chain, never as anything Kafka's redelivery or this app's own error handling could act on. This is a real, live, silent-data-loss bug in the core registration flow.

Fixed by blocking on the chain (so the offset only commits after the work genuinely completes) and throwing when the accumulated error list is non-empty (so a real failure engages the container's error handler instead of looking identical to success). Added a bounded `DefaultErrorHandler` (3 retries, 1s apart, then log-and-move-on) alongside it, matching the same pattern used in food-ordering-system - previously there was no error handler configured at all, meaning whatever Spring Kafka's implicit default is for this version was in effect silently.

## Idempotence

Producer: `enable.idempotence`/`acks=all` made explicit (was relying on the Kafka client library's own version-dependent default - Kafka 3.0+ defaults these on, earlier versions don't; explicit now regardless of which `kafka-clients` version resolves).

Consumer: no formal inbox/dedup table, but `existsByUsername`/`existsByEmail` checks at the top of `createClient` mean a genuine redelivery of an already-fully-completed registration gets caught as a validation error ("username already exists") rather than silently creating a duplicate client. This is real idempotency, just implicit rather than a named pattern - same category of finding as furniture_store's unique-constraint-as-inbox.

**Real gap this surfaces, not fixed here**: those same existence checks mean a retry after a *partial* failure (client/detail/inscription DB rows already committed, but Keycloak provisioning or the confirmation email failed) will incorrectly report "already exists" on redelivery, even though the registration is genuinely stuck incomplete - there's no compensation or resume-from-where-it-failed logic. This is really a saga-completeness question, not a pure Kafka one - covered in the Saga/architecture assessment note rather than duplicated here.

## Partitioning / keying

Single partition on the `flow` topic - the "wrong partition key breaks ordering" class of bug that was real and worth fixing in food-ordering-system doesn't apply here, since there's nothing to reorder across when there's only one partition. Worth revisiting if this topic is ever scaled to multiple partitions (would need a key - `clerkId`/username - to keep a given user's registration events, if there are ever more than one, in order).

## Consumer group naming

`GROUP_ID_CONFIG` is hardcoded to the literal string `"grupo1"` - works, but a more descriptive group id (e.g. `server-register-flow-consumer`) would make consumer-group state easier to identify in Kafka tooling/monitoring later. Cosmetic, not fixed given the scope of the bug above.
