const Group = require('../models/Group');
const GroupMember = require('../models/GroupMember');
const User = require('../models/User');
const ApiError = require('../utils/ApiError');
const ApiResponse = require('../utils/ApiResponse');

// @desc    Create a new group
// @route   POST /api/v1/groups
// @access  Private
const createGroup = async (req, res, next) => {
    try {
        const { name, description, category, memberEmails } = req.body;

        const group = await Group.create({
            name,
            description: description || '',
            category: category || 'Trip',
            createdBy: req.user._id
        });

        // Add creator as OWNER
        await GroupMember.create({
            groupId: group._id,
            userId: req.user._id,
            role: 'OWNER'
        });

        // Add initial members by email if provided
        if (Array.isArray(memberEmails) && memberEmails.length > 0) {
            const users = await User.find({ email: { $in: memberEmails, $ne: req.user.email } });
            const memberDocs = users.map(user => ({
                groupId: group._id,
                userId: user._id,
                role: 'MEMBER'
            }));
            if (memberDocs.length > 0) {
                await GroupMember.insertMany(memberDocs, { ordered: false }).catch(() => {});
            }
        }

        const members = await GroupMember.find({ groupId: group._id }).populate('userId', 'name email avatarColorHex');

        return ApiResponse.success(res, { group, members, myRole: 'OWNER' }, 201);
    } catch (error) {
        next(error);
    }
};

// @desc    Get all groups for logged-in user
// @route   GET /api/v1/groups
// @access  Private
const getGroups = async (req, res, next) => {
    try {
        const myMemberships = await GroupMember.find({ userId: req.user._id });
        const groupIds = myMemberships.map(m => m.groupId);

        const groups = await Group.find({ _id: { $in: groupIds } }).sort({ createdAt: -1 });

        const groupsWithMembers = await Promise.all(
            groups.map(async (group) => {
                const members = await GroupMember.find({ groupId: group._id }).populate('userId', 'name email avatarColorHex');
                const myMembership = myMemberships.find(m => m.groupId.toString() === group._id.toString());
                return {
                    id: group._id,
                    name: group.name,
                    description: group.description,
                    category: group.category,
                    createdBy: group.createdBy,
                    createdAt: group.createdAt,
                    myRole: myMembership ? myMembership.role : 'MEMBER',
                    members: members.map(m => ({
                        id: m.userId._id,
                        name: m.userId.name,
                        email: m.userId.email,
                        avatarColorHex: m.userId.avatarColorHex,
                        role: m.role
                    }))
                };
            })
        );

        return ApiResponse.success(res, { groups: groupsWithMembers });
    } catch (error) {
        next(error);
    }
};

// @desc    Get group by ID
// @route   GET /api/v1/groups/:groupId
// @access  Private (Group Members)
const getGroupById = async (req, res, next) => {
    try {
        const groupId = req.params.groupId || req.params.id;
        const group = await Group.findById(groupId);
        if (!group) {
            return next(new ApiError(404, 'Group not found', 'GROUP_NOT_FOUND'));
        }

        const members = await GroupMember.find({ groupId: group._id }).populate('userId', 'name email avatarColorHex');

        return ApiResponse.success(res, {
            group: {
                id: group._id,
                name: group.name,
                description: group.description,
                category: group.category,
                createdBy: group.createdBy,
                createdAt: group.createdAt,
                myRole: req.groupMember.role,
                members: members.map(m => ({
                    id: m.userId._id,
                    name: m.userId.name,
                    email: m.userId.email,
                    avatarColorHex: m.userId.avatarColorHex,
                    role: m.role
                }))
            }
        });
    } catch (error) {
        next(error);
    }
};

// @desc    Add member to group
// @route   POST /api/v1/groups/:groupId/members
// @access  Private (Group Owner/Admin)
const addGroupMember = async (req, res, next) => {
    try {
        const groupId = req.params.groupId || req.params.id;
        const { email } = req.body;

        const userToAdd = await User.findOne({ email });
        if (!userToAdd) {
            return next(new ApiError(404, 'User with this email address was not found', 'USER_NOT_FOUND'));
        }

        const existingMembership = await GroupMember.findOne({ groupId, userId: userToAdd._id });
        if (existingMembership) {
            return next(new ApiError(400, 'User is already a member of this group', 'ALREADY_MEMBER'));
        }

        const newMember = await GroupMember.create({
            groupId,
            userId: userToAdd._id,
            role: 'MEMBER'
        });

        return ApiResponse.success(res, {
            member: {
                id: userToAdd._id,
                name: userToAdd.name,
                email: userToAdd.email,
                avatarColorHex: userToAdd.avatarColorHex,
                role: newMember.role
            }
        }, 201);
    } catch (error) {
        next(error);
    }
};

// @desc    Remove member from group or leave
// @route   DELETE /api/v1/groups/:groupId/members/:userId
// @access  Private
const removeGroupMember = async (req, res, next) => {
    try {
        const groupId = req.params.groupId || req.params.id;
        const targetUserId = req.params.userId;

        // If removing someone else, must be OWNER or ADMIN
        if (targetUserId !== req.user._id.toString() && !['OWNER', 'ADMIN'].includes(req.groupMember.role)) {
            return next(new ApiError(403, 'Only group Owners and Admins can remove other members', 'INSUFFICIENT_ROLE'));
        }

        await GroupMember.deleteOne({ groupId, userId: targetUserId });

        return ApiResponse.success(res, { message: 'Member removed from group' });
    } catch (error) {
        next(error);
    }
};

// @desc    Delete group
// @route   DELETE /api/v1/groups/:groupId
// @access  Private (Owner Only)
const deleteGroup = async (req, res, next) => {
    try {
        const groupId = req.params.groupId || req.params.id;

        await Group.deleteOne({ _id: groupId });
        await GroupMember.deleteMany({ groupId });

        return ApiResponse.success(res, { message: 'Group deleted successfully' });
    } catch (error) {
        next(error);
    }
};

module.exports = {
    createGroup,
    getGroups,
    getGroupById,
    addGroupMember,
    removeGroupMember,
    deleteGroup
};
