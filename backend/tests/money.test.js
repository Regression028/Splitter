const { toPaise, toRupees, formatCurrency } = require('../src/utils/money');

describe('Money Handling Utilities', () => {
    test('toPaise converts float rupees to integer paise accurately', () => {
        expect(toPaise(10.50)).toBe(1050);
        expect(toPaise(0.99)).toBe(99);
        expect(toPaise(100)).toBe(10000);
        expect(toPaise('invalid')).toBe(0);
    });

    test('toRupees converts integer paise to float rupees accurately', () => {
        expect(toRupees(1050)).toBe(10.50);
        expect(toRupees(99)).toBe(0.99);
        expect(toRupees(10000)).toBe(100.00);
        expect(toRupees('invalid')).toBe(0.0);
    });

    test('formatCurrency formats paise as currency string', () => {
        expect(formatCurrency(1050)).toBe('₹10.50');
        expect(formatCurrency(0)).toBe('₹0.00');
    });
});
