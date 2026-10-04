const assert = require('node:assert/strict');

const baseUrl = process.env.API_BASE_URL || 'http://localhost:8081/api/v1';
const uniqueEmail = `smoke-${Date.now()}@pmsss.local`;
const password = 'SmokeTest@123';

async function request(path, options = {}) {
  const response = await fetch(`${baseUrl}${path}`, {
    headers: { 'Content-Type': 'application/json', ...(options.headers || {}) },
    ...options,
  });
  let body = null;
  try { body = await response.json(); } catch {}
  return { response, body };
}

function tokenFrom(body) { return body?.data?.token; }
function refreshFrom(body) { return body?.data?.refreshToken; }

async function run() {
  let passed = 0;
  const check = async (name, action) => {
    await action();
    passed += 1;
    console.log(`[PASS] ${name}`);
  };

  await check('Public health endpoint is available', async () => {
    const healthUrl = baseUrl.replace(/\/api\/v1$/, '/api/health');
    const response = await fetch(healthUrl);
    assert.equal(response.status, 200);
  });

  await check('Unauthenticated /auth/me is rejected', async () => {
    const { response } = await request('/auth/me');
    assert.equal(response.status, 401);
  });

  await check('Invalid refresh token is rejected', async () => {
    const { response } = await request('/auth/refresh', {
      method: 'POST', body: JSON.stringify({ refreshToken: 'invalid-token' }),
    });
    assert.ok([400, 401].includes(response.status));
  });

  const registration = await request('/auth/register', {
    method: 'POST',
    body: JSON.stringify({
      firstName: 'Smoke', lastName: 'Student', username: 'smoke-student',
      email: uniqueEmail, mobileNumber: '9876543210', password,
      role: 'ROLE_ADMIN',
    }),
  });
  assert.equal(registration.response.status, 200);
  assert.equal(registration.body.data.user.role, 'student');
  assert.equal(registration.body.data.user.password, undefined);
  console.log('[PASS] Registration creates a student with safe response');
  passed += 1;

  await check('Duplicate registration is rejected', async () => {
    const { response } = await request('/auth/register', {
      method: 'POST', body: JSON.stringify({ firstName: 'Smoke', lastName: 'Student', email: uniqueEmail, mobile: '9876543210', password }),
    });
    assert.equal(response.status, 400);
  });

  await check('Weak registration password is rejected', async () => {
    const { response } = await request('/auth/register', {
      method: 'POST', body: JSON.stringify({ firstName: 'Weak', lastName: 'Student', email: `weak-${Date.now()}@pmsss.local`, mobile: '9876543211', password: 'weakpass' }),
    });
    assert.equal(response.status, 400);
  });

  const login = await request('/auth/login', { method: 'POST', body: JSON.stringify({ email: uniqueEmail, password }) });
  assert.equal(login.response.status, 200);
  const accessToken = tokenFrom(login.body);
  const refreshToken = refreshFrom(login.body);
  assert.ok(accessToken && refreshToken);
  console.log('[PASS] Login returns access and refresh tokens');
  passed += 1;

  await check('Authenticated /auth/me returns safe user data', async () => {
    const { response, body } = await request('/auth/me', { headers: { Authorization: `Bearer ${accessToken}` } });
    assert.equal(response.status, 200);
    assert.equal(body.data.role, 'student');
    assert.equal(body.data.password, undefined);
    assert.equal(body.data.passwordHash, undefined);
  });

  await check('Wrong password is rejected', async () => {
    const { response } = await request('/auth/login', { method: 'POST', body: JSON.stringify({ email: uniqueEmail, password: 'Wrong@12345' }) });
    assert.equal(response.status, 401);
  });

  const refreshed = await request('/auth/refresh', { method: 'POST', body: JSON.stringify({ refreshToken }) });
  assert.equal(refreshed.response.status, 200);
  const rotatedRefreshToken = refreshFrom(refreshed.body);
  assert.ok(tokenFrom(refreshed.body) && rotatedRefreshToken);
  console.log('[PASS] Refresh rotates tokens');
  passed += 1;

  await check('Rotated refresh token cannot be reused', async () => {
    const { response } = await request('/auth/refresh', { method: 'POST', body: JSON.stringify({ refreshToken }) });
    assert.ok([400, 401].includes(response.status));
  });

  await check('Student cannot access admin API', async () => {
    const { response } = await request('/admin/users', { headers: { Authorization: `Bearer ${accessToken}` } });
    assert.equal(response.status, 403);
  });

  await check('Logout revokes refresh token', async () => {
    const logout = await request('/auth/logout', { method: 'POST', headers: { Authorization: `Bearer ${accessToken}` }, body: JSON.stringify({ refreshToken: rotatedRefreshToken }) });
    assert.equal(logout.response.status, 200);
    const { response } = await request('/auth/refresh', { method: 'POST', body: JSON.stringify({ refreshToken: rotatedRefreshToken }) });
    assert.ok([400, 401].includes(response.status));
  });

  console.log(`Completed ${passed} backend smoke checks.`);
}

run().catch((error) => {
  console.error('[FAIL]', error.message);
  process.exitCode = 1;
});