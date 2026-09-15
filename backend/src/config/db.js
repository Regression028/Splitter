const mongoose = require('mongoose');
const config = require('./env');

const connectDB = async () => {
    try {
        const conn = await mongoose.connect(config.mongoUri, {
            serverSelectionTimeoutMS: 5000
        });
        console.log(`[MongoDB Connected]: ${conn.connection.host}`);
    } catch (error) {
        console.error(`[MongoDB Connection Error]: ${error.message}`);
        if (config.nodeEnv === 'production') {
            process.exit(1);
        } else {
            console.log(`⚠️ Running in fallback mode. Please verify your MONGO_URI in backend/.env`);
        }
    }
};

module.exports = connectDB;
