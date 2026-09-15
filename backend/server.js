const express = require('express');
const cors = require('cors');
const dotenv = require('dotenv');
const connectDB = require('./config/db');

// Load env vars
dotenv.config();

// Connect to MongoDB
connectDB();

const app = express();

// Body parser & CORS
app.use(express.json());
app.use(cors());

// Mount Routes
app.use('/api/auth', require('./routes/auth'));
app.use('/api/users', require('./routes/users'));
app.use('/api/groups', require('./routes/groups'));
app.use('/api/expenses', require('./routes/expenses'));
app.use('/api/settlements', require('./routes/settlements'));

// Welcome Landing Page
app.get('/', (req, res) => {
    res.send(`
        <!DOCTYPE html>
        <html>
        <head>
            <title>Splitter REST API Server</title>
            <style>
                body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background: #0F172A; color: #F8FAFC; padding: 40px; }
                .container { max-width: 800px; margin: 0 auto; background: #1E293B; padding: 32px; border-radius: 16px; box-shadow: 0 10px 25px rgba(0,0,0,0.5); }
                h1 { color: #10B981; margin-top: 0; }
                .status { display: inline-block; padding: 6px 12px; background: #064E3B; color: #34D399; border-radius: 20px; font-weight: bold; font-size: 14px; }
                ul { line-height: 1.8; }
                code { background: #334155; padding: 3px 8px; border-radius: 4px; color: #38BDF8; }
            </style>
        </head>
        <body>
            <div class="container">
                <h1>Splitter API Server ⚡</h1>
                <p><span class="status">✓ Server Running & Connected to MongoDB</span></p>
                <p>Welcome to the <strong>Splitter</strong> backend REST API. Use this endpoint for Android app integration.</p>
                <h3>Available Endpoints:</h3>
                <ul>
                    <li><code>POST /api/auth/register</code> - Register new user</li>
                    <li><code>POST /api/auth/login</code> - Login & get JWT token</li>
                    <li><code>GET /api/auth/me</code> - Get current user profile</li>
                    <li><code>GET /api/users/search?query=...</code> - Find friends by email or name</li>
                    <li><code>POST /api/groups</code> & <code>GET /api/groups</code> - Manage groups</li>
                    <li><code>POST /api/expenses</code> & <code>GET /api/expenses</code> - Manage expenses & splits</li>
                    <li><code>POST /api/settlements</code> & <code>GET /api/settlements</code> - Record debt settlements</li>
                </ul>
            </div>
        </body>
        </html>
    `);
});

const PORT = process.env.PORT || 8080;

app.listen(PORT, '0.0.0.0', () => {
    console.log(`=================================================`);
    console.log(`🚀 Splitter API Server running on port ${PORT}`);
    console.log(`🌐 Local URL: http://localhost:${PORT}`);
    console.log(`📲 Android Emulator Base URL: http://10.0.2.2:${PORT}/api/`);
    console.log(`=================================================`);
});
