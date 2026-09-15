const GroupMember = require('../models/GroupMember');
const ApiError = require('../utils/ApiError');

const checkGroupMembership = async (req, res, next) => {
    try {
        const groupId = req.params.groupId || req.params.id || req.body.groupId;
        if (!groupId) {
            return next(new ApiError(400, 'Group ID is required', 'MISSING_GROUP_ID'));
        }

        const membership = await GroupMember.findOne({
            groupId: groupId,
            userId: req.user._id
        });

        if (!membership) {
            return next(new ApiError(403, 'Access denied. You are not a member of this group.', 'NOT_GROUP_MEMBER'));
        }

        req.groupMember = membership;
        next();
    } catch (error) {
        next(error);
    }
};

const checkGroupRole = (allowedRoles = ['OWNER', 'ADMIN']) => {
    return (req, res, next) => {
        if (!req.groupMember || !allowedRoles.includes(req.groupMember.role)) {
            return next(new ApiError(403, 'Permission denied. You do not have authorization for this action in the group.', 'INSUFFICIENT_ROLE'));
        }
        next();
    };
};

module.exports = {
    checkGroupMembership,
    checkGroupRole
};
