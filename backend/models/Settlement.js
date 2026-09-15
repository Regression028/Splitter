const mongoose = require('mongoose');

const SettlementSchema = new mongoose.Schema({
    payer: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'User',
        required: true
    },
    payee: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'User',
        required: true
    },
    group: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'Group',
        default: null
    },
    amount: {
        type: Number,
        required: true
    },
    note: {
        type: String,
        default: 'Settled up'
    },
    dateTimestamp: {
        type: Date,
        default: Date.now
    }
});

module.exports = mongoose.model('Settlement', SettlementSchema);
