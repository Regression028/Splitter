const express = require('express');
const router = express.Router({ mergeParams: true });
const {
    createSettlement,
    getGroupSettlements,
    getUserSettlements
} = require('../controllers/settlementController');
const { protect } = require('../middleware/auth');
const { checkGroupMembership } = require('../middleware/groupAuth');
const { validateCreateSettlement } = require('../validators/settlementValidator');

router.use(protect);

router.route('/')
    .get((req, res, next) => {
        if (req.params.groupId) {
            return checkGroupMembership(req, res, () => getGroupSettlements(req, res, next));
        } else {
            return getUserSettlements(req, res, next);
        }
    })
    .post(validateCreateSettlement, createSettlement);

module.exports = router;
