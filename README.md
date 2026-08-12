# NoteMate

Collaborative real-time document editor. Multiple users edit simultaneously, changes sync instantly (<100ms), auto-save every 30 seconds, version history, export to PDF/Markdown/Text.

---

## Quick Start

```bash
# Clone
git clone https://github.com/luckeykumar/notemate.git && cd notemate

# Setup environment
cp .env.example .env
# Add: MONGODB_URI, JWT_SECRET, SENDGRID_API_KEY

# Install & run
npm install

# Terminal 1: Backend
cd server && npm run dev

# Terminal 2: Frontend  
cd client && npm run dev

# Open: http://localhost:3000
```

---

## Architecture

```
User A ←→ (WebSocket) ←→ Express Server ←→ MongoDB
User B ←→ (Socket.io) ←→ Real-time Sync  ← Version Storage
                       ↓
                    Auto-Save (30s)
                    Conflict Resolution
                    Audit Trail
```

---

## Core Features

| Feature | How It Works |
|---------|-------------|
| **Real-Time Sync** | Socket.io WebSocket, <100ms latency, cursor tracking |
| **Auto-Save** | Every 30 seconds, version snapshots stored |
| **Version History** | Browse past versions, restore any point in time |
| **Markdown Editor** | Monaco Editor with live preview |
| **Sharing** | Invite collaborators with view/edit permissions |
| **Export** | PDF, Markdown, or plain text download |
| **Organize** | Tags, folders, full-text search |
| **Audit Trail** | See who changed what when |

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Frontend | React 18, Redux, Monaco Editor, Tailwind |
| Backend | Express.js, Node.js |
| Real-time | Socket.io (WebSocket) |
| Database | MongoDB + Mongoose |
| Auth | JWT + Bcrypt |
| Export | html2pdf, markdown-it |
| Hosting | Docker, Vercel, Heroku |

---

## File Structure

```
notemate/
├── client/                    # React frontend
│   ├── src/components/        # Editor, NoteList, Sharing, VersionHistory
│   ├── src/services/          # API, WebSocket, Export
│   └── src/store/             # Redux state
│
├── server/                    # Express backend
│   ├── src/routes/            # /api/notes, /auth, /share
│   ├── src/socket/            # WebSocket handlers
│   ├── src/models/            # MongoDB schemas
│   └── src/services/          # Versioning, Auth, Export
│
└── docs/                      # API, Architecture, Contributing
```

---

## Setup

### Prerequisites
- Node.js 16+
- MongoDB (local or Atlas)

### Environment Variables

**server/.env**
```
MONGODB_URI=mongodb://localhost:27017/notemate
JWT_SECRET=your_secret_key_min_32_chars
PORT=5000
FRONTEND_URL=http://localhost:3000
```

**client/.env**
```
VITE_API_URL=http://localhost:5000
VITE_SOCKET_URL=http://localhost:5000
```

### Run

```bash
# Start MongoDB
mongod

# Terminal 1: Backend
cd server && npm install && npm run dev

# Terminal 2: Frontend
cd client && npm install && npm run dev
```

Open http://localhost:3000

---

## API Endpoints

```
# Auth
POST   /api/auth/register
POST   /api/auth/login
POST   /api/auth/logout

# Notes (CRUD)
GET    /api/notes
POST   /api/notes
GET    /api/notes/:id
PUT    /api/notes/:id
DELETE /api/notes/:id

# Sharing
POST   /api/notes/:id/share
DELETE /api/notes/:id/share/:userId
GET    /api/notes/:id/collaborators

# Versions
GET    /api/notes/:id/versions
POST   /api/notes/:id/versions/:vNum/restore

# Search & Export
GET    /api/search?q=query
POST   /api/notes/:id/export
```

---

## WebSocket Events

```
# Editor
cursor_move          { line, column, username, color }
content_change       { change, position }
save_note            { noteId, content }
version_saved        { versionNum, savedAt }

# Collaboration  
user_joined          { userId, username }
user_left            { userId }
permission_changed   { userId, newPermission }
```

---

## Usage

### Create & Edit
1. Click "+ New Note"
2. Type markdown
3. Live preview appears
4. Auto-saves every 30s

### Collaborate
1. Click "Share"
2. Enter email + choose permission (view/edit)
3. They join via email link
4. See each other's cursors + changes live

### Organize
- **Tags:** #work, #important → filter by tag
- **Folders:** Drag notes into folders
- **Search:** Type to find by title/content

### Export
1. Click "Download"
2. Choose: PDF | Markdown | Text
3. File downloads

---

## Deployment

### Docker
```bash
docker-compose up -d
# Frontend: http://localhost:3000
# Backend: http://localhost:5000
```

### Vercel (Frontend)
```bash
cd client && vercel deploy
```

### Heroku (Backend)
```bash
cd server
heroku create your-app
git push heroku main
```

---

## Roadmap

| Version | Features |
|---------|----------|
| **1.0** | ✅ Real-time editing, version history, sharing, export |
| **1.5** | Comments, @mentions, offline mode |
| **2.0** | Mobile app, AI suggestions, integrations |

---

## Support

- **Issues:** [GitHub Issues](https://github.com/luckeykumar/notemate/issues)
- **Email:** support@notemate.dev

---

## License

MIT License — see [LICENSE](./LICENSE)

---

<div align="center">

**Built for teams that write together** ⭐

Made with ❤️ by [Luckey](https://github.com/luckeykumar)

</div>
