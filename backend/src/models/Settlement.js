const mongoose = require('mongoose');

const SettlementSchema = new mongoose.Schema(
    {
        groupId: {
            type: mongoose.Schema.Types.ObjectId,
            ref: 'Group',
            default: null,
            index: true
        },
        payer: {
            type: mongoose.Schema.Types.ObjectId,
            ref: 'User',
            required: [true, 'Payer user ID is required'],
            index: true
        },
        payee: {
            type: mongoose.Schema.Types.ObjectId,
            ref: 'User',
            required: [true, 'Payee user ID is required'],
            index: true
        },
        amountPaise: {
            type: Number,
            required: [true, 'Settlement amount is required'],
            min: [1, 'Settlement amount must be at least ₹0.01']
        },
        currency: {
            type: String,
            default: 'INR'
        },
        note: {
            type: String,
            default: 'Settled payment',
            trim: true,
            maxlength: [100, 'Note cannot exceed 100 characters']
        },
        dateTimestamp: {
            type: Date,
            default: Date.now,
            index: true
        }
    },
    {
        timestamps: true
    }
);

module.exports = mongoose.model('Settlement', SettlementSchema);
