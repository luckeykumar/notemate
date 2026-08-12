/* =========================================================
   NOTEMATE — admin.js
   Admin panel logic: auth, tabs, data rendering, export
   ========================================================= */

'use strict';

const ADMIN_CREDS = { username: 'admin', password: 'notemate@2025' };
const ADMIN_SESSION_KEY = 'nm_admin_session';
let isAdminDbReady = false;

/* ══════════════════════════════════════════════
   INIT
══════════════════════════════════════════════ */
NM_DB.init().then(() => {
  isAdminDbReady = true;
  if (localStorage.getItem(ADMIN_SESSION_KEY) === 'true') {
    showPanel();
  }
}).catch((err) => {
  console.error('Admin DB init failed:', err);
});

/* ══════════════════════════════════════════════
   AUTH
══════════════════════════════════════════════ */
function adminLogin(e) {
  e.preventDefault();
  const username = document.getElementById('al-username').value.trim();
  const password = document.getElementById('al-password').value;
  const error    = document.getElementById('al-error');

  if (username === ADMIN_CREDS.username && password === ADMIN_CREDS.password) {
    localStorage.setItem(ADMIN_SESSION_KEY, 'true');
    error.classList.add('hidden');
    showPanel();
  } else {
    error.classList.remove('hidden');
  }
}

function adminLogout() {
  localStorage.removeItem(ADMIN_SESSION_KEY);
  document.getElementById('admin-panel').classList.add('hidden');
  document.getElementById('admin-login-screen').classList.remove('hidden');
}

function showPanel() {
  document.getElementById('admin-login-screen').classList.add('hidden');
  document.getElementById('admin-panel').classList.remove('hidden');
  if (!isAdminDbReady) {
    console.warn('Admin DB not ready yet, retrying panel load');
    NM_DB.init().then(() => {
      isAdminDbReady = true;
      loadDashboard();
      loadUsers();
      loadBookings();
      loadContacts();
      loadActivity();
    }).catch(err => {
      console.error('Admin DB retry failed:', err);
      alert('Unable to load admin data. Please refresh the page.');
    });
    return;
  }
  loadDashboard();
  loadUsers();
  loadBookings();
  loadContacts();
  loadActivity();
}

function toggleAdminPassword() {
  const input = document.getElementById('al-password');
  const icon  = document.getElementById('al-eye-icon');
  const show  = input.type === 'password';
  input.type  = show ? 'text' : 'password';
  icon.innerHTML = show
    ? `<path d="M17.94 17.94A10.07 10.07 0 0112 20c-7 0-11-8-11-8a18.45 18.45 0 015.06-5.94" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
       <path d="M9.9 4.24A9.12 9.12 0 0112 4c7 0 11 8 11 8a18.5 18.5 0 01-2.16 3.19" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
       <line x1="1" y1="1" x2="23" y2="23" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>`
    : `<path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" stroke="currentColor" stroke-width="2"/>
       <circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="2"/>`;
}

/* ══════════════════════════════════════════════
   SIDEBAR
══════════════════════════════════════════════ */
function toggleSidebar() {
  document.getElementById('sidebar').classList.toggle('open');
}

/* ══════════════════════════════════════════════
   TABS
══════════════════════════════════════════════ */
const TAB_TITLES = {
  dashboard: 'Dashboard',
  users: 'Users',
  bookings: 'Bookings',
  contacts: 'Contacts',
  activity: 'Login Activity',
};

function showTab(name, btn) {
  document.querySelectorAll('.tab').forEach(t => t.classList.add('hidden'));
  document.querySelectorAll('.nav-item').forEach(b => b.classList.remove('active'));

  document.getElementById(`tab-${name}`).classList.remove('hidden');
  btn.classList.add('active');
  document.getElementById('page-title').textContent = TAB_TITLES[name];

  const loaders = { users: loadUsers, bookings: loadBookings, contacts: loadContacts, activity: loadActivity };
  if (loaders[name]) loaders[name]();

  // Close sidebar on mobile
  document.getElementById('sidebar').classList.remove('open');
}

