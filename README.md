# Reporting Gateway

Browser-facing Spring Boot gateway for the Job Seeker Copilot reporting APIs.

## Current scope

The inherited baseline forwards two reporting operations:

- `GET /api/v1/reports/summary`
- `GET /api/v1/reports/uc-journal`

This repository is an audit baseline, not a beta-ready release. The source currently
depends on a locally supplied generated `reporting-service-client` JAR. That binary
is intentionally not committed. See
[`docs/BETA_READINESS_AUDIT.md`](docs/BETA_READINESS_AUDIT.md) for the verified
build limitation and the security and product gaps recorded for follow-up.

## Build

Java 17 and Maven are required.

```bash
mvn -B clean verify
```

The command will fail in a clean clone until the generated-client dependency is
made reproducible. This is tracked as a beta blocker; do not work around it by
committing generated JARs.

## API contract

The contract captured during the source audit is in `contracts/openapi.json`.
Contract generation currently occurs during the test suite and also depends on the
missing generated client.

## Licence

Proprietary and confidential. See `LICENSE`.
