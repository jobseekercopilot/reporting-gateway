# Reporting Gateway beta-readiness audit

Audit date: 2026-07-23  
Decision: **Not ready for private beta**

This document records the inherited implementation without changing its product
behaviour. The repository was created as a sanitised audit baseline only.

## Verified behaviour

- Exposes summary and `uc-journal` endpoints under `/api/v1/reports`.
- Requires a non-blank `X-User-Id` header but does not validate an access token or
  derive identity from authentication.
- Delegates to a generated reporting-service client.
- Propagates correlation IDs and exposes the Spring Boot health endpoint.
- Produces an OpenAPI document during tests.
- With the inherited untracked client JAR present, `mvn -q clean verify` passed
  3 tests (0 failures, 0 errors, 0 skipped).

## Clean-clone and supply-chain result

- The POM uses Maven `systemPath` for
  `libs/reporting-service-client-1.0.0.jar`.
- Generated binaries are excluded from this repository by design.
- A clean clone therefore cannot compile or test.
- The Dockerfile also copies `libs`, so its build is not reproducible from this
  repository.
- The API contract is captured in `contracts/openapi.json`, but no reproducible
  contract-to-client pipeline is present.

Unblock condition: publish or generate the client through an approved, versioned
build workflow; remove `systemPath` and the Docker dependency on untracked binaries;
then prove a clean-clone build and CI.

## Security and privacy findings

### Critical: browser-controlled identity

The Angular/BFF path can forward a browser-provided `X-User-Id`, and this gateway
accepts that value as the reporting identity. The service does not validate a JWT,
derive the subject from a trusted security context, or reject a mismatched supplied
ID. This can expose another user's reporting data if downstream services also trust
the header.

Unblock condition: derive user identity from validated authentication, remove the
browser-controlled identity path, scope every downstream request to that identity,
and add missing/invalid/expired/forged-token plus cross-user tests.

### High: sensitive identifiers in logs

Raw user IDs and application totals are logged. Full report contents are not logged
in the inspected source, but identifiers and result metadata need a documented
redaction policy.

### High: downstream resilience is undefined

No explicit connect/read timeouts, retry rules, circuit breaker, or safe error
contract is present. Access-denied metrics are absent.

## Functional classification

| Capability | Result | Evidence |
|---|---|---|
| Summary forwarding | Incomplete | Endpoint exists; identity is untrusted. |
| Journal forwarding | Incomplete | Endpoint exists but uses prohibited product terminology. |
| Date-range reports | Absent, required | No date parameters or rules. |
| Pagination/filtering | Absent, required | No detailed collection contract. |
| Report review/export | Absent, required | No gateway flow or ownership checks. |
| Deterministic client contract | Incomplete | Captured OpenAPI; client build is not reproducible. |
| Health | Working, limited | Actuator health is exposed; readiness dependencies are not proven. |
| Reporting metrics | Absent, required | No latency/failure/empty/access-denied measures. |

## Required follow-up

The GitHub Reporting epic contains the focused issues for architecture, metrics and
date rules, authentication, deterministic reports, evidence report/export,
performance, privacy, observability, security, and browser E2E. The epic remains in
Backlog and no issue was implemented during this audit.