/* ══════════════════════════════════════════════
   LOAD DASHBOARD
══════════════════════════════════════════════ */
function loadDashboard() {
  const c       = NM_DB.counts();
  const bookings = NM_DB.Bookings.getAll();

  document.getElementById('stat-users').textContent    = c.users;
  document.getElementById('stat-bookings').textContent = c.bookings;
  document.getElementById('stat-contacts').textContent = c.contacts;
  document.getElementById('stat-activity').textContent = c.activity;

  const tbody = document.getElementById('recent-bookings-body');
  const recent = bookings.slice(0, 5);

  tbody.innerHTML = recent.length
    ? recent.map(b => `
        <tr>
          <td>${b.name}</td>
          <td>${b.course || '—'}</td>
          <td>${b.type || '—'}</td>
          <td>${formatDate(b.date)}</td>
          <td><span class="badge badge-${b.status}">${b.status}</span></td>
        </tr>`).join('')
    : `<tr class="empty-row"><td colspan="5">No bookings yet.</td></tr>`;
}

/* ══════════════════════════════════════════════
   LOAD USERS
══════════════════════════════════════════════ */
function loadUsers() {
  const users = NM_DB.Users.getAll();
  const tbody = document.getElementById('users-body');

  tbody.innerHTML = users.length
    ? users.map((u, i) => `
        <tr>
          <td>${i + 1}</td>
          <td>${u.name}</td>
          <td>${u.email}</td>
          <td>${u.phone || '—'}</td>
          <td>${u.course || '—'}</td>
          <td>${formatDate(u.created_at)}</td>
        </tr>`).join('')
    : `<tr class="empty-row"><td colspan="6">No users registered yet.</td></tr>`;
}

/* ══════════════════════════════════════════════
   LOAD BOOKINGS
══════════════════════════════════════════════ */
function loadBookings() {
  const bookings = NM_DB.Bookings.getAll();
  const tbody = document.getElementById('bookings-body');

  tbody.innerHTML = bookings.length
    ? bookings.map((b, i) => {
        let filesHtml = '—';
        if (b.files) {
          try {
            const parsed = JSON.parse(b.files);
            filesHtml = parsed.map(f =>
              `<a href="${f.data}" download="${f.name}" style="display:inline-flex;align-items:center;gap:4px;color:#fff;font-size:0.8rem;text-decoration:underline;margin-right:6px;">
                <svg width="12" height="12" viewBox="0 0 24 24" fill="none"><path d="M21 15v4a2 2 0 01-2 2H5a2 2 0 01-2-2v-4" stroke="currentColor" stroke-width="2" stroke-linecap="round"/><polyline points="7,10 12,15 17,10" stroke="currentColor" stroke-width="2" stroke-linecap="round"/><line x1="12" y1="15" x2="12" y2="3" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>
                ${f.name}
              </a>`
            ).join('');
          } catch(e) { filesHtml = '—'; }
        }
        const isDone = b.status === 'done';
        const phoneHtml = b.phone
          ? `<a href="tel:${b.phone}" style="color:#4fc;text-decoration:none;font-weight:600;">${b.phone}</a>`
          : '—';
        return `
          <tr>
            <td>${i + 1}</td>
            <td>${b.name}</td>
            <td>${phoneHtml}</td>
            <td>${b.course || '—'}</td>
            <td>${b.subject || '—'}</td>
            <td>${b.type || '—'}</td>
            <td>${b.plan || '—'}</td>
            <td>${formatDate(b.date)}</td>
            <td>${filesHtml}</td>
            <td>
              <button class="status-toggle ${isDone ? 'status-done' : 'status-pending'}" onclick="updateBookingStatus('${b.id}', '${isDone ? 'pending' : 'done'}')">
                ${isDone ? '✓ Done' : '⏳ Pending'}
              </button>
            </td>
          </tr>`;
      }).join('')
    : `<tr class="empty-row"><td colspan="10">No bookings yet.</td></tr>`;
}

/* ══════════════════════════════════════════════
   UPDATE BOOKING STATUS
══════════════════════════════════════════════ */
function updateBookingStatus(id, newStatus) {
  if (!id || !newStatus) {
    alert('Unable to update booking status. Missing booking identifier.');
    return;
  }

  try {
    NM_DB.Bookings.updateStatus(id, newStatus);
    loadBookings();
  } catch (err) {
    console.error('Booking status update failed:', err);
    alert('Failed to update booking status. Please refresh the page and try again.');
  }
}

/* ══════════════════════════════════════════════
   LOAD CONTACTS
══════════════════════════════════════════════ */
function loadContacts() {
  const contacts = NM_DB.Contacts.getAll();
  const tbody = document.getElementById('contacts-body');

  tbody.innerHTML = contacts.length
    ? contacts.map((c, i) => `
        <tr>
          <td>${i + 1}</td>
          <td>${c.name}</td>
          <td>${c.email}</td>
          <td>${c.course || '—'}</td>
          <td title="${c.message || ''}">${truncate(c.message, 50)}</td>
          <td>${formatDate(c.created_at)}</td>
        </tr>`).join('')
    : `<tr class="empty-row"><td colspan="6">No messages yet.</td></tr>`;
}

