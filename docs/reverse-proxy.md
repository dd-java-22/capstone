# Reverse Proxy (Apache) Headers

The Spring Boot service runs behind Apache, which terminates TLS and forwards requests to the
internal Spring connector (e.g., `:8080`). For the backend to generate correct public URLs
(especially `/users/{externalId}/avatar`), Apache must forward the public request context and Spring
must honor it.

## Apache (example)

Ensure proxying preserves the public host and tells the backend the public scheme/port:

```apacheconf
ProxyPreserveHost On
RequestHeader set X-Forwarded-Proto "https"
RequestHeader set X-Forwarded-Port "443"
```

Do not duplicate these directives if they already exist; configure them once in the relevant
`<VirtualHost *:443>` block.

## Spring Boot

Spring Boot is configured to honor forwarded headers via:

`server.forward-headers-strategy=framework`

This allows `ServletUriComponentsBuilder.fromCurrentContextPath()` to build URLs using the public
`https://{host}` rather than the internal `http://{host}:8080`.

