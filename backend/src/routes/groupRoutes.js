const express = require('express');
const router = express.Router();
const {
    createGroup,
    getGroups,
    getGroupById,
    addGroupMember,
    removeGroupMember,
    deleteGroup
} = require('../controllers/groupController');
const { protect } = require('../middleware/auth');
const { checkGroupMembership, checkGroupRole } = require('../middleware/groupAuth');
const { validateCreateGroup, validateAddMember } = require('../validators/groupValidator');

router.use(protect);

router.route('/')
    .get(getGroups)
    .post(validateCreateGroup, createGroup);

router.route('/:groupId')
    .get(checkGroupMembership, getGroupById)
    .delete(checkGroupMembership, checkGroupRole(['OWNER']), deleteGroup);

router.route('/:groupId/members')
    .post(checkGroupMembership, checkGroupRole(['OWNER', 'ADMIN']), validateAddMember, addGroupMember);

router.route('/:groupId/members/:userId')
    .delete(checkGroupMembership, removeGroupMember);

module.exports = router;
