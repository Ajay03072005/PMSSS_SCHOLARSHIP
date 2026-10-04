# Railway Backend Deployment

## 1. Create the project

In Railway:

1. Create a new project.
2. Add a **MySQL** service.
3. Add this GitHub repository as a service.
4. Set the service root to the repository root. Railway will use `railway.json` and `Dockerfile.backend`.

## 2. Configure backend variables

Map the Railway MySQL service variables to the backend service:

```text
DB_HOST=${{MySQL.MYSQLHOST}}
DB_PORT=${{MySQL.MYSQLPORT}}
DB_NAME=${{MySQL.MYSQLDATABASE}}
DB_USERNAME=${{MySQL.MYSQLUSER}}
DB_PASSWORD=${{MySQL.MYSQLPASSWORD}}
```

Also set:

```text
APP_JWT_SECRET=<generate-a-stable-secret>
APP_CORS_ALLOWED_ORIGINS=https://<your-vercel-domain>
```

Set Cloudinary variables if document storage is enabled:

```text
CLOUDINARY_CLOUD_NAME=...
CLOUDINARY_API_KEY=...
CLOUDINARY_API_SECRET=...
```

## 3. Deploy and verify

Generate a Railway public domain for the backend, then verify:

```text
https://<railway-domain>/api/health
```

Run the API smoke suite locally against Railway:

```powershell
$env:API_BASE_URL = 'https://<railway-domain>/api/v1'
node scratch/backend-smoke-test.js
```

## 4. Connect Vercel

Set this Vercel environment variable:

```text
VITE_API_BASE_URL=https://<railway-domain>/api/v1
```

Redeploy the frontend after setting it.

Do not commit Railway, MySQL, JWT, or Cloudinary secrets.