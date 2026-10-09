# Personal Expense Tracker - Layered Core Java Project

## Architecture

This project keeps the mentor's seven required classes but organizes them into a layered package structure:

```text
src/
└── com/
    └── personalexpensetracker/
        ├── controller/
        │   └── Main.java
        │
        ├── model/
        │   ├── expense/
        │   │   ├── Expense.java
        │   │   ├── CashExpense.java
        │   │   └── OnlineExpense.java
        │   │
        │   └── budget/
        │       └── Budget.java
        │
        ├── service/
        │   └── ExpenseService.java
        │
        └── repository/
            └── file/
                └── FileManager.java
```

## Layer purpose

- `controller`: console input, menu and output.
- `model`: application data and OOP model classes.
- `service`: business logic and calculations.
- `repository.file`: text-file persistence.

## Data files

The program creates:

```text
data/expenses.txt
data/budget.txt
data/report.txt
```

## Compile

From the project root:

```bash
javac -d out src/com/personalexpensetracker/controller/Main.java src/com/personalexpensetracker/model/expense/*.java src/com/personalexpensetracker/model/budget/*.java src/com/personalexpensetracker/service/*.java src/com/personalexpensetracker/repository/file/*.java
```

## Run

```bash
java -cp out com.personalexpensetracker.controller.Main
```

## Core Java concepts demonstrated

- Classes and objects
- Constructors
- Encapsulation
- Inheritance
- Abstraction
- Polymorphism
- Packages and imports
- Arrays
- ArrayList
- HashSet
- Exception handling
- LocalDate
- File handling with BufferedReader/BufferedWriter
- try-with-resources
- Basic validation
- Text-file persistence

No database, Spring, JDBC, GUI, or advanced frameworks are used.
