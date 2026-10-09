package com.personalexpensetracker.model.expense;

import java.time.LocalDate;

/**
 * Abstract parent class for all expenses.
 * Demonstrates abstraction and encapsulation.
 */
public abstract class Expense {

    private final int id;
    private final LocalDate date;
    private final String category;
    private final int amountRupees;
    private final String note;

    protected Expense(int id, LocalDate date, String category,
                      int amountRupees, String note) {
        this.id = id;
        this.date = date;
        this.category = category;
        this.amountRupees = amountRupees;
        this.note = note;
    }

    public int getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getCategory() {
        return category;
    }

    public int getAmountRupees() {
        return amountRupees;
    }

    public String getNote() {
        return note;
    }

    public abstract String getPaymentMode();
}
