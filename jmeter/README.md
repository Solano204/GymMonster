# JMeter load tests

Three test plans against the gateway (default `localhost:8081`), covering the three
main traffic patterns through the system:

- **`public-browsing-flow.jmx`** — anonymous visitors hitting web-page's public listing
  endpoints (memberships, pools, specialties, work classes). 30 threads, 10 loops each,
  15s ramp-up. No auth needed.
- **`registration-flow.jmx`** — the registration entry point
  (`POST /api/page/registerClient`), which is what kicks off the Kafka-driven
  web-page → server-register flow. 10 threads, 3 loops, unique username/email per
  request via JMeter's `__UUID()` function so repeated runs don't all collide on
  "already exists" validation errors.
- **`login-session-flow.jmx`** — logs in (`POST /GymMonster/auth/login`), extracts the
  Keycloak access token from the response, and uses it as a Bearer token against a
  protected server-administrator endpoint (`GET /api/admin/clients`) through the
  gateway. Requires a real Keycloak user — set `USERNAME`/`PASSWORD` before running
  (either edit the User Defined Variables in the GUI, or pass
  `-JUSERNAME=... -JPASSWORD=...` on the command line).

## Running

Requires the full stack up (docker-compose or Kubernetes) and a working JMeter
install. From this directory:

```
jmeter -n -t public-browsing-flow.jmx -l results-browsing.jtl -e -o report-browsing/
jmeter -n -t registration-flow.jmx -l results-registration.jtl -e -o report-registration/
jmeter -n -t login-session-flow.jmx -l results-login.jtl -e -o report-login/ -JUSERNAME=<user> -JPASSWORD=<pass>
```

`-e -o <dir>` generates an HTML dashboard report after the run. Override
`HOST`/`PORT` the same way if the gateway isn't on `localhost:8081`
(`-JHOST=... -JPORT=...`).

Each plan's Summary Report listener prints live throughput/error-rate to the console;
the `.jtl` file has the full per-sample results for deeper analysis.
