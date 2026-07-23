# Security policy

This private repository is not yet beta-ready.

Report suspected vulnerabilities privately to the repository owner. Do not place
credentials, tokens, personal information, report contents, document contents, or
exploit details in a public channel or ordinary issue.

The current audit identifies a critical trust-boundary gap: browser-supplied
`X-User-Id` values are accepted as reporting identity without token validation in
this service. Treat the service as unsuitable for user data until authenticated
identity, downstream propagation, cross-user tests, and safe logging are complete.

Rotate any accidentally disclosed secret and remove it from all affected systems;
do not rely on deleting a Git commit as the remediation.
