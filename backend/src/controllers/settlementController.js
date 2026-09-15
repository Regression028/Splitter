const Settlement = require('../models/Settlement');
const ApiError = require('../utils/ApiError');
const ApiResponse = require('../utils/ApiResponse');
const { toPaise } = require('../utils/money');

// @desc    Record a settlement payment
// @route   POST /api/v1/groups/:groupId/settlements OR POST /api/v1/settlements
// @access  Private
const createSettlement = async (req, res, next) => {
    try {
        const groupId = req.params.groupId || req.body.groupId || null;
        const { payerId, payeeId, amount, note } = req.body;

        const amountPaise = toPaise(Number(amount));

        const settlement = await Settlement.create({
            groupId,
            payer: payerId,
            payee: payeeId,
            amountPaise,
            currency: 'INR',
            note: note || 'Settled payment'
        });

        const populated = await Settlement.findById(settlement._id)
            .populate('payer', 'name email avatarColorHex')
            .populate('payee', 'name email avatarColorHex');

        return ApiResponse.success(res, { settlement: populated }, 201);
    } catch (error) {
        next(error);
    }
};

// @desc    Get group settlements
// @route   GET /api/v1/groups/:groupId/settlements
// @access  Private (Group Members)
const getGroupSettlements = async (req, res, next) => {
    try {
        const groupId = req.params.groupId;
        const settlements = await Settlement.find({ groupId })
            .populate('payer', 'name email avatarColorHex')
            .populate('payee', 'name email avatarColorHex')
            .sort({ dateTimestamp: -1 });

        return ApiResponse.success(res, { settlements });
    } catch (error) {
        next(error);
    }
};

// @desc    Get all user settlements
// @route   GET /api/v1/settlements
// @access  Private
const getUserSettlements = async (req, res, next) => {
    try {
        const settlements = await Settlement.find({
            $or: [
                { payer: req.user._id },
                { payee: req.user._id }
            ]
        })
            .populate('payer', 'name email avatarColorHex')
            .populate('payee', 'name email avatarColorHex')
            .sort({ dateTimestamp: -1 });

        return ApiResponse.success(res, { settlements });
    } catch (error) {
        next(error);
    }
};

module.exports = {
    createSettlement,
    getGroupSettlements,
    getUserSettlements
};
