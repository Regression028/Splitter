const app = require('./app');
const config = require('./config/env');
const connectDB = require('./config/db');

// Handle Uncaught Exceptions
process.on('uncaughtException', (err) => {
    console.error(`[Uncaught Exception]: ${err.message}`);
    process.exit(1);
});

// Connect to MongoDB
connectDB();

const PORT = config.port;

const server = app.listen(PORT, '0.0.0.0', () => {
    console.log(`=================================================`);
    console.log(`🚀 Splitter Production API Server Running`);
    console.log(`📡 Environment: ${config.nodeEnv}`);
    console.log(`🌐 Local URL: http://localhost:${PORT}`);
    console.log(`📲 Android Emulator URL: http://10.0.2.2:${PORT}/api/`);
    console.log(`=================================================`);
});

// Handle Unhandled Promise Rejections
process.on('unhandledRejection', (err) => {
    console.error(`[Unhandled Rejection]: ${err.message}`);
    server.close(() => process.exit(1));
});
