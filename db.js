/* =========================================================
   NOTEMATE — db.js
   App database layer — calls NM_SQL (database.sql.js)
   NO raw SQL here. All queries live in database.sql.js
   ========================================================= */

'use strict';

const NM_DB = (() => {

  function generateId() {
    return Date.now().toString(36) + Math.random().toString(36).slice(2, 7);
  }

  function timestamp() {
    return new Date().toISOString();
  }

  /* ── USERS ── */
  const Users = {
    getAll() {
      return NM_SQL.exec(NM_SQL.QUERIES.users.selectAll);
    },

    findByEmail(email) {
      return NM_SQL.exec(NM_SQL.QUERIES.users.selectEmail, [email])[0] || null;
    },

    create({ name, email, phone = '', course = '', password = '' }) {
      if (this.findByEmail(email)) return { error: 'Email already registered.' };
      if (phone && this.findByPhone(phone)) return { error: 'Phone number already registered.' };
      const user = { id: generateId(), name, email, phone, course, password, created_at: timestamp() };
      NM_SQL.run(NM_SQL.QUERIES.users.insert,
        [user.id, user.name, user.email, user.phone, user.course, user.password, user.created_at]
      );
      return { user };
    },

    findByPhone(phone) {
      return NM_SQL.exec(NM_SQL.QUERIES.users.selectPhone, [phone])[0] || null;
    },

    verify(email, password) {
      const user = this.findByEmail(email);
      if (!user) return { error: 'No account found with this email.' };
      if (user.password && user.password !== password) return { error: 'Incorrect password.' };
      return { user };
    },

    verifyByPhone(phone, password) {
      const user = this.findByPhone(phone);
      if (!user) return { error: 'No account found with this phone number.' };
      if (user.password && user.password !== password) return { error: 'Incorrect password.' };
      return { user };
    },
  };

  /* ── LOGIN ACTIVITY ── */
  const LoginActivity = {
    getAll() {
      return NM_SQL.exec(NM_SQL.QUERIES.loginActivity.selectAll);
    },

    log({ userId, name, email, action }) {
      NM_SQL.run(NM_SQL.QUERIES.loginActivity.insert,
        [generateId(), userId, name, email, action, timestamp()]
      );
    },

    getForUser(userId) {
      return NM_SQL.exec(NM_SQL.QUERIES.loginActivity.selectUser, [userId]);
    },
  };

  /* ── BOOKINGS ── */
  const Bookings = {
    getAll() {
      return NM_SQL.exec(NM_SQL.QUERIES.bookings.selectAll);
    },

    create(data) {
      const id = generateId();
      const now = timestamp();
      NM_SQL.run(NM_SQL.QUERIES.bookings.insert, [
        id, data.userId || null, data.course, data.name, data.email,
        data.phone, data.college, data.subject, data.date, data.type,
        data.requirements, data.plan, data.files || null, 'pending', now
      ]);
      return { id, ...data, status: 'pending', created_at: now };
    },

    getForUser(email) {
      return NM_SQL.exec(NM_SQL.QUERIES.bookings.selectEmail, [email]);
    },

    updateStatus(id, status) {
      NM_SQL.run(NM_SQL.QUERIES.bookings.updateStatus, [status, id]);
    },
  };

  /* ── CONTACTS ── */
  const Contacts = {
    getAll() {
      return NM_SQL.exec(NM_SQL.QUERIES.contacts.selectAll);
    },

    create(data) {
      const id = generateId();
      const now = timestamp();
      NM_SQL.run(NM_SQL.QUERIES.contacts.insert,
        [id, data.userId || null, data.name, data.email, data.course, data.message, now]
      );
      return { id, ...data, created_at: now };
    },
  };

  /* ── INIT ── */
  function init() {
    return NM_SQL.init();
  }

  function counts() {
    return {
      users:    NM_SQL.exec(NM_SQL.QUERIES.counts.users)[0]?.total    ?? 0,
      bookings: NM_SQL.exec(NM_SQL.QUERIES.counts.bookings)[0]?.total ?? 0,
      contacts: NM_SQL.exec(NM_SQL.QUERIES.contacts)[0]?.total ?? 0,
      activity: NM_SQL.exec(NM_SQL.QUERIES.counts.activity)[0]?.total ?? 0,
    };
  }

  return { init, counts, Users, LoginActivity, Bookings, Contacts };

})();
