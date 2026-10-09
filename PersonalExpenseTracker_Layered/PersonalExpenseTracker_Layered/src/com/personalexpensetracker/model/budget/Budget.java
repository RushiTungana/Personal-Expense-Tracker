package com.personalexpensetracker.model.budget;

/**
 * Stores the user's budget and validates budget updates.
 */
public class Budget {

    private int amountRupees;

    public Budget(int amountRupees) {
        setAmountRupees(amountRupees);
    }

    public int getAmountRupees() {
        return amountRupees;
    }

    public void setAmountRupees(int amountRupees) {
        if (amountRupees < 1 || amountRupees > 1_000_000) {
            throw new IllegalArgumentException(
                    "Budget must be between 1 and 1000000 rupees."
            );
        }
        this.amountRupees = amountRupees;
    }
}
