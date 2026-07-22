# Monster Gym — Ops Console (React 19 + TypeScript)

A new React 19 / TypeScript frontend built to consume the existing Spring Boot
backend (`web-page`, package `com.monster.web_page`). The backend had no
frontend code in it — this is a fresh client, not a migration of existing UI.

## Stack

- **React 19** + **TypeScript** (strict, via Vite's `react-ts` template)
- **Vite 8** — dev server + build, with a `/api` proxy to the backend in dev
- **React Router 7** — client-side routing
- **TanStack Query 5** — data fetching, caching, mutations
- **Axios** — typed HTTP client with a normalized `ApiError`
- **Tailwind CSS v4** — utility styling, wired via `@tailwindcss/vite`

## Getting started

```bash
npm install
cp .env.example .env      # optional — see below
npm run dev                # http://localhost:5173
```

By default, dev requests to `/api/*` are proxied to `http://localhost:8080`
(see `vite.config.ts`), so run the Spring Boot backend locally on port 8080
and the console will talk to it with no extra config.

For a production build pointing at a deployed backend, set
`VITE_API_BASE_URL` in `.env`:

```
VITE_API_BASE_URL=https://api.monstergym.com
```

```bash
npm run build     # type-checks (tsc -b) then builds to dist/
npm run preview   # serve the production build locally
```

## Project structure

```
src/
  api/endpoints.ts       # one function per backend route, grouped by domain
  types/api.ts           # TS mirrors of every Java DTO / record used over JSON
  lib/http.ts             # axios instance + ApiError normalization
  hooks/useApi.ts         # TanStack Query hooks wrapping api/endpoints.ts
  components/             # Layout, Card, DataTable, state views, client forms
  pages/                  # one page per resource area
```

## Backend endpoints covered

Every route exposed by the backend's `RouterFunction` beans has a
corresponding typed function in `src/api/endpoints.ts` and is used from at
least one page:

| Resource | Routes |
|---|---|
| Memberships | `GET /api/page/allMemberships` |
| Pools | `GET /api/page/allPools` |
| Specialties | `GET /api/page/allSpecialties` |
| Promotions | `GET /api/page/promotions/currentPromotions/{date}`, `GET /api/page/promotions/specificDate/{date}` |
| Work classes | `GET /api/page/workclasses`, `.../{name}/schedules`, `.../{name}/clients`, `.../{name}/trainers` |
| Trainers | `GET /api/page/allTrainers`, `GET /api/page/trainers/{username}/specialties` |
| Members | `POST /api/page/registerClient`, `GET .../{username}/allInformation`, `.../allClass`, and the `PUT`/`DELETE` mutations for username, email, membership, trainer, password, and account deletion |

Two things worth knowing when you touch this:

1. **There is no "list all members" endpoint.** The backend only supports
   looking a member up by username (or listing members of a given class), so
   the Members page is search-first rather than a full table.
2. **`GET /promotions/specificDate/{date}`** declares a `{date}` path
   variable in the router but the handler actually reads a `date` *query*
   param instead. The frontend sends both so it works either way — worth
   fixing on the backend side.

## Design

Industrial "gym signage" direction: charcoal surfaces, a hazard-yellow /
iron-rust two-accent palette, Bebas Neue for display type, Inter for body
copy, JetBrains Mono for data (times, ages, IDs). The signature motif is a
diagonal hazard-tape rule under page headers and a diagonally-clipped "plate
tab" badge used for real metadata (day of week, record IDs, live/ended
status) rather than as decoration.
