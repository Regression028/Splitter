const User = require('../models/User');
const ApiError = require('../utils/ApiError');
const ApiResponse = require('../utils/ApiResponse');

// @desc    Register a new user
// @route   POST /api/v1/auth/register
// @access  Public
const register = async (req, res, next) => {
    try {
        const { name, email, password } = req.body;

        const existingUser = await User.findOne({ email });
        if (existingUser) {
            return next(new ApiError(400, 'User already exists with this email address', 'DUPLICATE_EMAIL'));
        }

        const avatarColors = ['#10B981', '#3B82F6', '#F59E0B', '#8B5CF6', '#EC4899', '#00BCD4'];
        const randomColor = avatarColors[Math.floor(Math.random() * avatarColors.length)];

        const user = await User.create({
            name,
            email,
            password,
            avatarColorHex: randomColor
        });

        const token = user.generateJwtToken();

        return ApiResponse.success(
            res,
            {
                user: {
                    id: user._id,
                    name: user.name,
                    email: user.email,
                    avatarColorHex: user.avatarColorHex,
                    defaultCurrency: user.defaultCurrency
                },
                token
            },
            201
        );
    } catch (error) {
        next(error);
    }
};

// @desc    Login user
// @route   POST /api/v1/auth/login
// @access  Public
const login = async (req, res, next) => {
    try {
        const { email, password } = req.body;

        const user = await User.findOne({ email }).select('+password');
        if (!user) {
            return next(new ApiError(401, 'Invalid email address or password', 'INVALID_CREDENTIALS'));
        }

        const isMatch = await user.comparePassword(password);
        if (!isMatch) {
            return next(new ApiError(401, 'Invalid email address or password', 'INVALID_CREDENTIALS'));
        }

        const token = user.generateJwtToken();

        return ApiResponse.success(res, {
            user: {
                id: user._id,
                name: user.name,
                email: user.email,
                avatarColorHex: user.avatarColorHex,
                defaultCurrency: user.defaultCurrency
            },
            token
        });
    } catch (error) {
        next(error);
    }
};

// @desc    Get current logged in user profile
// @route   GET /api/v1/auth/me
// @access  Private
const getMe = async (req, res, next) => {
    try {
        return ApiResponse.success(res, {
            user: {
                id: req.user._id,
                name: req.user.name,
                email: req.user.email,
                avatarColorHex: req.user.avatarColorHex,
                defaultCurrency: req.user.defaultCurrency
            }
        });
    } catch (error) {
        next(error);
    }
};

// @desc    Change password
// @route   PUT /api/v1/auth/password
// @access  Private
const changePassword = async (req, res, next) => {
    try {
        const { currentPassword, newPassword } = req.body;

        const user = await User.findById(req.user._id).select('+password');
        if (!user) {
            return next(new ApiError(404, 'User not found', 'USER_NOT_FOUND'));
        }

        const isMatch = await user.comparePassword(currentPassword);
        if (!isMatch) {
            return next(new ApiError(401, 'Current password is incorrect', 'INVALID_PASSWORD'));
        }

        user.password = newPassword;
        await user.save();

        return ApiResponse.success(res, { message: 'Password updated successfully' });
    } catch (error) {
        next(error);
    }
};

module.exports = {
    register,
    login,
    getMe,
    changePassword
};
