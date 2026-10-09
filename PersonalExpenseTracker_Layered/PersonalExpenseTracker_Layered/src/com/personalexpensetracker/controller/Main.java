package com.personalexpensetracker.controller;

import com.personalexpensetracker.model.budget.Budget;
import com.personalexpensetracker.model.expense.Expense;
import com.personalexpensetracker.repository.file.FileManager;
import com.personalexpensetracker.service.ExpenseService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Controller/UI layer.
 * Reads console input, displays menus, and calls the service layer.
 */
public class Main {

    private static final String[] CATEGORIES = {
            "Food", "Travel", "Study", "Other"
    };

    private static final Scanner scanner =
            new Scanner(System.in);

    public static void main(String[] args) {

        try {
            FileManager fileManager = new FileManager();

            List<Expense> loadedExpenses =
                    fileManager.loadExpenses();

            int loadedBudget =
                    fileManager.loadBudget();

            Budget budget = new Budget(loadedBudget);

            ExpenseService service =
                    new ExpenseService(
                            loadedExpenses,
                            budget,
                            fileManager
                    );

            System.out.println(
                    "Data loaded successfully.");

            runMenu(service);

        } catch (IOException | IllegalArgumentException e) {
            System.out.println(
                    "Application startup failed: "
                            + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    private static void runMenu(ExpenseService service) {

        boolean running = true;

        while (running) {

            showMenu();

            int choice = readInt("Enter choice: ");

            switch (choice) {

                case 1 -> addExpense(service);

                case 2 -> viewAllExpenses(service);

                case 3 -> viewByCategory(service);

                case 4 -> setBudget(service);

                case 5 -> viewSummary(service);

                case 6 -> saveReport(service);

                case 0 -> {
                    running = false;
                    System.out.println(
                            "Thank you for using Personal Expense Tracker.");
                }

                default ->
                        System.out.println(
                                "Invalid menu choice. Please try again.");
            }
        }
    }

    private static void showMenu() {

        System.out.println();
        System.out.println("========================================");
        System.out.println("       PERSONAL EXPENSE TRACKER");
        System.out.println("========================================");
        System.out.println("1. Add expense");
        System.out.println("2. View all expenses");
        System.out.println("3. View expenses by category");
        System.out.println("4. Set budget");
        System.out.println("5. View summary");
        System.out.println("6. Save report");
        System.out.println("0. Exit");
    }

    private static void addExpense(ExpenseService service) {

        System.out.println("\nADD EXPENSE");

        LocalDate date = readDate();

        String category = readCategory();

        int amount = readAmount();

        String paymentMode = readPaymentMode();

        String note = readNote();

        try {
            Expense expense = service.addExpense(
                    date,
                    category,
                    amount,
                    paymentMode,
                    note
            );

            System.out.println(
                    "Expense " + expense.getId() + " saved.");

        } catch (IOException | IllegalArgumentException e) {
            System.out.println(
                    "Could not save expense: "
                            + e.getMessage());
        }
    }

    private static void viewAllExpenses(
            ExpenseService service) {

        printExpenses(service.getExpenses());
    }

    private static void viewByCategory(
            ExpenseService service) {

        System.out.println("\nVIEW EXPENSES BY CATEGORY");

        for (int i = 0; i < CATEGORIES.length; i++) {
            System.out.println(
                    (i + 1) + ". " + CATEGORIES[i]);
        }

        System.out.println("0. Back");

        int choice = readInt("Choose category: ");

        if (choice == 0) {
            return;
        }

        if (choice < 1 || choice > CATEGORIES.length) {
            System.out.println("Invalid category.");
            return;
        }

        String category = CATEGORIES[choice - 1];

        printExpenses(
                service.filterByCategory(category));
    }

    private static void setBudget(
            ExpenseService service) {

        System.out.println("\nSET BUDGET");

        int budget = readIntInRange(
                "Enter budget in whole rupees: ",
                1,
                1_000_000
        );

        try {
            service.setBudget(budget);

            System.out.println(
                    "Budget saved successfully: "
                            + budget
                            + " rupees.");

        } catch (IOException | IllegalArgumentException e) {
            System.out.println(
                    "Could not save budget: "
                            + e.getMessage());
        }
    }

    private static void viewSummary(
            ExpenseService service) {

        System.out.println(
                service.showSummary());
    }

    private static void saveReport(
            ExpenseService service) {

        try {
            service.saveReport();

            System.out.println(
                    "Report saved successfully to data/report.txt.");

        } catch (IOException e) {
            System.out.println(
                    "Could not save report: "
                            + e.getMessage());
        }
    }

    private static void printExpenses(
            List<Expense> expenses) {

        if (expenses.isEmpty()) {
            System.out.println("No records found.");
            return;
        }

        System.out.println();

        System.out.printf(
                "%-4s %-12s %-10s %-10s %-10s %s%n",
                "ID", "Date", "Category",
                "Amount", "Payment", "Note"
        );

        System.out.println(
                "---------------------------------------------------------------------");

        for (Expense expense : expenses) {

            System.out.printf(
                    "%-4d %-12s %-10s %-10d %-10s %s%n",
                    expense.getId(),
                    expense.getDate(),
                    expense.getCategory(),
                    expense.getAmountRupees(),
                    expense.getPaymentMode(),
                    expense.getNote()
            );
        }
    }

    private static LocalDate readDate() {

        while (true) {

            String input = readNonBlank(
                    "Date in yyyy-MM-dd format: ");

            try {
                return LocalDate.parse(input);

            } catch (DateTimeParseException e) {
                System.out.println(
                        "Invalid date. Example: 2026-10-01");
            }
        }
    }

    private static String readCategory() {

        while (true) {

            System.out.println("Category:");

            for (int i = 0; i < CATEGORIES.length; i++) {
                System.out.println(
                        (i + 1) + ". " + CATEGORIES[i]);
            }

            int choice = readInt(
                    "Choose category: ");

            if (choice >= 1
                    && choice <= CATEGORIES.length) {

                return CATEGORIES[choice - 1];
            }

            System.out.println(
                    "Please choose 1, 2, 3 or 4.");
        }
    }

    private static int readAmount() {

        return readIntInRange(
                "Amount in whole rupees: ",
                1,
                1_000_000
        );
    }

    private static String readPaymentMode() {

        while (true) {

            System.out.println("Payment:");
            System.out.println("1. Cash");
            System.out.println("2. Online");

            int choice = readInt(
                    "Choose payment: ");

            if (choice == 1) {
                return "CASH";
            }

            if (choice == 2) {
                return "ONLINE";
            }

            System.out.println(
                    "Please choose 1 or 2.");
        }
    }

    private static String readNote() {

        while (true) {

            String note = readNonBlank(
                    "Short note: ");

            if (note.contains("|")) {
                System.out.println(
                        "Note cannot contain |.");
                continue;
            }

            if (note.contains("\n")
                    || note.contains("\r")) {
                System.out.println(
                        "Note cannot contain line breaks.");
                continue;
            }

            return note;
        }
    }

    private static String readNonBlank(
            String message) {

        while (true) {

            System.out.print(message);

            String input =
                    scanner.nextLine().trim();

            if (!input.isBlank()) {
                return input;
            }

            System.out.println(
                    "Input cannot be blank.");
        }
    }

    private static int readInt(
            String message) {

        while (true) {

            System.out.print(message);

            String input =
                    scanner.nextLine().trim();

            try {
                return Integer.parseInt(input);

            } catch (NumberFormatException e) {

                System.out.println(
                        "Please enter a whole number.");
            }
        }
    }

    private static int readIntInRange(
            String message,
            int minimum,
            int maximum) {

        while (true) {

            int value = readInt(message);

            if (value >= minimum
                    && value <= maximum) {

                return value;
            }

            System.out.println(
                    "Enter a value from "
                            + minimum
                            + " to "
                            + maximum
                            + ".");
        }
    }
}
