# SQL Notes — GymMonster (MySQL)

Doc 6 deliverable.

## Fixed: no uniqueness constraint on username/email anywhere

`clients`, `per_trainer`, and `trainers_class` all store `username`/`email` as plain `NOT NULL` columns with zero uniqueness enforcement at the database level - registration relies entirely on `existsByUsername`/`existsByEmail` application checks run *before* the insert. That's a real race condition (TOCTOU): two concurrent registration requests for the same username can both pass the check before either commits, producing two "unique" accounts with the same username. Added `UNIQUE` constraints on all three tables' `username`/`email` columns in `init.sql` - the database is now the actual enforcement point, not just an app-level check with a race window.

## FK indexing: already fine, no action needed

Unlike food-ordering-system's Postgres schema, MySQL/InnoDB auto-creates an index on a column the moment it's used in a `FOREIGN KEY` constraint - every FK column here (`id_membership`, `id_trainer`, `id_detail`, `id_client`, the join tables' composite keys) is already indexed as a side effect of the FK declaration itself. Confirmed this is genuinely how InnoDB works before concluding "no gap here," rather than assuming the same finding from the Postgres-based projects would carry over unchanged.

## Migrations: no tool, plain SQL scripts

`init.sql`/`insertions.sql`/`deletion.sql` are hand-run scripts, not a migration tool with history (no Flyway/Liquibase) - same category of gap as food-ordering-system's `init-schema.sql` and furniture_store's `prisma db push`, though less severe here since `init.sql` isn't re-run destructively on every boot (it's a one-time setup script, not wired into `spring.sql.init.mode=ALWAYS` the way food-ordering-system's was). Flagging for awareness, not fixing: adopting Flyway here would mean converting these hand-run scripts into versioned migrations across 5 services, a bigger deliberate piece of work than fits in this pass.
