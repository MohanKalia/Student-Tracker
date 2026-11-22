import { distinctValues, filterStudents, toCsv } from './students.mjs';

const $ = id => document.getElementById(id);
const icons = {
  users: '<path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2M22 21v-2a4 4 0 0 0-3-3.87M16 3.13a4 4 0 0 1 0 7.75"/><circle cx="9" cy="7" r="4"/>',
  chart: '<path d="M4 3v17h17M9 15v-4M14 15V7M19 15v-6"/>',
  cap: '<path d="m2 9 10-5 10 5-10 5Zm4 3v6c4 3 8 3 12 0v-6M22 9v8"/>',
  building: '<path d="m3 9 9-6 9 6M3 21h18M5 10v8M10 10v8M14 10v8M19 10v8M2 9h20"/>',
  shield: '<path d="M12 3 4 6v6c0 5 8 9 8 9s8-4 8-9V6ZM8 12l3 3 5-6"/>',
  plus: '<path d="M12 5v14M5 12h14"/>',
  sparkles: '<path d="m12 3 2.5 6.5L21 12l-6.5 2.5L12 21l-2.5-6.5L3 12l6.5-2.5ZM20 2v4M18 4h4"/>',
  search: '<circle cx="10.5" cy="10.5" r="6.5"/><path d="m16 16 5 5"/>',
  download: '<path d="M12 3v12m-5-5 5 5 5-5M4 16v5h16v-5"/>',
  'chevron-left': '<path d="m14 6-6 6 6 6"/>',
  'chevron-right': '<path d="m10 6 6 6-6 6"/>',
  edit: '<path d="m16 3 5 5-12 12-6 1 1-6ZM13 6l5 5"/>',
  trash: '<path d="M3 6h18M9 6V3h6v3M5 6l1 15h12l1-15M10 10v7M14 10v7"/>',
  x: '<path d="m6 6 12 12M6 18 18 6"/>'
};
function svg(name) { return `<svg viewBox="0 0 24 24" aria-hidden="true">${icons[name] || ''}</svg>`; }
document.querySelectorAll('[data-icon]').forEach(element => { element.innerHTML = svg(element.dataset.icon); });
const state = { students: [], user: null, authorization: '', page: 1, ascending: true, loaded: false, loading: false, editing: null, deleting: null, session: 0 };
const authorizationKey = 'student-tracker.authorization';
function savedAuthorization() {
  try { return sessionStorage.getItem(authorizationKey) || ''; }
  catch { return ''; }
}
function rememberAuthorization(authorization) {
  try {
    if (authorization) sessionStorage.setItem(authorizationKey, authorization);
    else sessionStorage.removeItem(authorizationKey);
  } catch { /* Sign-in still works when browser storage is unavailable. */ }
}
const canEdit = () => state.user?.roles.includes('PROFESSOR');
const canDelete = () => state.user?.roles.includes('DEAN');
const escapeHtml = value => String(value ?? '').replace(/[&<>"']/g, character => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[character]));
function filteredStudents() { return filterStudents(state.students, { search: $('search').value, university: $('university').value, studyLevel: $('study-level').value, ascending: state.ascending }); }
function hasFilters() { return Boolean($('search').value || $('university').value || $('study-level').value); }
function errorIn(id, message = '') { $(id).textContent = message; $(id).hidden = !message; }
function notify(message) {
  clearTimeout(notify.timer); $('toast').textContent = message; $('toast').hidden = false;
  notify.timer = setTimeout(() => { $('toast').hidden = true; }, 4500);
}
async function api(path, { authorization = state.authorization, ...options } = {}) {
  let response;
  try {
    response = await fetch(path, { ...options, credentials: 'omit', cache: 'no-store', signal: AbortSignal.timeout(20000), headers: { 'Authorization': authorization, 'Accept': 'application/json', 'X-Requested-With': 'XMLHttpRequest', ...(options.body ? { 'Content-Type': 'application/json' } : {}) } });
  } catch { throw new Error('Could not reach the server. Check your connection and try again.'); }
  if (!response.ok) {
    const messages = { 401: 'Your sign-in was not accepted. Check your username and password.', 403: 'Your account does not have permission to perform this action.', 404: 'This student no longer exists. Refresh the directory and try again.', 400: 'Check the student details. First name, last name, and a valid email are required.' };
    if (response.status === 401) {
      rememberAuthorization('');
      if (state.user) signOut();
    }
    throw new Error(messages[response.status] || 'The server could not complete your request. Please try again.');
  }
  return response.headers.get('content-type')?.includes('json') ? response.json() : response.text();
}
function updateOptions() {
  for (const [id, field, title] of [['university', 'university', 'All universities'], ['study-level', 'studyLevel', 'All study levels']]) {
    const selected = $(id).value;
    $(id).replaceChildren(new Option(title, ''), ...distinctValues(state.students, field).map(value => new Option(value, value)));
    if ([...$(id).options].some(option => option.value === selected)) $(id).value = selected;
  }
  $('university-options').replaceChildren(...distinctValues(state.students, 'university').map(value => new Option(value, value)));
  $('level-options').replaceChildren(...[...new Set(['Bachelor', 'Master', 'PhD', ...distinctValues(state.students, 'studyLevel')])].map(value => new Option(value, value)));
}
function render() {
  const students = filteredStudents();
  const size = Number($('page-size').value);
  const pages = Math.max(1, Math.ceil(students.length / size));
  state.page = Math.max(1, Math.min(state.page, pages));
  const start = (state.page - 1) * size;
  $('stat-total').textContent = state.loaded ? state.students.length.toLocaleString() : '—';
  $('stat-universities').textContent = state.loaded ? distinctValues(state.students, 'university').length : '—';
  $('stat-levels').textContent = state.loaded ? distinctValues(state.students, 'studyLevel').length : '—';
  $('nav-count').textContent = state.students.length;
  $('result-count').textContent = `${students.length} student${students.length === 1 ? '' : 's'}`;
  $('add-student').disabled = !canEdit() || !state.loaded || state.loading;
  $('export').disabled = !state.loaded || !students.length || state.loading;
  $('reset-filters').hidden = !hasFilters();
  $('empty-clear').hidden = !hasFilters();
  $('student-rows').innerHTML = students.slice(start, start + size).map(student => {
    const name = `${student.firstName} ${student.lastName}`;
    const initial = `${student.firstName?.[0] || ''}${student.lastName?.[0] || ''}`.toLocaleUpperCase();
    const colors = [['#eeeafa', '#9479bf'], ['#eaf3ef', '#7da391'], ['#fbeee5', '#bd987b'], ['#eaf1fc', '#829fc4']][Math.abs(student.id) % 4];
    return `<tr><td><div class="student-cell"><span class="student-avatar" style="--avatar-bg:${colors[0]};--avatar-color:${colors[1]}">${escapeHtml(initial)}</span><div><span class="student-name">${escapeHtml(name)}</span><span class="student-email">${escapeHtml(student.email)}</span></div></div></td><td>${escapeHtml(student.university || 'Not specified')}</td><td>${student.studyLevel ? `<span class="level-badge" data-level="${escapeHtml(student.studyLevel.toLowerCase())}">${escapeHtml(student.studyLevel)}</span>` : 'Not specified'}</td><td class="student-id">ST-${String(student.id).padStart(4, '0')}</td><td><div class="row-actions">${canEdit() ? `<button class="icon-button" data-edit="${student.id}" aria-label="Edit ${escapeHtml(name)}">${svg('edit')}</button>` : ''}${canDelete() ? `<button class="icon-button delete" data-delete="${student.id}" aria-label="Delete ${escapeHtml(name)}">${svg('trash')}</button>` : ''}${!canEdit() && !canDelete() ? '<span class="student-id">View only</span>' : ''}</div></td></tr>`;
  }).join('');
  $('empty-state').hidden = students.length > 0;
  $('empty-title').textContent = state.loading ? 'Loading your community…' : !state.user ? 'Your directory starts here' : !state.loaded ? 'Your directory is unavailable' : hasFilters() ? 'No students match your search' : 'A new community starts here';
  $('empty-description').textContent = state.loading ? 'Fetching the latest student records.' : !state.user ? 'Sign in to view and manage your student community.' : !state.loaded ? 'Try loading the directory again using the button above.' : hasFilters() ? 'Try another name or adjust your filters.' : canEdit() ? 'Add your first student to bring your directory to life.' : 'There are no student records yet. A professor can add the first one.';
  $('page-summary').textContent = students.length ? `Showing ${start + 1}–${Math.min(start + size, students.length)} of ${students.length} students` : 'No students to display';
  $('page-number').textContent = `Page ${state.page} of ${pages}`;
  $('previous').disabled = state.page === 1;
  $('next').disabled = state.page === pages;
  $('sort-indicator').textContent = state.ascending ? '↓' : '↑';
  $('sort-name').closest('th').setAttribute('aria-sort', state.ascending ? 'ascending' : 'descending');
  const levels = new Map();
  state.students.forEach(student => { const level = student.studyLevel || 'Not specified'; levels.set(level, (levels.get(level) || 0) + 1); });
  $('level-breakdown').innerHTML = levels.size ? [...levels].sort((a, b) => b[1] - a[1]).map(([level, count]) => `<div class="level-row"><span>${escapeHtml(level)}</span><div class="level-track"><div class="level-fill" style="width:${count / state.students.length * 100}%"></div></div><span>${Math.round(count / state.students.length * 100)}%</span></div>`).join('') : '<p>Study insights will appear when students are loaded.</p>';
  $('student-rows').closest('section').setAttribute('aria-busy', state.loading ? 'true' : 'false');
}
async function loadStudents() {
  const session = state.session;
  state.loading = true; $('page-error').hidden = true; render();
  try {
    const students = await api('/api/students');
    if (session !== state.session) return;
    state.students = students; state.loaded = true; updateOptions();
  } catch (error) {
    if (session !== state.session) return;
    $('page-error-text').textContent = error.message; $('page-error').hidden = false;
  } finally {
    if (session === state.session) { state.loading = false; render(); }
  }
}
function signOut() {
  rememberAuthorization('');
  state.session++; state.authorization = ''; state.user = null; state.students = []; state.loaded = false; state.loading = false;
  $('account-role').textContent = 'Not signed in'; $('account-avatar').textContent = 'ST'; $('sign-out').hidden = true;
  $('page-error').hidden = true; $('login-form').reset(); errorIn('login-error');
  ['student-dialog', 'delete-dialog'].forEach(id => $(id).close());
  resetFilters(); updateOptions();
  if (!$('login-dialog').open) $('login-dialog').showModal();
}
$('login-dialog').addEventListener('cancel', event => event.preventDefault());
async function signIn(authorization) {
  const user = await api('/api/me', { authorization });
  state.session++; state.user = user; state.authorization = authorization;
  rememberAuthorization(authorization);
  $('account-role').textContent = `${user.username} · ${user.roles.includes('DEAN') ? 'Dean' : user.roles.includes('PROFESSOR') ? 'Professor' : 'Student'}`;
  $('account-avatar').textContent = user.username.slice(0, 2).toUpperCase(); $('sign-out').hidden = false;
  $('login-form').reset(); $('login-dialog').close(); await loadStudents();
}
$('login-form').addEventListener('submit', async event => {
  event.preventDefault(); const form = event.currentTarget; const button = form.querySelector('[type="submit"]');
  button.disabled = true; button.textContent = 'Signing in…'; errorIn('login-error');
  try {
    const fields = new FormData(form);
    const bytes = new TextEncoder().encode(`${String(fields.get('username')).trim()}:${fields.get('password')}`);
    const authorization = `Basic ${btoa(String.fromCharCode(...bytes))}`;
    await signIn(authorization);
  } catch (error) { errorIn('login-error', error.message); }
  finally { button.disabled = false; button.textContent = 'Sign in'; }
});
function resetFilters() { $('search').value = ''; $('university').value = ''; $('study-level').value = ''; state.page = 1; render(); }
['search', 'university', 'study-level', 'page-size'].forEach(id => $(id).addEventListener(id === 'search' ? 'input' : 'change', () => { state.page = 1; render(); }));
$('reset-filters').addEventListener('click', resetFilters); $('empty-clear').addEventListener('click', resetFilters);
$('sort-name').addEventListener('click', () => { state.ascending = !state.ascending; render(); });
$('previous').addEventListener('click', () => { state.page--; render(); }); $('next').addEventListener('click', () => { state.page++; render(); });
$('sign-out').addEventListener('click', signOut); $('retry').addEventListener('click', loadStudents);
function openStudent(student = null) {
  if (!canEdit()) return;
  state.editing = student?.id ?? null; $('student-form').reset(); errorIn('student-error');
  for (const field of ['firstName', 'lastName', 'email', 'university', 'studyLevel']) $('student-form').elements.namedItem(field).value = student?.[field] ?? '';
  $('student-dialog-title').textContent = student ? 'Edit student' : 'Add a student';
  $('save-student').textContent = student ? 'Save changes' : 'Add student';
  $('student-dialog').showModal();
}
$('add-student').addEventListener('click', () => openStudent());
$('student-rows').addEventListener('click', event => {
  const edit = event.target.closest('[data-edit]'); const remove = event.target.closest('[data-delete]');
  if (edit) openStudent(state.students.find(student => student.id === Number(edit.dataset.edit)));
  if (remove && canDelete()) {
    state.deleting = state.students.find(student => student.id === Number(remove.dataset.delete));
    errorIn('delete-error'); $('delete-description').textContent = `${state.deleting.firstName} ${state.deleting.lastName} (${state.deleting.email})`;
    $('delete-dialog').showModal();
  }
});
document.querySelectorAll('[data-close]').forEach(button => button.addEventListener('click', () => $(button.dataset.close).close()));
function lockDialog(id, locked) {
  $(id).querySelectorAll('button, input').forEach(control => { control.disabled = locked; });
  $(id).dataset.busy = String(locked);
}
['student-dialog', 'delete-dialog'].forEach(id => $(id).addEventListener('cancel', event => { if ($(id).dataset.busy === 'true') event.preventDefault(); }));
$('student-form').addEventListener('submit', async event => {
  event.preventDefault();
  const form = event.currentTarget;
  for (const field of ['firstName', 'lastName', 'email']) {
    const input = form.elements.namedItem(field); input.value = input.value.trim();
    if (!input.reportValidity()) return;
  }
  const student = Object.fromEntries([...new FormData(form)].map(([key, value]) => [key, String(value).trim()]));
  const editing = state.editing !== null; if (editing) student.id = state.editing;
  lockDialog('student-dialog', true); errorIn('student-error'); $('save-student').textContent = 'Saving…';
  try {
    await api('/api/students', { method: editing ? 'PUT' : 'POST', body: JSON.stringify(student) });
    $('student-dialog').close(); notify(editing ? 'Student details updated.' : 'Student added to your directory.'); await loadStudents();
  } catch (error) { errorIn('student-error', error.message); }
  finally { lockDialog('student-dialog', false); $('save-student').textContent = editing ? 'Save changes' : 'Add student'; }
});
$('delete-form').addEventListener('submit', async event => {
  event.preventDefault(); lockDialog('delete-dialog', true); errorIn('delete-error');
  const button = event.currentTarget.querySelector('[type="submit"]'); button.textContent = 'Deleting…';
  try {
    await api(`/api/students/${state.deleting.id}`, { method: 'DELETE' });
    $('delete-dialog').close(); notify('Student removed from the directory.'); await loadStudents();
  } catch (error) { errorIn('delete-error', error.message); }
  finally { lockDialog('delete-dialog', false); button.textContent = 'Delete student'; }
});
$('export').addEventListener('click', () => {
  const students = filteredStudents(); const url = URL.createObjectURL(new Blob(['\ufeff', toCsv(students)], { type: 'text/csv;charset=utf-8;' }));
  const link = document.createElement('a'); link.href = url; link.download = `students-${new Date().toISOString().slice(0, 10)}.csv`; link.click();
  setTimeout(() => URL.revokeObjectURL(url), 1000); notify(`Exported ${students.length} student${students.length === 1 ? '' : 's'}.`);
});
async function restoreSignIn() {
  const authorization = savedAuthorization();
  if (authorization) {
    try { await signIn(authorization); return; }
    catch (error) { errorIn('login-error', error.message); }
  }
  $('login-dialog').showModal();
}
render(); restoreSignIn();
