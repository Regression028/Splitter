# Splitter Node.js + Express + MongoDB Backend API

This is the official REST API server for the **Splitter** Android application, built with **Node.js**, **Express.js**, **MongoDB**, and **JWT Authentication**.

---

## 🚀 How to Run the Backend Server Locally

### 1. Prerequisites
- Node.js installed (`v18` or higher)
- MongoDB installed locally or a free [MongoDB Atlas Cloud URI](https://www.mongodb.com/cloud/atlas)

### 2. Install Dependencies
In your terminal, navigate to the `backend` folder:
```bash
cd backend
npm install
```

### 3. Configure Environment Variables (`.env`)
The `.env` file is already created inside the `backend/` folder:
```env
PORT=5000
MONGO_URI=mongodb://127.0.0.1:27017/splitter_db
JWT_SECRET=splitter_super_secret_jwt_key_2026_x89a
```
*(If using MongoDB Atlas Cloud, replace `MONGO_URI` with your MongoDB Atlas connection string).*

### 4. Start the Server
```bash
npm start
```
Or for development with auto-reload:
```bash
npm run dev
```

The server will start at:
- **Local Web Browser**: `http://localhost:5000`
- **Android Emulator**: `http://10.0.2.2:5000/api/`

---

## 📡 API Reference

### 🔐 Authentication (`/api/auth`)
- `POST /api/auth/register` - Body: `{ "name": "...", "email": "...", "password": "..." }`
- `POST /api/auth/login` - Body: `{ "email": "...", "password": "..." }`
- `GET /api/auth/me` - Header: `Authorization: Bearer <token>`

### 👤 Users (`/api/users`)
- `GET /api/users/search?query=alice` - Search friends by name or email
- `GET /api/users/all` - Get list of users

### 👥 Groups (`/api/groups`)
- `POST /api/groups` - Body: `{ "name": "Goa Trip", "description": "...", "category": "Trip", "memberIds": [...] }`
- `GET /api/groups` - Get all groups for logged-in user

### 💸 Expenses (`/api/expenses`)
- `POST /api/expenses` - Body: `{ "title": "Dinner", "amount": 1500, "paidBy": "...", "groupId": "...", "splits": [...] }`
- `GET /api/expenses` - Get user's expenses

### 🤝 Settlements (`/api/settlements`)
- `POST /api/settlements` - Record a debt payment between users
