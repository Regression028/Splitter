const { simplifyDebts } = require('../src/services/debtSimplificationService');

describe('Debt Simplification Engine', () => {
    const userMap = {
        'u1': { id: 'u1', name: 'Alice' },
        'u2': { id: 'u2', name: 'Bob' },
        'u3': { id: 'u3', name: 'Charlie' }
    };

    test('chained debts simplify directly (Bob owes Alice 500, Alice owes Charlie 500 -> Bob owes Charlie 500)', () => {
        const netBalancesPaise = {
            'u1': 0,        // Alice net balance 0
            'u2': -50000,   // Bob owes 500.00 (50000 paise)
            'u3': 50000     // Charlie is owed 500.00 (50000 paise)
        };

        const simplified = simplifyDebts(netBalancesPaise, userMap);

        expect(simplified.length).toBe(1);
        expect(simplified[0].fromUser.id).toBe('u2');
        expect(simplified[0].toUser.id).toBe('u3');
        expect(simplified[0].amountPaise).toBe(50000);
        expect(simplified[0].amountRupees).toBe(500.0);
    });

    test('fully settled balances return empty array', () => {
        const netBalancesPaise = {
            'u1': 0,
            'u2': 0,
            'u3': 0
        };

        const simplified = simplifyDebts(netBalancesPaise, userMap);
        expect(simplified.length).toBe(0);
    });
});
