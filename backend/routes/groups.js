const express = require('express');
const router = express.Router();
const Group = require('../models/Group');
const { protect } = require('../middleware/auth');

// @route   POST /api/groups
// @desc    Create a new group
router.post('/', protect, async (req, res) => {
    try {
        const { name, description, category, memberIds } = req.body;

        if (!name) {
            return res.status(400).json({ success: false, message: 'Group name is required' });
        }

        const allMembers = Array.from(new Set([...(memberIds || []), req.user._id.toString()]));

        const group = await Group.create({
            name,
            description: description || '',
            category: category || 'Trip',
            createdBy: req.user._id,
            members: allMembers
        });

        const populatedGroup = await Group.findById(group._id).populate('members', 'name email avatarColorHex');

        res.status(201).json({ success: true, group: populatedGroup });
    } catch (error) {
        res.status(500).json({ success: false, message: error.message });
    }
});

// @route   GET /api/groups
// @desc    Get all groups for current user
router.get('/', protect, async (req, res) => {
    try {
        const groups = await Group.find({ members: req.user._id })
            .populate('members', 'name email avatarColorHex')
            .sort({ createdAt: -1 });

        res.json({ success: true, groups });
    } catch (error) {
        res.status(500).json({ success: false, message: error.message });
    }
});

// @route   GET /api/groups/:id
// @desc    Get group detail by ID
router.get('/:id', protect, async (req, res) => {
    try {
        const group = await Group.findById(req.params.id)
            .populate('members', 'name email avatarColorHex');

        if (!group) {
            return res.status(404).json({ success: false, message: 'Group not found' });
        }

        res.json({ success: true, group });
    } catch (error) {
        res.status(500).json({ success: false, message: error.message });
    }
});

module.exports = router;
