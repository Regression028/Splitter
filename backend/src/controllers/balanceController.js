const { calculateGroupBalancesAndDebts } = require('../services/debtSimplificationService');
const ApiResponse = require('../utils/ApiResponse');

// @desc    Get derived balances and simplified debts for a group
// @route   GET /api/v1/groups/:groupId/balances
// @access  Private (Group Members)
const getGroupBalances = async (req, res, next) => {
    try {
        const groupId = req.params.groupId;
        const summary = await calculateGroupBalancesAndDebts(groupId);

        return ApiResponse.success(res, { summary });
    } catch (error) {
        next(error);
    }
};

module.exports = {
    getGroupBalances
};
