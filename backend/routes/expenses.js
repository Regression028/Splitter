const express = require('express');
const router = express.Router();
const Expense = require('../models/Expense');
const { protect } = require('../middleware/auth');

// @route   POST /api/expenses
// @desc    Add a new expense
router.post('/', protect, async (req, res) => {
    try {
        const { title, amount, paidBy, groupId, category, splitType, splits } = req.body;

        if (!title || !amount || !paidBy) {
            return res.status(400).json({ success: false, message: 'Please provide title, amount, and paidBy' });
        }

        const expense = await Expense.create({
            title,
            amount,
            paidBy,
            group: groupId || null,
            category: category || 'Food',
            splitType: splitType || 'EQUAL',
            splits: splits || []
        });

        const populated = await Expense.findById(expense._id)
            .populate('paidBy', 'name email avatarColorHex')
            .populate('splits.user', 'name email avatarColorHex');

        res.status(201).json({ success: true, expense: populated });
    } catch (error) {
        res.status(500).json({ success: false, message: error.message });
    }
});

// @route   GET /api/expenses
// @desc    Get all expenses involving current user
router.get('/', protect, async (req, res) => {
    try {
        const expenses = await Expense.find({
            $or: [
                { paidBy: req.user._id },
                { 'splits.user': req.user._id }
            ]
        })
            .populate('paidBy', 'name email avatarColorHex')
            .populate('group', 'name category')
            .populate('splits.user', 'name email avatarColorHex')
            .sort({ dateTimestamp: -1 });

        res.json({ success: true, expenses });
    } catch (error) {
        res.status(500).json({ success: false, message: error.message });
    }
});

// @route   GET /api/expenses/group/:groupId
// @desc    Get expenses for a specific group
router.get('/group/:groupId', protect, async (req, res) => {
    try {
        const expenses = await Expense.find({ group: req.params.groupId })
            .populate('paidBy', 'name email avatarColorHex')
            .populate('splits.user', 'name email avatarColorHex')
            .sort({ dateTimestamp: -1 });

        res.json({ success: true, expenses });
    } catch (error) {
        res.status(500).json({ success: false, message: error.message });
    }
});

module.exports = router;
