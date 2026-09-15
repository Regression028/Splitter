const jwt = require('jsonwebtoken');
const User = require('../models/User');
const ApiError = require('../utils/ApiError');
const config = require('../config/env');

const protect = async (req, res, next) => {
    let token;

    if (req.headers.authorization && req.headers.authorization.startsWith('Bearer')) {
        token = req.headers.authorization.split(' ')[1];
    }

    if (!token) {
        return next(new ApiError(401, 'Not authorized to access this resource. Token missing.', 'UNAUTHORIZED'));
    }

    try {
        const decoded = jwt.verify(token, config.jwtSecret);
        const user = await User.findById(decoded.id).select('-password');

        if (!user) {
            return next(new ApiError(401, 'User belonging to this token no longer exists.', 'UNAUTHORIZED'));
        }

        req.user = user;
        next();
    } catch (error) {
        return next(new ApiError(401, 'Not authorized to access this resource. Token invalid or expired.', 'UNAUTHORIZED'));
    }
};

module.exports = { protect };
