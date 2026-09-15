const mongoose = require('mongoose');

const connectDB = async () => {
    try {
        const mongoURI = process.env.MONGO_URI || 'mongodb://127.0.0.1:27017/splitter_db';
        const conn = await mongoose.connect(mongoURI, {
            serverSelectionTimeoutMS: 5000 // Timeout after 5s if MongoDB is not running
        });
        console.log(`[MongoDB Connected]: ${conn.connection.host}`);
    } catch (error) {
        console.error(`[MongoDB Connection Warning]: ${error.message}`);
        console.log(`⚠️  Local MongoDB is not running. Please start MongoDB or provide a MongoDB Atlas Cloud URI in backend/.env`);
    }
};

module.exports = connectDB;
