package com.personalexpensetracker.service;

import com.personalexpensetracker.model.budget.Budget;
import com.personalexpensetracker.model.expense.CashExpense;
import com.personalexpensetracker.model.expense.Expense;
import com.personalexpensetracker.model.expense.OnlineExpense;
import com.personalexpensetracker.repository.file.FileManager;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Service/business layer.
 * Contains the application's main operations and calculations.
 */
public class ExpenseService {

    private static final String[] CATEGORIES = {
            "Food", "Travel", "Study", "Other"
    };

    private final ArrayList<Expense> expenses;
    private final Budget budget;
    private final FileManager fileManager;

    private int nextId;

    public ExpenseService(List<Expense> loadedExpenses,
                          Budget budget,
                          FileManager fileManager) {

        this.expenses = new ArrayList<>(loadedExpenses);
        this.budget = budget;
        this.fileManager = fileManager;
        this.nextId = calculateNextId();
    }

    public Expense addExpense(LocalDate date,
                              String category,
                              int amountRupees,
                              String paymentMode,
                              String note) throws IOException {

        validateExpenseInput(
                date, category, amountRupees, paymentMode, note);

        int id = nextId;

        Expense expense;

        if (paymentMode.equals("CASH")) {
            expense = new CashExpense(
                    id, date, category, amountRupees, note);
        } else {
            expense = new OnlineExpense(
                    id, date, category, amountRupees, note);
        }

        ArrayList<Expense> candidate =
                new ArrayList<>(expenses);

        candidate.add(expense);

        // Save first. Change in-memory list only after saving succeeds.
        fileManager.saveExpenses(candidate);

        expenses.add(expense);
        nextId++;

        return expense;
    }

    public List<Expense> getExpenses() {
        return List.copyOf(expenses);
    }

    public List<Expense> filterByCategory(String category) {
        if (!isValidCategory(category)) {
            return List.of();
        }

        List<Expense> result = new ArrayList<>();

        for (Expense expense : expenses) {
            if (expense.getCategory().equals(category)) {
                result.add(expense);
            }
        }

        return result;
    }

    public long[] getTotals() {
        long[] totals = new long[CATEGORIES.length];

        for (Expense expense : expenses) {
            for (int i = 0; i < CATEGORIES.length; i++) {
                if (CATEGORIES[i].equals(
                        expense.getCategory())) {

                    totals[i] += expense.getAmountRupees();
                    break;
                }
            }
        }

        return totals;
    }

    public long getTotalSpending() {
        long total = 0;

        for (Expense expense : expenses) {
            total += expense.getAmountRupees();
        }

        return total;
    }

    public Set<String> getUsedCategories() {
        Set<String> usedCategories = new HashSet<>();

        for (Expense expense : expenses) {
            usedCategories.add(expense.getCategory());
        }

        return Set.copyOf(usedCategories);
    }

    public void setBudget(int amountRupees) throws IOException {
        if (amountRupees < 1 || amountRupees > 1_000_000) {
            throw new IllegalArgumentException(
                    "Budget must be between 1 and 1000000 rupees.");
        }

        // Save first. Update the object only if saving succeeds.
        fileManager.saveBudget(amountRupees);
        budget.setAmountRupees(amountRupees);
    }

    public int getBudget() {
        return budget.getAmountRupees();
    }

    public String showSummary() {
        long totalSpending = getTotalSpending();
        long remaining = (long) budget.getAmountRupees()
                - totalSpending;

        long[] totals = getTotals();

        StringBuilder summary = new StringBuilder();

        summary.append("\nSPENDING SUMMARY\n");
        summary.append("================\n");
        summary.append("Expense count: ")
                .append(expenses.size()).append('\n');
        summary.append("Total spending: ")
                .append(totalSpending)
                .append(" rupees\n");
        summary.append("Budget: ")
                .append(budget.getAmountRupees())
                .append(" rupees\n");

        if (remaining >= 0) {
            summary.append("Remaining: ")
                    .append(remaining)
                    .append(" rupees\n");
        } else {
            summary.append("Over budget by: ")
                    .append(Math.abs(remaining))
                    .append(" rupees\n");
        }

        summary.append("Food: ")
                .append(totals[0])
                .append(" | Travel: ")
                .append(totals[1]).append('\n');

        summary.append("Study: ")
                .append(totals[2])
                .append(" | Other: ")
                .append(totals[3]).append('\n');

        summary.append("Categories used: ")
                .append(getUsedCategories().size())
                .append('\n');

        return summary.toString();
    }

    public void saveReport() throws IOException {
        StringBuilder report = new StringBuilder();

        report.append("PERSONAL EXPENSE TRACKER REPORT\n");
        report.append("===============================\n");
        report.append(showSummary()).append('\n');
        report.append("EXPENSE HISTORY\n");
        report.append("===============\n");

        if (expenses.isEmpty()) {
            report.append("No records found.\n");
        } else {
            for (Expense expense : expenses) {
                report.append("ID: ")
                        .append(expense.getId())
                        .append(" | Date: ")
                        .append(expense.getDate())
                        .append(" | Category: ")
                        .append(expense.getCategory())
                        .append(" | Amount: ")
                        .append(expense.getAmountRupees())
                        .append(" | Payment: ")
                        .append(expense.getPaymentMode())
                        .append(" | Note: ")
                        .append(expense.getNote())
                        .append('\n');
            }
        }

        fileManager.saveReport(report.toString());
    }

    private int calculateNextId() {
        int maxId = 0;

        for (Expense expense : expenses) {
            if (expense.getId() > maxId) {
                maxId = expense.getId();
            }
        }

        return maxId + 1;
    }

    private void validateExpenseInput(LocalDate date,
                                      String category,
                                      int amountRupees,
                                      String paymentMode,
                                      String note) {

        if (date == null) {
            throw new IllegalArgumentException(
                    "Date is required.");
        }

        if (!isValidCategory(category)) {
            throw new IllegalArgumentException(
                    "Invalid category.");
        }

        if (amountRupees < 1 || amountRupees > 1_000_000) {
            throw new IllegalArgumentException(
                    "Amount must be between 1 and 1000000 rupees.");
        }

        if (!paymentMode.equals("CASH")
                && !paymentMode.equals("ONLINE")) {
            throw new IllegalArgumentException(
                    "Payment must be CASH or ONLINE.");
        }

        if (note == null || note.isBlank()) {
            throw new IllegalArgumentException(
                    "Note cannot be blank.");
        }

        if (note.contains("|")
                || note.contains("\n")
                || note.contains("\r")) {
            throw new IllegalArgumentException(
                    "Note cannot contain | or line breaks.");
        }
    }

    private boolean isValidCategory(String category) {
        for (String allowed : CATEGORIES) {
            if (allowed.equals(category)) {
                return true;
            }
        }
        return false;
    }
}
