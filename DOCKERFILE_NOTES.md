# Dockerfile Notes — GymMonster

Doc 3 deliverable. All 5 Dockerfiles (gateway, server-administrator, server-informations, server-register, web-page) are generated from the same template - identical structure, same fixes applied uniformly across all 5.

## Fixed, all 5 services

- **Non-root user**: `eclipse-temurin:21-jre` runs as root by default (unlike Confluent/Paketo images) - none of the 5 had a `USER` directive. Added `groupadd`/`useradd` + `chown` the jar + `USER spring` to each final stage.
- **Graceful shutdown**: none had `server.shutdown=graceful` configured - added to all 5 `application.properties`. Matters most for `server-register` (a Kafka consumer completing multi-step registration work per message - see Doc 4/5) and `gateway` (in-flight proxied requests), but applied uniformly for consistency.

## Already good, no change needed

Multi-stage builds with dependency-layer caching (pom.xml copied and dependency-resolved before source, matching food-ordering-system's and furniture_store's already-correct pattern) and exec-form `ENTRYPOINT` were already in place on all 5 - no fix needed there.

## Flagged, not fixed

- Base images (`eclipse-temurin:21-jdk`/`21-jre`) pinned to major-version-only tags, same reproducibility gap noted in the other two projects' Doc 3 - not pinning to an exact patch/digest without network access to verify a current tag exists.
- No `.dockerignore` in any of the 5 service directories - `target/` and `.git` get sent to the build context unnecessarily. Low-priority/mechanical; worth adding but not done across 5 near-identical files in this pass.
