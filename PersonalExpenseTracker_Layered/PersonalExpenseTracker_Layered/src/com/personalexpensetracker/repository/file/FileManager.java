package com.personalexpensetracker.repository.file;

import com.personalexpensetracker.model.expense.CashExpense;
import com.personalexpensetracker.model.expense.Expense;
import com.personalexpensetracker.model.expense.OnlineExpense;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Repository/file layer.
 * Reads and writes expenses, budget and report text files.
 */
public class FileManager {

    private static final Path DATA_DIR = Path.of("data");
    private static final Path EXPENSES_FILE = DATA_DIR.resolve("expenses.txt");
    private static final Path BUDGET_FILE = DATA_DIR.resolve("budget.txt");
    private static final Path REPORT_FILE = DATA_DIR.resolve("report.txt");

    private static final int DEFAULT_BUDGET = 2000;
    private static final int MAX_AMOUNT = 1_000_000;

    private static final String[] CATEGORIES = {
            "Food", "Travel", "Study", "Other"
    };

    public FileManager() throws IOException {
        Files.createDirectories(DATA_DIR);
    }

    public List<Expense> loadExpenses() throws IOException {
        List<Expense> loaded = new ArrayList<>();

        if (!Files.exists(EXPENSES_FILE)) {
            return loaded;
        }

        Set<Integer> ids = new HashSet<>();
        int lineNumber = 0;

        try (BufferedReader reader = Files.newBufferedReader(
                EXPENSES_FILE, StandardCharsets.UTF_8)) {

            String line;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                if (line.isBlank()) {
                    throw fileError(lineNumber, "blank record");
                }

                String[] fields = line.split("\\|", -1);

                if (fields.length != 6) {
                    throw fileError(lineNumber,
                            "expected 6 fields separated by |");
                }

                int id = parsePositiveInt(fields[0], lineNumber, "id");

                if (!ids.add(id)) {
                    throw fileError(lineNumber,
                            "duplicate expense ID: " + id);
                }

                LocalDate date;
                try {
                    date = LocalDate.parse(fields[1]);
                } catch (DateTimeParseException e) {
                    throw fileError(lineNumber,
                            "invalid date: " + fields[1]);
                }

                String category = fields[2];
                if (!isValidCategory(category)) {
                    throw fileError(lineNumber,
                            "invalid category: " + category);
                }

                int amount = parsePositiveInt(
                        fields[3], lineNumber, "amount");

                if (amount > MAX_AMOUNT) {
                    throw fileError(lineNumber,
                            "amount must be <= 1000000");
                }

                String paymentMode = fields[4];
                if (!paymentMode.equals("CASH")
                        && !paymentMode.equals("ONLINE")) {
                    throw fileError(lineNumber,
                            "payment must be CASH or ONLINE");
                }

                String note = fields[5];
                validateTextField(note, lineNumber, "note");

                Expense expense;

                if (paymentMode.equals("CASH")) {
                    expense = new CashExpense(
                            id, date, category, amount, note);
                } else {
                    expense = new OnlineExpense(
                            id, date, category, amount, note);
                }

                loaded.add(expense);
            }
        }

        return loaded;
    }

    public int loadBudget() throws IOException {
        if (!Files.exists(BUDGET_FILE)) {
            return DEFAULT_BUDGET;
        }

        List<String> lines = Files.readAllLines(
                BUDGET_FILE, StandardCharsets.UTF_8);

        if (lines.size() != 1) {
            throw new IOException(
                    "Invalid data/budget.txt: expected exactly one line.");
        }

        int budget = parsePositiveInt(
                lines.get(0), 1, "budget");

        if (budget > MAX_AMOUNT) {
            throw new IOException(
                    "Invalid data/budget.txt: budget must be <= 1000000.");
        }

        return budget;
    }

    public void saveExpenses(List<Expense> expenses) throws IOException {
        List<String> lines = new ArrayList<>();

        for (Expense expense : expenses) {
            validateExpenseForSaving(expense);

            lines.add(
                    expense.getId() + "|" +
                    expense.getDate() + "|" +
                    expense.getCategory() + "|" +
                    expense.getAmountRupees() + "|" +
                    expense.getPaymentMode() + "|" +
                    expense.getNote()
            );
        }

        atomicWrite(EXPENSES_FILE, lines);
    }

    public void saveBudget(int budget) throws IOException {
        if (budget < 1 || budget > MAX_AMOUNT) {
            throw new IOException(
                    "Budget must be between 1 and 1000000 rupees.");
        }

        atomicWrite(BUDGET_FILE, List.of(String.valueOf(budget)));
    }

    public void saveReport(String report) throws IOException {
        if (report == null) {
            throw new IOException("Report cannot be null.");
        }

        atomicWrite(REPORT_FILE, List.of(report.split("\\R", -1)));
    }

    private void validateExpenseForSaving(Expense expense)
            throws IOException {

        if (expense == null) {
            throw new IOException("Expense cannot be null.");
        }

        if (expense.getId() < 1) {
            throw new IOException("Expense ID must be positive.");
        }

        if (expense.getDate() == null) {
            throw new IOException("Expense date cannot be null.");
        }

        if (!isValidCategory(expense.getCategory())) {
            throw new IOException("Invalid expense category.");
        }

        if (expense.getAmountRupees() < 1
                || expense.getAmountRupees() > MAX_AMOUNT) {
            throw new IOException("Invalid expense amount.");
        }

        if (!expense.getPaymentMode().equals("CASH")
                && !expense.getPaymentMode().equals("ONLINE")) {
            throw new IOException("Invalid payment mode.");
        }

        validateTextField(expense.getNote(), 0, "note");
    }

    private void validateTextField(String value, int lineNumber,
                                   String fieldName) throws IOException {
        if (value == null || value.isBlank()) {
            throw fileError(lineNumber,
                    fieldName + " cannot be blank");
        }

        if (value.contains("|") || value.contains("\n")
                || value.contains("\r")) {
            throw fileError(lineNumber,
                    fieldName + " cannot contain | or line breaks");
        }
    }

    private int parsePositiveInt(String value, int lineNumber,
                                 String fieldName) throws IOException {
        try {
            int number = Integer.parseInt(value);

            if (number < 1) {
                throw fileError(lineNumber,
                        fieldName + " must be positive");
            }

            return number;
        } catch (NumberFormatException e) {
            throw fileError(lineNumber,
                    fieldName + " must be a whole number");
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

    private IOException fileError(int lineNumber, String message) {
        if (lineNumber > 0) {
            return new IOException(
                    "Invalid " + EXPENSES_FILE +
                    " at line " + lineNumber + ": " + message);
        }
        return new IOException(message);
    }

    private void atomicWrite(Path target, List<String> lines)
            throws IOException {

        Path temp = target.resolveSibling(target.getFileName() + ".tmp");

        try {
            try (BufferedWriter writer = Files.newBufferedWriter(
                    temp,
                    StandardCharsets.UTF_8)) {

                for (int i = 0; i < lines.size(); i++) {
                    writer.write(lines.get(i));

                    if (i < lines.size() - 1) {
                        writer.newLine();
                    }
                }
            }

            try {
                Files.move(
                        temp,
                        target,
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE
                );
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(
                        temp,
                        target,
                        StandardCopyOption.REPLACE_EXISTING
                );
            }

        } catch (IOException e) {
            try {
                Files.deleteIfExists(temp);
            } catch (IOException ignored) {
                // Keep original exception as the useful error.
            }
            throw e;
        }
    }
}
