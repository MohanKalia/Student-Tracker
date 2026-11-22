import test from 'node:test';
import assert from 'node:assert/strict';
import { readFile } from 'node:fs/promises';
import vm from 'node:vm';
import { distinctValues, filterStudents, toCsv } from '../../main/resources/static/students.mjs';

const source = (await readFile(new URL('../../main/resources/static/app.js', import.meta.url), 'utf8'))
  .replace(/^import .*;\n/, '');
const authorizationKey = 'student-tracker.authorization';
const authorization = `Basic ${btoa('mary:test123')}`;

async function openDashboard(storage = new Map(), { status = 200, offline = false } = {}) {
  const elements = new Map();
  function element(id) {
    if (!elements.has(id)) elements.set(id, {
      value: id === 'page-size' ? '10' : '', options: [], dataset: {}, hidden: true, open: false,
      listeners: {}, textContent: '',
      addEventListener(type, handler) { this.listeners[type] = handler; },
      setAttribute() {},
      querySelector() { return element(`${id}-button`); },
      closest() { return element(`${id}-parent`); },
      replaceChildren(...options) { this.options = options; },
      reset() {},
      close() { this.open = false; },
      showModal() { this.open = true; }
    });
    return elements.get(id);
  }
  const requests = [];
  const context = vm.createContext({
    document: { getElementById: element, querySelectorAll: () => [] },
    sessionStorage: {
      getItem: key => storage.get(key),
      setItem: (key, value) => storage.set(key, value),
      removeItem: key => storage.delete(key)
    },
    fetch: async (path, options) => {
      requests.push({ path, ...options });
      if (offline) throw new Error('Offline');
      return {
        ok: status === 200, status,
        headers: { get: () => 'application/json' },
        json: async () => path === '/api/me' ? { username: 'mary', roles: ['STUDENT', 'PROFESSOR'] } : []
      };
    },
    Option: class { constructor(text, value) { this.text = text; this.value = value; } },
    FormData: class { get(key) { return key === 'username' ? 'mary' : 'test123'; } },
    AbortSignal, TextEncoder, btoa, distinctValues, filterStudents, toCsv
  });
  vm.runInContext(source, context);
  // Finish startup's asynchronous identity validation and directory load.
  await new Promise(resolve => setImmediate(resolve));
  return { element, requests, storage };
}

test('sign-in survives refresh, revalidates identity, and sign-out clears it', async () => {
  const storage = new Map();
  const first = await openDashboard(storage);
  assert.equal(first.element('login-dialog').open, true);
  assert.equal(first.requests.length, 0);

  await first.element('login-form').listeners.submit({
    preventDefault() {}, currentTarget: first.element('login-form')
  });
  assert.equal(storage.get(authorizationKey), authorization);
  assert.equal(first.element('login-dialog').open, false);

  const refreshed = await openDashboard(storage);
  assert.deepEqual(refreshed.requests.map(request => request.path), ['/api/me', '/api/students']);
  assert.ok(refreshed.requests.every(request => request.headers.Authorization === authorization));
  assert.equal(refreshed.element('login-dialog').open, false);
  assert.equal(refreshed.element('account-role').textContent, 'mary · Professor');
  assert.equal(refreshed.element('add-student').disabled, false);

  refreshed.element('sign-out').listeners.click();
  assert.equal(storage.has(authorizationKey), false);
  assert.equal(refreshed.element('login-dialog').open, true);
  const signedOut = await openDashboard(storage);
  assert.equal(signedOut.element('login-dialog').open, true);
  assert.equal(signedOut.requests.length, 0);
});

test('rejected saved credentials are cleared and prompt for sign-in', async () => {
  const storage = new Map([[authorizationKey, authorization]]);
  const dashboard = await openDashboard(storage, { status: 401 });
  assert.equal(storage.has(authorizationKey), false);
  assert.equal(dashboard.element('login-dialog').open, true);
  assert.match(dashboard.element('login-error').textContent, /not accepted/);
  assert.deepEqual(dashboard.requests.map(request => request.path), ['/api/me']);
});

test('a temporary connection failure preserves saved sign-in for the next refresh', async () => {
  const storage = new Map([[authorizationKey, authorization]]);
  const dashboard = await openDashboard(storage, { offline: true });
  assert.equal(storage.get(authorizationKey), authorization);
  assert.equal(dashboard.element('login-dialog').open, true);
  assert.match(dashboard.element('login-error').textContent, /Could not reach/);
  const refreshed = await openDashboard(storage);
  assert.equal(refreshed.element('login-dialog').open, false);
});
