const Expense = require('../models/Expense');
const ApiError = require('../utils/ApiError');
const ApiResponse = require('../utils/ApiResponse');
const { toPaise } = require('../utils/money');

// @desc    Create a new expense in a group
// @route   POST /api/v1/groups/:groupId/expenses
// @access  Private (Group Members)
const createExpense = async (req, res, next) => {
    try {
        const groupId = req.params.groupId || req.body.groupId;
        const { title, amount, paidBy, category, splitType, participants } = req.body;

        const totalAmountPaise = toPaise(Number(amount));
        const count = participants.length;

        let processedParticipants = [];

        if (splitType === 'EQUAL') {
            const baseSharePaise = Math.floor(totalAmountPaise / count);
            let remainderPaise = totalAmountPaise - (baseSharePaise * count);

            processedParticipants = participants.map(p => {
                let share = baseSharePaise;
                // Add remainder paise to payer or first participant
                if (p.userId.toString() === paidBy.toString() && remainderPaise > 0) {
                    share += remainderPaise;
                    remainderPaise = 0;
                }
                return {
                    userId: p.userId,
                    amountOwedPaise: share,
                    percentage: Number((100 / count).toFixed(2))
                };
            });
        } else if (splitType === 'EXACT') {
            processedParticipants = participants.map(p => ({
                userId: p.userId,
                amountOwedPaise: toPaise(Number(p.amountOwed || 0)),
                percentage: Number(((toPaise(Number(p.amountOwed || 0)) / totalAmountPaise) * 100).toFixed(2))
            }));
        } else if (splitType === 'PERCENTAGE') {
            let runningSum = 0;
            processedParticipants = participants.map((p, idx) => {
                let share = Math.round((totalAmountPaise * Number(p.percentage)) / 100);
                if (idx === count - 1) {
                    // Adjust last participant for integer rounding
                    share = totalAmountPaise - runningSum;
                } else {
                    runningSum += share;
                }
                return {
                    userId: p.userId,
                    amountOwedPaise: share,
                    percentage: Number(p.percentage)
                };
            });
        }

        const expense = await Expense.create({
            groupId,
            title,
            totalAmountPaise,
            currency: 'INR',
            paidBy,
            category: category || 'Food',
            splitType,
            participants: processedParticipants,
            createdBy: req.user._id
        });

        const populated = await Expense.findById(expense._id)
            .populate('paidBy', 'name email avatarColorHex')
            .populate('participants.userId', 'name email avatarColorHex');

        return ApiResponse.success(res, { expense: populated }, 201);
    } catch (error) {
        next(error);
    }
};

// @desc    Get expenses for a group
// @route   GET /api/v1/groups/:groupId/expenses
// @access  Private (Group Members)
const getGroupExpenses = async (req, res, next) => {
    try {
        const groupId = req.params.groupId;
        const expenses = await Expense.find({ groupId })
            .populate('paidBy', 'name email avatarColorHex')
            .populate('participants.userId', 'name email avatarColorHex')
            .sort({ dateTimestamp: -1 });

        return ApiResponse.success(res, { expenses });
    } catch (error) {
        next(error);
    }
};

// @desc    Delete expense
// @route   DELETE /api/v1/expenses/:expenseId
// @access  Private (Payer or Group Admin/Owner)
const deleteExpense = async (req, res, next) => {
    try {
        const expenseId = req.params.expenseId || req.params.id;
        const expense = await Expense.findById(expenseId);
        if (!expense) {
            return next(new ApiError(404, 'Expense not found', 'EXPENSE_NOT_FOUND'));
        }

        await Expense.deleteOne({ _id: expenseId });

        return ApiResponse.success(res, { message: 'Expense deleted successfully' });
    } catch (error) {
        next(error);
    }
};

module.exports = {
    createExpense,
    getGroupExpenses,
    deleteExpense
};
