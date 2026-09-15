const { body, validationResult } = require('express-validator');
const ApiResponse = require('../utils/ApiResponse');
const { toPaise } = require('../utils/money');

const handleValidationErrors = (req, res, next) => {
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
        const firstError = errors.array()[0].msg;
        return ApiResponse.error(res, 400, firstError, 'VALIDATION_ERROR');
    }
    next();
};

const validateCreateExpense = [
    body('title').trim().notEmpty().withMessage('Expense title is required').isLength({ max: 100 }),
    body('amount').isNumeric().withMessage('Expense amount must be a number').custom(val => val > 0).withMessage('Amount must be greater than 0'),
    body('paidBy').notEmpty().withMessage('Payer (paidBy) is required'),
    body('splitType').isIn(['EQUAL', 'EXACT', 'PERCENTAGE']).withMessage('Split type must be EQUAL, EXACT, or PERCENTAGE'),
    body('participants').isArray({ min: 1 }).withMessage('At least 1 participant is required for the expense'),

    // Financial Split Calculations Verification
    (req, res, next) => {
        const errors = validationResult(req);
        if (!errors.isEmpty()) return handleValidationErrors(req, res, next);

        const { amount, splitType, participants } = req.body;
        const totalPaise = toPaise(Number(amount));

        if (splitType === 'EXACT') {
            const sumPaise = participants.reduce((sum, p) => sum + toPaise(Number(p.amountOwed || 0)), 0);
            if (sumPaise !== totalPaise) {
                return ApiResponse.error(
                    res,
                    400,
                    `Exact split sum (₹${(sumPaise/100).toFixed(2)}) does not match total expense amount (₹${(totalPaise/100).toFixed(2)})`,
                    'INVALID_SPLIT_TOTAL'
                );
            }
        } else if (splitType === 'PERCENTAGE') {
            const sumPercentage = participants.reduce((sum, p) => sum + Number(p.percentage || 0), 0);
            if (Math.abs(sumPercentage - 100.0) > 0.01) {
                return ApiResponse.error(
                    res,
                    400,
                    `Percentage split sum (${sumPercentage.toFixed(1)}%) must equal exactly 100%`,
                    'INVALID_PERCENTAGE_TOTAL'
                );
            }
        }

        next();
    }
];

module.exports = {
    validateCreateExpense
};
