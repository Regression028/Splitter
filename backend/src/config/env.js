const dotenv = require('dotenv');
const path = require('path');

dotenv.config({ path: path.join(__dirname, '../../.env') });

const config = {
    port: process.env.PORT || 8080,
    nodeEnv: process.env.NODE_ENV || 'development',
    mongoUri: process.env.MONGO_URI || 'mongodb://127.0.0.1:27017/splitter_db',
    jwtSecret: process.env.JWT_SECRET || 'splitter_super_secret_jwt_key_2026_production_grade_token',
    jwtExpire: process.env.JWT_EXPIRE || '30d',
    clientOrigin: process.env.CLIENT_ORIGIN || '*'
};

module.exports = config;
