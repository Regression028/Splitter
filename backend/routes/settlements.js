const express = require('express');
const router = express.Router();
const Settlement = require('../models/Settlement');
const { protect } = require('../middleware/auth');

// @route   POST /api/settlements
// @desc    Record a settlement payment
router.post('/', protect, async (req, res) => {
    try {
        const { payerId, payeeId, groupId, amount, note } = req.body;

        if (!payerId || !payeeId || !amount) {
            return res.status(400).json({ success: false, message: 'Please provide payer, payee, and amount' });
        }

        const settlement = await Settlement.create({
            payer: payerId,
            payee: payeeId,
            group: groupId || null,
            amount,
            note: note || 'Settled up'
        });

        const populated = await Settlement.findById(settlement._id)
            .populate('payer', 'name email avatarColorHex')
            .populate('payee', 'name email avatarColorHex');

        res.status(201).json({ success: true, settlement: populated });
    } catch (error) {
        res.status(500).json({ success: false, message: error.message });
    }
});

// @route   GET /api/settlements
// @desc    Get all settlements involving current user
router.get('/', protect, async (req, res) => {
    try {
        const settlements = await Settlement.find({
            $or: [
                { payer: req.user._id },
                { payee: req.user._id }
            ]
        })
            .populate('payer', 'name email avatarColorHex')
            .populate('payee', 'name email avatarColorHex')
            .populate('group', 'name category')
            .sort({ dateTimestamp: -1 });

        res.json({ success: true, settlements });
    } catch (error) {
        res.status(500).json({ success: false, message: error.message });
    }
});

// @route   GET /api/settlements/group/:groupId
// @desc    Get settlements for a group
router.get('/group/:groupId', protect, async (req, res) => {
    try {
        const settlements = await Settlement.find({ group: req.params.groupId })
            .populate('payer', 'name email avatarColorHex')
            .populate('payee', 'name email avatarColorHex')
            .sort({ dateTimestamp: -1 });

        res.json({ success: true, settlements });
    } catch (error) {
        res.status(500).json({ success: false, message: error.message });
    }
});

module.exports = router;
