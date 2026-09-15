const { body, validationResult } = require('express-validator');
const ApiResponse = require('../utils/ApiResponse');

const handleValidationErrors = (req, res, next) => {
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
        const firstError = errors.array()[0].msg;
        return ApiResponse.error(res, 400, firstError, 'VALIDATION_ERROR');
    }
    next();
};

const validateCreateSettlement = [
    body('payerId').notEmpty().withMessage('Payer (who paid) is required'),
    body('payeeId').notEmpty().withMessage('Payee (who received) is required'),
    body('amount').isNumeric().withMessage('Amount must be a number').custom(val => val > 0).withMessage('Amount must be greater than 0'),

    (req, res, next) => {
        const { payerId, payeeId } = req.body;
        if (payerId === payeeId) {
            return ApiResponse.error(res, 400, 'Payer and payee cannot be the same user', 'INVALID_SETTLEMENT');
        }
        next();
    },
    handleValidationErrors
];

module.exports = {
    validateCreateSettlement
};
