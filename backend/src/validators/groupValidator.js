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

const validateCreateGroup = [
    body('name').trim().notEmpty().withMessage('Group name is required').isLength({ max: 60 }).withMessage('Group name cannot exceed 60 characters'),
    body('category').optional().isIn(['Trip', 'Home', 'Couples', 'Entertainment', 'Other']).withMessage('Invalid group category'),
    handleValidationErrors
];

const validateAddMember = [
    body('email').trim().isEmail().withMessage('Please provide a valid user email address').normalizeEmail(),
    handleValidationErrors
];

module.exports = {
    validateCreateGroup,
    validateAddMember
};
