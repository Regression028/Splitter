const express = require('express');
const cors = require('cors');
const helmet = require('helmet');
const morgan = require('morgan');
const config = require('./config/env');
const errorHandler = require('./middleware/error');
const { apiLimiter } = require('./middleware/rateLimiter');
const ApiResponse = require('./utils/ApiResponse');

const app = express();

// Security Middlewares
app.use(helmet());
app.use(cors({ origin: config.clientOrigin }));

// Logging
if (config.nodeEnv !== 'test') {
    app.use(morgan('dev'));
}

// Body Parser
app.use(express.json({ limit: '10mb' }));

// Apply Rate Limiter
app.use('/api', apiLimiter);

// Mount API Routes
const authRoutes = require('./routes/authRoutes');
const groupRoutes = require('./routes/groupRoutes');
const expenseRoutes = require('./routes/expenseRoutes');

app.use('/api/v1/auth', authRoutes);
app.use('/api/auth', authRoutes); // Backwards compatibility alias

app.use('/api/v1/groups', groupRoutes);
app.use('/api/groups', groupRoutes); // Backwards compatibility alias

app.use('/api/v1/groups/:groupId/expenses', expenseRoutes);
app.use('/api/v1/expenses', expenseRoutes);
app.use('/api/expenses', expenseRoutes); // Backwards compatibility alias

// Health Check & Welcome Endpoint
app.get('/health', (req, res) => {
    return ApiResponse.success(res, {
        status: 'UP',
        timestamp: new Date().toISOString(),
        environment: config.nodeEnv
    });
});

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
                <p><span class="status">✓ Phase 2 Architecture Ready</span></p>
                <p>Production REST API backend for the <strong>Splitter</strong> Android Application.</p>
                <h3>System Status:</h3>
                <ul>
                    <li><code>GET /health</code> - Returns service health & status</li>
                    <li><code>PORT</code> - Running on port ${config.port}</li>
                </ul>
            </div>
        </body>
        </html>
    `);
});

// 404 Handler
app.use((req, res, next) => {
    return ApiResponse.error(res, 404, `Route ${req.originalUrl} not found`, 'NOT_FOUND');
});

// Centralized Error Handler Middleware
app.use(errorHandler);

module.exports = app;
