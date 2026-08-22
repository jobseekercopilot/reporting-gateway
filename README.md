# Reporting Gateway

## Role in Job Seeker Copilot

| Role | Called by | Calls | Data | Local port |
|---|---|---|---|---:|
| JWT-protected reporting facade | Client Express BFF | Reporting Service | None | 8095 |

See the central [reporting journey](https://docs.jobseekercopilot.com/journeys/reporting-payments/), [gateway guide](https://docs.jobseekercopilot.com/services/frontend-gateways/), and [API map](https://docs.jobseekercopilot.com/apis/overview/).

Browser-facing Spring Boot gateway for the Job Seeker Copilot reporting APIs.

## Current scope

The gateway exposes three reporting operations:

- `GET /api/v1/reports/summary`
- `GET /api/v1/reports/uc-journal`
- `GET /api/v1/reports/evidence.txt`

The inherited generated-client JAR dependency has been replaced with a
source-controlled HTTP adapter. The remaining reporting product gaps are recorded
in [`docs/BETA_READINESS_AUDIT.md`](docs/BETA_READINESS_AUDIT.md).

The gateway is the reporting authentication boundary. It validates signed access
tokens against the configured issuer, audience, token type and JWKS endpoint,
derives report ownership exclusively from the token subject, and ignores
caller-supplied ownership headers. It forwards the validated bearer value, the
trusted subject and its dedicated service identity to Reporting Service.

## Build

Java 17 and Maven are required.

```bash
mvn -B clean verify
```

The build is reproducible from source and does not require a locally supplied JAR.
It also writes a CycloneDX 1.6 runtime SBOM to
`target/classes/META-INF/sbom/application.cdx.json` and packages it in the
application JAR.

The complete local dependency-security gate is:

```bash
./scripts/verify-supply-chain.sh
```

The gate uses a pinned, free local Trivy container and never exposes the Docker
socket to the scanner. See
[`docs/SECURITY_VERIFICATION.md`](docs/SECURITY_VERIFICATION.md) for the dated
results, offline mode and remaining image/TLS evidence.

Build and scan the complete local image through the least-privilege archive
boundary:

```bash
docker build --tag jobseekercopilot/reporting-gateway:local .
REPORTING_TRIVY_OFFLINE=true \
  ./scripts/verify-image-security.sh jobseekercopilot/reporting-gateway:local
```

Runtime startup fails closed unless these settings are configured:

- `AUTH_JWKS_URI`
- `REPORTING_GATEWAY_JWT_ISSUER`
- `REPORTING_GATEWAY_JWT_AUDIENCE`
- `REPORTING_GATEWAY_SERVICE_TOKEN` (at least 32 bytes)

Runtime OpenAPI and Swagger UI endpoints are disabled by default. The reviewed
contract remains available in source; operators may enable the runtime endpoints
explicitly with `OPENAPI_DOCS_ENABLED=true` or `SWAGGER_UI_ENABLED=true`.

## API contract

The reviewed version 2 contract is in `contracts/openapi.json`. The test suite
fails if runtime-generated OpenAPI drifts from that file.

## Licence

Proprietary and confidential. See `LICENSE`.
