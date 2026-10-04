# Render Backend Deployment

## Required services

Render can host the Spring Boot backend, but this project still requires an external MySQL 8+ database. Use a managed MySQL provider such as Aiven, Railway, PlanetScale-compatible MySQL, AWS RDS, or a VPS database.

## Deploy

1. Push this repository to GitHub.
2. In Render, create a Blueprint from the repository.
3. Render will read `render.yaml` and build `Dockerfile.backend`.
4. Configure the `DB_HOST`, `DB_NAME`, `DB_USERNAME`, and `DB_PASSWORD` values for the external MySQL instance.
5. Set `APP_CORS_ALLOWED_ORIGINS` to the final Vercel frontend URL, for example:

```text
https://pmsss-frontend.vercel.app
```

6. Deploy and verify:

```text
https://<render-service>.onrender.com/api/health
```

## Vercel connection

Set this Vercel environment variable for the React project:

```text
VITE_API_BASE_URL=https://<render-service>.onrender.com/api/v1
```

Redeploy the frontend after setting the variable.

## Production notes

- Do not commit `.env` files or database credentials.
- Use a paid Render instance for production workloads; free instances may sleep.
- Restrict MySQL network access to the Render service where the provider supports allowlists.
- Keep `APP_JWT_SECRET` stable across redeployments or existing sessions will become invalid.
- The current backend uses the legacy JPA schema with `ddl-auto=update`; do not automatically apply the normalized Module 2 migrations until the compatibility migration is complete.