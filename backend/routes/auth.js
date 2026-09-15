const express = require('express');
const router = express.Router();
const jwt = require('jsonwebtoken');
const User = require('../models/User');
const { protect } = require('../middleware/auth');

const mongoose = require('mongoose');

const generateToken = (id) => {
    return jwt.sign({ id }, process.env.JWT_SECRET || 'splitter_super_secret_jwt_key_2026_x89a', {
        expiresIn: '30d'
    });
};

const checkDB = (req, res, next) => {
    if (mongoose.connection.readyState !== 1) {
        return res.status(503).json({
            success: false,
            message: 'MongoDB is not connected! Please start MongoDB or set MONGO_URI in backend/.env'
        });
    }
    next();
};

// @route   POST /api/auth/register
// @desc    Register a new user
router.post('/register', checkDB, async (req, res) => {
    try {
        const { name, email, password } = req.body;

        if (!name || !email || !password) {
            return res.status(400).json({ success: false, message: 'Please provide all fields' });
        }

        const userExists = await User.findOne({ email });
        if (userExists) {
            return res.status(400).json({ success: false, message: 'User already exists with this email' });
        }

        const avatarColors = ['#10B981', '#3B82F6', '#F59E0B', '#8B5CF6', '#EC4899', '#00BCD4'];
        const randomColor = avatarColors[Math.floor(Math.random() * avatarColors.length)];

        const user = await User.create({
            name,
            email,
            password,
            avatarColorHex: randomColor
        });

        res.status(201).json({
            success: true,
            user: {
                id: user._id,
                name: user.name,
                email: user.email,
                avatarColorHex: user.avatarColorHex
            },
            token: generateToken(user._id)
        });
    } catch (error) {
        res.status(500).json({ success: false, message: error.message });
    }
});

// @route   POST /api/auth/login
// @desc    Authenticate user & get token
router.post('/login', checkDB, async (req, res) => {
    try {
        const { email, password } = req.body;

        if (!email || !password) {
            return res.status(400).json({ success: false, message: 'Please provide email and password' });
        }

        const user = await User.findOne({ email });
        if (!user || !(await user.matchPassword(password))) {
            return res.status(401).json({ success: false, message: 'Invalid email or password' });
        }

        res.json({
            success: true,
            user: {
                id: user._id,
                name: user.name,
                email: user.email,
                avatarColorHex: user.avatarColorHex
            },
            token: generateToken(user._id)
        });
    } catch (error) {
        res.status(500).json({ success: false, message: error.message });
    }
});

// @route   GET /api/auth/me
// @desc    Get authenticated user info
router.get('/me', protect, async (req, res) => {
    res.json({
        success: true,
        user: {
            id: req.user._id,
            name: req.user.name,
            email: req.user.email,
            avatarColorHex: req.user.avatarColorHex
        }
    });
});

module.exports = router;
