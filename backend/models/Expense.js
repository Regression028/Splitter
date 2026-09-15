const mongoose = require('mongoose');

const ExpenseSplitSchema = new mongoose.Schema({
    user: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'User',
        required: true
    },
    amountOwed: {
        type: Number,
        required: true
    }
});

const ExpenseSchema = new mongoose.Schema({
    title: {
        type: String,
        required: true,
        trim: true
    },
    amount: {
        type: Number,
        required: true
    },
    paidBy: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'User',
        required: true
    },
    group: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'Group',
        default: null
    },
    category: {
        type: String,
        default: 'Food'
    },
    splitType: {
        type: String,
        default: 'EQUAL'
    },
    splits: [ExpenseSplitSchema],
    dateTimestamp: {
        type: Date,
        default: Date.now
    }
});

module.exports = mongoose.model('Expense', ExpenseSchema);
