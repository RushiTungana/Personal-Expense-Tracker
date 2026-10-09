# Personal-Expense-Tracker
# 💰 Personal Expense Tracker

A console-based **Personal Expense Tracker** developed using Core Java as part of the Core Java Module 1 project. The application helps users record daily expenses, manage a budget, view spending summaries, and generate reports.

## 📌 Project Overview

Managing daily expenses manually can make it difficult to track spending and stay within a budget. This project provides a simple solution by allowing users to record expenses, organize them by category, and monitor their overall spending.

## ✨ Features

- **Expense Entry:** Add expenses with date, category, amount, payment mode, and note.
- **Multiple Payment Modes:** Supports cash and online expenses.
- **Expense History:** View recorded expenses and filter them by category.
- **Budget Management:** Set and save a personal budget.
- **Spending Summary:** View total expenses, category-wise totals, and unique categories.
- **Budget Monitoring:** Identify when total spending exceeds the budget.
- **Report Generation:** Generate and save spending reports.
- **File Persistence:** Save and reload expense and budget data using text files.
- **Input Validation:** Validate dates, amounts, and other user inputs.

## 🛠️ Technologies Used

- Java
- Object-Oriented Programming (OOP)
- ArrayList and HashSet
- LocalDate API
- Exception Handling
- File Handling
- Text-file storage

## 🏗️ Project Structure

```text
PersonalExpenseTracker/
├── src/
│   └── com/
│       └── personalexpensetracker/
│           ├── controller/
│           │   └── Main.java
│           ├── model/
│           │   ├── expense/
│           │   │   ├── Expense.java
│           │   │   ├── CashExpense.java
│           │   │   └── OnlineExpense.java
│           │   └── budget/
│           │       └── Budget.java
│           ├── service/
│           │   └── ExpenseService.java
│           └── repository/
│               └── file/
│                   └── FileManager.java
├── data/
│   └── .gitkeep
├── README.md
└── PROJECT_DOCUMENTATION.md
```

## 🧩 Architecture

The project follows a layered architecture:

1. **Controller Layer:** `Main.java` manages the console menu and user interaction.
2. **Service Layer:** `ExpenseService.java` handles expense operations, budget management, calculations, and reports.
3. **Repository Layer:** `FileManager.java` manages reading and writing data files.
4. **Model Layer:** Represents expenses and budgets using dedicated Java classes.

## 📂 Data Files

The application uses text files to store data:

- `expenses.txt` — stores expense records.
- `budget.txt` — stores the user's budget.
- `report.txt` — stores the generated spending report.

These files are created or updated by the application when the corresponding operations are performed.

## ▶️ How to Run

### Prerequisites

- Java Development Kit (JDK) installed.
- A terminal or command prompt.

Check your Java installation:

```bash
java -version
javac -version
```

### Compile the Project

Run the following commands from the project root directory:

```bash
javac -d out src/com/personalexpensetracker/model/expense/*.java src/com/personalexpensetracker/model/budget/*.java src/com/personalexpensetracker/repository/file/*.java src/com/personalexpensetracker/service/*.java src/com/personalexpensetracker/controller/*.java
```

### Run the Application

```bash
java -cp out com.personalexpensetracker.controller.Main
```

## 💡 Example

Suppose the user sets a budget of ₹15,000 and records expenses totaling ₹21,190.

The spending summary will show:

```text
SPENDING SUMMARY
Expense count: 4
Total spending: 21190 rupees
Budget: 15000 rupees
Over budget by: 6190 rupees
```

*The values above are illustrative sample output.*

## 📚 Core Java Concepts Demonstrated

- **Encapsulation:** Private fields and getter methods.
- **Inheritance:** `CashExpense` and `OnlineExpense` extend `Expense`.
- **Abstraction:** The abstract `Expense` class defines common expense behavior.
- **Polymorphism:** Subclasses override the payment-mode method.
- **Collections:** ArrayList for expense records and HashSet for unique categories.
- **Date Handling:** LocalDate for expense dates.
- **Exception Handling:** Handles invalid input and file-related errors.
- **File Handling:** Persists expense, budget, and report data.

## 🚀 Future Enhancements

- Monthly and yearly spending charts.
- Edit and delete expense functionality.
- Search expenses by date range.
- Graphical user interface.
- Database integration.

## 👨‍💻 Author

**Name:** Your Name

**Project:** Personal Expense Tracker

**Module:** Core Java Module 1

---

⭐ *A simple Core Java project demonstrating object-oriented programming, collections, input validation, and file persistence.*
