# chat-app (Backend)

Spring Boot REST + WebSocket backend for the Vue SPA. It handles authentication, sessions, and message delivery via RabbitMQ STOMP relay.

See also:
- Frontend: `github.com/Ruclo/chat-vue`

**Architecture**
1. Client sends chat message over WebSocket (STOMP).
2. Backend persists to Postgres.
3. Backend publishes to RabbitMQ exchange.
4. RabbitMQ routes to per-user queues.
5. Backend relays to connected clients.

**Message Flow**
`Browser (STOMP) -> Backend -> Postgres -> RabbitMQ -> Backend (STOMP) -> Browsers`

## Auth Design
JWT-based auth with refresh + access tokens stored as HttpOnly cookies:
- `access_token`: short-lived, used for API and WebSocket auth.
- `refresh_token`: long-lived, used to rotate access tokens.

**Rotation + Theft Detection**
- Each refresh token has a `successionId`.
- The refresh token hash is stored in DB.
- On refresh, the token rotates and the hash updates.
- If the same `successionId` is used with a different hash, all sessions are invalidated (suspected theft).

**WebSockets**
- WebSocket connections are tied to the access token.
- If the access token expires and isn’t refreshed, the socket is disconnected.

## Config
Environment variables (see `.env.example`):
- `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_PASSWORD`
- `BROKER_HOST`, `BROKER_PORT`, `BROKER_USER`, `BROKER_PASSWORD`
- `FRONTEND_URL`
- `RSA_PRIVATE_KEY`, `RSA_PUBLIC_KEY`
- `CLOUDINARY_URL`

`RSA_PRIVATE_KEY` and `RSA_PUBLIC_KEY` are **file paths** when using Docker Compose secrets:
```
RSA_PRIVATE_KEY=file:/run/secrets/rsa_private_key
RSA_PUBLIC_KEY=file:/run/secrets/rsa_public_key
```

## Notes
- For production, use TLS. Cookies are `Secure` and won’t be sent over plain HTTP.
- `FRONTEND_URL` should match the public origin of the proxy (e.g., `https://chat.example.com`).
