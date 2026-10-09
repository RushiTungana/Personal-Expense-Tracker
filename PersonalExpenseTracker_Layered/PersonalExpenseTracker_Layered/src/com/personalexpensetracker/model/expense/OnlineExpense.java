package com.personalexpensetracker.model.expense;

import java.time.LocalDate;

/**
 * Represents an expense paid online.
 */
public class OnlineExpense extends Expense {

    public OnlineExpense(int id, LocalDate date, String category,
                         int amountRupees, String note) {
        super(id, date, category, amountRupees, note);
    }

    @Override
    public String getPaymentMode() {
        return "ONLINE";
    }
}
