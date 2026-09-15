/**
 * Money Handling Utility
 * All internal backend calculations use Integer Paise (1 Rupee = 100 Paise)
 * to avoid floating-point inaccuracy.
 */

const toPaise = (rupeesAmount) => {
    if (typeof rupeesAmount !== 'number' || isNaN(rupeesAmount)) {
        return 0;
    }
    return Math.round(rupeesAmount * 100);
};

const toRupees = (paiseAmount) => {
    if (typeof paiseAmount !== 'number' || isNaN(paiseAmount)) {
        return 0.0;
    }
    return Math.round(paiseAmount) / 100.0;
};

const formatCurrency = (paiseAmount, currency = 'INR') => {
    const rupees = toRupees(paiseAmount);
    return `₹${rupees.toFixed(2)}`;
};

module.exports = {
    toPaise,
    toRupees,
    formatCurrency
};
