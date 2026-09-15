const Expense = require('../models/Expense');
const Settlement = require('../models/Settlement');
const GroupMember = require('../models/GroupMember');
const { toRupees } = require('../utils/money');

/**
 * Calculates net balances for all members in a group (in Integer Paise)
 * and simplifies debts using the Min-Cash-Flow Algorithm.
 */
const calculateGroupBalancesAndDebts = async (groupId) => {
    // 1. Fetch group members
    const members = await GroupMember.find({ groupId }).populate('userId', 'name email avatarColorHex');
    const userMap = {};
    const netBalancesPaise = {};

    members.forEach(m => {
        const u = m.userId;
        userMap[u._id.toString()] = {
            id: u._id.toString(),
            name: u.name,
            email: u.email,
            avatarColorHex: u.avatarColorHex
        };
        netBalancesPaise[u._id.toString()] = 0;
    });

    // 2. Process Expenses
    const expenses = await Expense.find({ groupId });
    let totalSpendingPaise = 0;

    expenses.forEach(expense => {
        totalSpendingPaise += expense.totalAmountPaise;

        const payerId = expense.paidBy.toString();
        // Credit payer with total amount
        if (netBalancesPaise[payerId] !== undefined) {
            netBalancesPaise[payerId] += expense.totalAmountPaise;
        }

        // Debit participants for their respective splits
        expense.participants.forEach(p => {
            const pId = p.userId.toString();
            if (netBalancesPaise[pId] !== undefined) {
                netBalancesPaise[pId] -= p.amountOwedPaise;
            }
        });
    });

    // 3. Process Settlements
    const settlements = await Settlement.find({ groupId });
    settlements.forEach(settlement => {
        const payerId = settlement.payer.toString();
        const payeeId = settlement.payee.toString();

        // Payer's net balance increases (debt decreases)
        if (netBalancesPaise[payerId] !== undefined) {
            netBalancesPaise[payerId] += settlement.amountPaise;
        }

        // Payee's net balance decreases (credit decreases)
        if (netBalancesPaise[payeeId] !== undefined) {
            netBalancesPaise[payeeId] -= settlement.amountPaise;
        }
    });

    // 4. Run Min-Cash-Flow Algorithm for Debt Simplification
    const simplifiedDebts = simplifyDebts(netBalancesPaise, userMap);

    // 5. Format User Balances List
    const userBalances = Object.keys(netBalancesPaise).map(userId => ({
        user: userMap[userId],
        netBalancePaise: netBalancesPaise[userId],
        netBalanceRupees: toRupees(netBalancesPaise[userId])
    }));

    return {
        totalSpendingPaise,
        totalSpendingRupees: toRupees(totalSpendingPaise),
        userBalances,
        simplifiedDebts
    };
};

/**
 * Min-Cash-Flow Algorithm
 */
const simplifyDebts = (netBalancesPaise, userMap) => {
    const debtors = [];   // (userId, amountOwed [positive])
    const creditors = []; // (userId, amountIsOwed [positive])

    for (const [userId, balance] of Object.entries(netBalancesPaise)) {
        if (balance < 0) {
            debtors.push({ userId, amount: Math.abs(balance) });
        } else if (balance > 0) {
            creditors.push({ userId, amount: balance });
        }
    }

    const result = [];
    let debtorIdx = 0;
    let creditorIdx = 0;

    while (debtorIdx < debtors.length && creditorIdx < creditors.length) {
        const debtor = debtors[debtorIdx];
        const creditor = creditors[creditorIdx];

        const minAmount = Math.min(debtor.amount, creditor.amount);

        if (minAmount > 0) {
            result.push({
                fromUser: userMap[debtor.userId] || { id: debtor.userId, name: 'Unknown' },
                toUser: userMap[creditor.userId] || { id: creditor.userId, name: 'Unknown' },
                amountPaise: minAmount,
                amountRupees: toRupees(minAmount)
            });
        }

        debtor.amount -= minAmount;
        creditor.amount -= minAmount;

        if (debtor.amount === 0) debtorIdx++;
        if (creditor.amount === 0) creditorIdx++;
    }

    return result;
};

module.exports = {
    calculateGroupBalancesAndDebts,
    simplifyDebts
};
