const express = require('express');
const router = express.Router({ mergeParams: true });
const {
    createExpense,
    getGroupExpenses,
    deleteExpense
} = require('../controllers/expenseController');
const { protect } = require('../middleware/auth');
const { checkGroupMembership } = require('../middleware/groupAuth');
const { validateCreateExpense } = require('../validators/expenseValidator');

router.use(protect);

router.route('/')
    .get(checkGroupMembership, getGroupExpenses)
    .post(checkGroupMembership, validateCreateExpense, createExpense);

router.route('/:expenseId')
    .delete(deleteExpense);

module.exports = router;
