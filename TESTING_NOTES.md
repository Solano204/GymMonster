# Testing Notes — GymMonster

Doc 7 deliverable.

## Coverage is uneven, and it shows

| Service | Test files | Notable |
|---|---|---|
| gateway | 7 | Has its own Redis-token-validation integration suite (per SERVICES.md) |
| server-informations | 6 | Keycloak service + client repository integration tests |
| server-register | 6 | `RegisterServiceKafkaListenerIntegrationTest` (`@EmbeddedKafka`) - good choice over Testcontainers-Kafka for this: proves the wire contract without a real broker |
| web-page | 4 | Includes `RouterRegisterTest`, the reference pattern for Doc 1's credentials-in-body fix |
| server-administrator | **2** | Lowest of the 5 |

`server-administrator` having the thinnest coverage isn't a coincidence relative to what Doc 1 found: it's the service where I found both the password-in-URL issue *and* a confirmed crash bug (`deleteClient` reading a path variable the route never declares). That crash bug is the exact same class of bug `ClientRouterTest` already exists to catch - its own doc comment says it's a regression test for a *different* handler in the same router reading the wrong path variable name and throwing `IllegalArgumentException` on every call. The pattern to catch this was already proven in this codebase; it just wasn't extended to every handler in the file.

**Recommended immediate follow-up** (not done in this pass - see Doc 1): extend `ClientRouterTest`'s pattern to the `changePassword`/`deleteClient` fixes, and replicate `RouterRegisterTest`'s pattern (the one this session's Doc 1 fixes were themselves modeled on) to the other 3 newly-fixed router/handler pairs in `server-administrator` and `server-informations`.

## Minor: unused Testcontainers dependency

`server-informations/pom.xml` declares `testcontainers` as a dependency, but nothing in its test sources actually uses `@Container`/`@Testcontainers` - confirmed by search, not assumed. Not removing it this pass (a missing dependency is a build break; an unused one is just dead weight) but worth either using it for a real integration test (the Keycloak service test SERVICES.md mentions would be the natural candidate - a Testcontainers Keycloak instance instead of whatever it currently mocks/stubs against) or removing it, rather than leaving intent unfollowed-through.
