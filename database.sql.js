/* =========================================================
   NOTEMATE — database.sql.js
   Pure SQL layer: table definitions + all queries
   This file has NO app logic — only database operations.
   ========================================================= */

'use strict';

const NM_SQL = (() => {

  /* ══════════════════════════════════════════════
     SCHEMA — Table Definitions
  ══════════════════════════════════════════════ */
  const SCHEMA = `
    CREATE TABLE IF NOT EXISTS users (
      id          TEXT PRIMARY KEY,
      name        TEXT NOT NULL,
      email       TEXT NOT NULL UNIQUE,
      phone       TEXT,
      course      TEXT,
      password    TEXT,
      created_at  TEXT NOT NULL
    );

    CREATE TABLE IF NOT EXISTS login_activity (
      id          TEXT PRIMARY KEY,
      user_id     TEXT,
      name        TEXT,
      email       TEXT,
      action      TEXT NOT NULL,
      timestamp   TEXT NOT NULL,
      FOREIGN KEY (user_id) REFERENCES users(id)
    );

    CREATE TABLE IF NOT EXISTS bookings (
      id            TEXT PRIMARY KEY,
      user_id       TEXT,
      course        TEXT,
      name          TEXT NOT NULL,
      email         TEXT NOT NULL,
      phone         TEXT,
      college       TEXT,
      subject       TEXT,
      date          TEXT,
      type          TEXT,
      requirements  TEXT,
      plan          TEXT,
      files         TEXT,
      status        TEXT DEFAULT 'pending',
      created_at    TEXT NOT NULL,
      FOREIGN KEY (user_id) REFERENCES users(id)
    );

    CREATE TABLE IF NOT EXISTS contacts (
      id          TEXT PRIMARY KEY,
      user_id     TEXT,
      name        TEXT NOT NULL,
      email       TEXT NOT NULL,
      course      TEXT,
      message     TEXT,
      created_at  TEXT NOT NULL,
      FOREIGN KEY (user_id) REFERENCES users(id)
    );
  `;

  /* ══════════════════════════════════════════════
     QUERIES — All SQL statements
  ══════════════════════════════════════════════ */
  const QUERIES = {

    /* ── Users ── */
    users: {
      insert:      `INSERT INTO users (id, name, email, phone, course, password, created_at)
                    VALUES (?, ?, ?, ?, ?, ?, ?)`,
      selectAll:   `SELECT id, name, email, phone, course, created_at FROM users ORDER BY created_at DESC`,
      selectEmail: `SELECT * FROM users WHERE LOWER(email) = LOWER(?) LIMIT 1`,
      selectPhone: `SELECT * FROM users WHERE phone = ? LIMIT 1`,
      selectId:    `SELECT id, name, email, phone, course, created_at FROM users WHERE id = ? LIMIT 1`,
      delete:      `DELETE FROM users WHERE id = ?`,
    },

    /* ── Login Activity ── */
    loginActivity: {
      insert:      `INSERT INTO login_activity (id, user_id, name, email, action, timestamp)
                    VALUES (?, ?, ?, ?, ?, ?)`,
      selectAll:   `SELECT * FROM login_activity ORDER BY timestamp DESC LIMIT 100`,
      countAll:    `SELECT COUNT(*) as total FROM login_activity`,
      selectUser:  `SELECT * FROM login_activity WHERE user_id = ? ORDER BY timestamp DESC`,
      deleteOld:   `DELETE FROM login_activity WHERE timestamp < ?`,
    },

    /* ── Counts ── */
    counts: {
      users:    `SELECT COUNT(*) as total FROM users`,
      bookings: `SELECT COUNT(*) as total FROM bookings`,
      contacts: `SELECT COUNT(*) as total FROM contacts`,
      activity: `SELECT COUNT(*) as total FROM login_activity`,
    },

    /* ── Bookings ── */
    bookings: {
      insert:      `INSERT INTO bookings
                      (id, user_id, course, name, email, phone, college, subject, date, type, requirements, plan, files, status, created_at)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)`,
      selectAll:   `SELECT * FROM bookings ORDER BY created_at DESC`,
      selectEmail: `SELECT * FROM bookings WHERE LOWER(email) = LOWER(?) ORDER BY created_at DESC`,
      selectUser:  `SELECT * FROM bookings WHERE user_id = ? ORDER BY created_at DESC`,
      updateStatus:`UPDATE bookings SET status = ? WHERE id = ?`,
      delete:      `DELETE FROM bookings WHERE id = ?`,
    },

    /* ── Contacts ── */
    contacts: {
      insert:      `INSERT INTO contacts (id, user_id, name, email, course, message, created_at)
                    VALUES (?, ?, ?, ?, ?, ?, ?)`,
      selectAll:   `SELECT * FROM contacts ORDER BY created_at DESC`,
      selectEmail: `SELECT * FROM contacts WHERE LOWER(email) = LOWER(?) ORDER BY created_at DESC`,
      delete:      `DELETE FROM contacts WHERE id = ?`,
    },
  };

  /* ══════════════════════════════════════════════
     ENGINE — Core SQL execution
  ══════════════════════════════════════════════ */
  let _db = null;
  const STORAGE_KEY = 'nm_sqlite_db';

  function exec(sql, params = []) {
    const result = _db.exec(sql, params);
    if (!result.length) return [];
    const { columns, values } = result[0];
    return values.map(row =>
      Object.fromEntries(columns.map((col, i) => [col, row[i]]))
    );
  }

  function run(sql, params = []) {
    try {
      _db.run(sql, params);
      _persist();
    } catch(e) {
      console.error('DB run error:', e.message, sql);
      throw e;
    }
  }

  function _persist() {
    const data = _db.export();
    localStorage.setItem(STORAGE_KEY, JSON.stringify(Array.from(data)));
  }

  function _migrate() {
    try { _db.run(`ALTER TABLE bookings ADD COLUMN files TEXT`); _persist(); } catch(e) {}
  }

  function _load(SQL) {
    const saved = localStorage.getItem(STORAGE_KEY);
    if (saved) {
      _db = new SQL.Database(new Uint8Array(JSON.parse(saved)));
      _migrate();
    } else {
      _db = new SQL.Database();
      _db.run(SCHEMA);
      _persist();
    }
  }

  /* ══════════════════════════════════════════════
     INIT — Load sql.js WebAssembly + open DB
  ══════════════════════════════════════════════ */
  function init() {
    return new Promise((resolve) => {
      if (typeof initSqlJs !== 'undefined') {
        initSqlJs({ locateFile: file => `https://cdnjs.cloudflare.com/ajax/libs/sql.js/1.10.3/${file}` })
          .then(SQL => { _load(SQL); resolve(); });
        return;
      }
      const script = document.createElement('script');
      script.src = 'https://cdnjs.cloudflare.com/ajax/libs/sql.js/1.10.3/sql-wasm.js';
      script.onload = () => {
        initSqlJs({ locateFile: file => `https://cdnjs.cloudflare.com/ajax/libs/sql.js/1.10.3/${file}` })
          .then(SQL => { _load(SQL); resolve(); });
      };
      document.head.appendChild(script);
    });
  }

  /* ══════════════════════════════════════════════
     PUBLIC API
  ══════════════════════════════════════════════ */
  return { init, exec, run, QUERIES };

})();
