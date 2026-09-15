const ApiError = require('../utils/ApiError');
const ApiResponse = require('../utils/ApiResponse');
const config = require('../config/env');

const errorHandler = (err, req, res, next) => {
    let statusCode = err.statusCode || 500;
    let message = err.message || 'Internal Server Error';
    let code = err.code || 'INTERNAL_ERROR';

    // Mongoose Duplicate Key Error (E11000)
    if (err.code === 11000) {
        statusCode = 400;
        const field = Object.keys(err.keyValue || {})[0] || 'field';
        message = `An account with this ${field} already exists.`;
        code = 'DUPLICATE_RESOURCE';
    }

    // Mongoose ValidationError
    if (err.name === 'ValidationError') {
        statusCode = 400;
        message = Object.values(err.errors).map(val => val.message).join(', ');
        code = 'VALIDATION_ERROR';
    }

    // Mongoose CastError (Invalid ObjectId)
    if (err.name === 'CastError') {
        statusCode = 400;
        message = `Invalid resource ID format: ${err.value}`;
        code = 'INVALID_ID';
    }

    // JsonWebTokenError / TokenExpiredError
    if (err.name === 'JsonWebTokenError' || err.name === 'TokenExpiredError') {
        statusCode = 401;
        message = 'Invalid or expired token. Please log in again.';
        code = 'UNAUTHORIZED';
    }

    if (config.nodeEnv === 'development') {
        console.error(`[API Error] ${req.method} ${req.originalUrl} - ${statusCode} - ${message}`);
        if (err.stack && statusCode === 500) {
            console.error(err.stack);
        }
    }

    return ApiResponse.error(res, statusCode, message, code);
};

module.exports = errorHandler;
