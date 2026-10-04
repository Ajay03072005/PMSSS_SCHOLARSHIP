# Docker/VPS Deployment

## Local container validation

1. Install and start Docker Desktop with the Linux engine enabled.
2. Copy `.env.example` to `.env`.
3. Replace every placeholder secret in `.env`.
4. Run:

```powershell
docker compose up --build -d
```

5. Verify:

```powershell
Invoke-WebRequest http://localhost/api/health
node scratch/backend-smoke-test.js
```

6. Stop the stack with `docker compose down`.

The current backend uses its legacy JPA model with `ddl-auto=update`. The normalized SQL in `database/migrations` is not automatically applied because it requires a compatibility/data migration first.

## VPS deployment

- Install Docker Engine and Compose on the VPS.
- Copy the repository and a host-only `.env` file to the deployment directory.
- Restrict inbound access to ports 80/443; keep MySQL private to the Compose network.
- Run `docker compose up --build -d`.
- Put TLS termination in front of port 80 using the VPS reverse proxy or a managed load balancer.
- Set `APP_CORS_ALLOWED_ORIGINS` to the real frontend origin.
- Back up the `mysql_data` volume before upgrades.

Do not commit `.env`, database passwords, JWT secrets, or Cloudinary credentials.