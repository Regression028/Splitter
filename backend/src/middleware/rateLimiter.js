const rateLimit = require('express-rate-limit');
const ApiResponse = require('../utils/ApiResponse');

// Global API Rate Limiter
const apiLimiter = rateLimit({
    windowMs: 15 * 60 * 1000, // 15 minutes
    max: 200, // Limit each IP to 200 requests per 15 mins
    standardHeaders: true,
    legacyHeaders: false,
    handler: (req, res) => {
        return ApiResponse.error(res, 429, 'Too many requests from this IP, please try again later.', 'RATE_LIMIT_EXCEEDED');
    }
});

// Stricter Limiter for Auth Routes
const authLimiter = rateLimit({
    windowMs: 15 * 60 * 1000, // 15 minutes
    max: 20, // Limit each IP to 20 auth requests per 15 mins
    standardHeaders: true,
    legacyHeaders: false,
    handler: (req, res) => {
        return ApiResponse.error(res, 429, 'Too many authentication attempts, please try again after 15 minutes.', 'AUTH_RATE_LIMIT');
    }
});

module.exports = {
    apiLimiter,
    authLimiter
};
