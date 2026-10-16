const mongoose = require('mongoose');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const config = require('../src/config/env');

const UserSchema = new mongoose.Schema(
    {
        name: {
            type: String,
            required: [true, 'Please provide a name'],
            trim: true
        },
        email: {
            type: String,
            required: [true, 'Please provide an email address'],
            unique: true,
            lowercase: true,
            trim: true
        },
        password: {
            type: String,
            required: [true, 'Please provide a password'],
            minlength: [6, 'Password must be at least 6 characters'],
            select: false
        },
        avatarColorHex: {
            type: String,
            default: '#10B981'
        },
        defaultCurrency: {
            type: String,
            default: 'INR'
        }
    },
    {
        timestamps: true
    }
);

UserSchema.pre('save', async function (next) {
    if (!this.isModified('password')) return next();
    const salt = await bcrypt.genSalt(10);
    this.password = await bcrypt.hash(this.password, salt);
    next();
});

UserSchema.methods.comparePassword = function (entered) {
    return bcrypt.compare(entered, this.password);
};

UserSchema.methods.generateJwtToken = function () {
    return jwt.sign({ id: this._id }, config.jwtSecret, {
        expiresIn: config.jwtExpire
    });
};

module.exports = mongoose.model('User', UserSchema);
