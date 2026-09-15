const mongoose = require('mongoose');

const ParticipantSchema = new mongoose.Schema({
    userId: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'User',
        required: true
    },
    amountOwedPaise: {
        type: Number,
        required: true,
        min: 0
    },
    percentage: {
        type: Number,
        default: null
    }
});

const ExpenseSchema = new mongoose.Schema(
    {
        groupId: {
            type: mongoose.Schema.Types.ObjectId,
            ref: 'Group',
            required: true,
            index: true
        },
        title: {
            type: String,
            required: [true, 'Expense title is required'],
            trim: true,
            maxlength: [100, 'Title cannot exceed 100 characters']
        },
        totalAmountPaise: {
            type: Number,
            required: [true, 'Total amount is required'],
            min: [1, 'Expense total amount must be at least ₹0.01']
        },
        currency: {
            type: String,
            default: 'INR'
        },
        paidBy: {
            type: mongoose.Schema.Types.ObjectId,
            ref: 'User',
            required: true,
            index: true
        },
        category: {
            type: String,
            enum: ['Food', 'Transport', 'Utilities', 'Hotels', 'Entertainment', 'Groceries', 'Shopping', 'Other'],
            default: 'Food'
        },
        splitType: {
            type: String,
            enum: ['EQUAL', 'EXACT', 'PERCENTAGE'],
            required: true,
            default: 'EQUAL'
        },
        participants: [ParticipantSchema],
        createdBy: {
            type: mongoose.Schema.Types.ObjectId,
            ref: 'User',
            required: true
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

module.exports = mongoose.model('Expense', ExpenseSchema);