/* ══════════════════════════════════════════════
   LOAD ACTIVITY
══════════════════════════════════════════════ */
function loadActivity() {
  const logs = NM_DB.LoginActivity.getAll();
  const tbody = document.getElementById('activity-body');

  tbody.innerHTML = logs.length
    ? logs.map((l, i) => `
        <tr>
          <td>${i + 1}</td>
          <td>${l.name || '—'}</td>
          <td>${l.email || '—'}</td>
          <td><span class="badge badge-${l.action}">${l.action}</span></td>
          <td>${formatDateTime(l.timestamp)}</td>
        </tr>`).join('')
    : `<tr class="empty-row"><td colspan="5">No activity yet.</td></tr>`;
}

/* ══════════════════════════════════════════════
   SEARCH
══════════════════════════════════════════════ */
function searchTable(tbodyId, query) {
  const q = query.toLowerCase();
  document.querySelectorAll(`#${tbodyId} tr`).forEach(row => {
    row.style.display = row.textContent.toLowerCase().includes(q) ? '' : 'none';
  });
}

/* ══════════════════════════════════════════════
   EXPORT CSV
══════════════════════════════════════════════ */
function exportCSV(type) {
  if (!isAdminDbReady) {
    alert('Please wait until the database has finished loading.');
    return;
  }

  const map = {
    users:    { data: NM_DB.Users.getAll(),         cols: ['name','email','phone','course','created_at'],                          headers: ['Name','Email','Phone','Course','Registered'] },
    bookings: { data: NM_DB.Bookings.getAll(),      cols: ['name','email','phone','course','subject','type','plan','date','status','created_at'], headers: ['Name','Email','Phone','Course','Subject','Type','Plan','Date','Status','Created'] },
    contacts: { data: NM_DB.Contacts.getAll(),      cols: ['name','email','course','message','created_at'],                        headers: ['Name','Email','Course','Message','Received'] },
    activity: { data: NM_DB.LoginActivity.getAll(), cols: ['name','email','action','timestamp'],                                   headers: ['Name','Email','Action','Timestamp'] },
  };

  const config = map[type];
  if (!config) {
    alert('Export type not recognized.');
    return;
  }

  const { data, cols, headers } = config;
  const rows = (data || []).map(row =>
    cols.map(c => {
      const val = (row[c] == null ? '' : String(row[c]))
        .replace(/"/g, '""')
        .replace(/\r?\n/g, ' ')
        .trim();
      return `"${val}"`;
    }).join(',')
  );

  const csv = '\uFEFF' + [headers.join(','), ...rows].join('\r\n');

  // Primary method using Blob (works in most modern browsers)
  try {
    const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `notemate_${type}_${Date.now()}.csv`;
    a.style.display = 'none';
    document.body.appendChild(a);
    a.click();
    setTimeout(() => {
      document.body.removeChild(a);
      URL.revokeObjectURL(url);
      console.log('CSV exported successfully via Blob');
    }, 300);
  } catch(err) {
    console.error('Blob export failed:', err);
    // Fallback for file:// protocol or restricted environments
    try {
      const a = document.createElement('a');
      a.href = 'data:text/csv;charset=utf-8,' + encodeURIComponent(csv);
      a.download = `notemate_${type}_${Date.now()}.csv`;
      a.style.display = 'none';
      document.body.appendChild(a);
      a.click();
      setTimeout(() => {
        document.body.removeChild(a);
        console.log('CSV exported successfully via data URI');
      }, 300);
    } catch(err2) {
      console.error('Data URI export also failed:', err2);
      alert('Export failed. Please try opening the page via a local server (not file://).\n\nError: ' + err2.message);
    }
  }
}

/* ══════════════════════════════════════════════
   HELPERS
══════════════════════════════════════════════ */
function formatDate(str) {
  if (!str) return '—';
  return new Date(str).toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' });
}

function formatDateTime(str) {
  if (!str) return '—';
  return new Date(str).toLocaleString('en-IN', { day: '2-digit', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit' });
}

function truncate(str, len) {
  if (!str) return '—';
  return str.length > len ? str.slice(0, len) + '…' : str;
}
