const express = require('express');
const router = express.Router({ mergeParams: true });
const { getGroupBalances } = require('../controllers/balanceController');
const { protect } = require('../middleware/auth');
const { checkGroupMembership } = require('../middleware/groupAuth');

router.use(protect);

router.get('/', checkGroupMembership, getGroupBalances);

module.exports = router;
