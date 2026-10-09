# Project Explanation - Personal Expense Tracker

## 1. Project goal

A single-user console application for recording expenses, viewing history, setting a budget, calculating category totals, and saving a report.

## 2. Package architecture

### controller
`Main.java`

The controller is responsible for:
- Starting the application.
- Showing the menu.
- Reading console input.
- Validating user input.
- Calling service methods.
- Printing results.

### model.expense
`Expense.java`, `CashExpense.java`, `OnlineExpense.java`

These classes represent expense data.

`Expense` is abstract and contains:
- id
- date
- category
- amountRupees
- note

`CashExpense` and `OnlineExpense` inherit from `Expense` and override `getPaymentMode()`.

### model.budget
`Budget.java`

Stores the budget and validates the allowed range.

### service
`ExpenseService.java`

Contains business logic:
- Add expense.
- Filter by category.
- Calculate category totals.
- Calculate total spending.
- Count distinct categories.
- Set budget.
- Show summary.
- Save report.
- Generate the next expense ID.

### repository.file
`FileManager.java`

Handles persistence:
- Load expenses.
- Save expenses.
- Load budget.
- Save budget.
- Save report.

## 3. Why repository is called a repository

The repository layer is responsible for storing and retrieving application data. In this Core Java version, the storage is text files rather than a database.

## 4. Important OOP examples

### Encapsulation

Expense fields are private and accessed through getters.

### Inheritance

```java
CashExpense extends Expense
OnlineExpense extends Expense
```

### Abstraction

```java
abstract class Expense
abstract String getPaymentMode();
```

### Polymorphism

```java
Expense expense = new CashExpense(...);
expense.getPaymentMode(); // CASH
```

or:

```java
Expense expense = new OnlineExpense(...);
expense.getPaymentMode(); // ONLINE
```

### Composition

`ExpenseService` contains an expense list, a `Budget` object and a `FileManager` object.

## 5. Collections

### Array

```java
String[] categories = {"Food", "Travel", "Study", "Other"};
```

### ArrayList

```java
ArrayList<Expense> expenses;
```

Stores all expense objects.

### HashSet

```java
HashSet<String> usedCategories;
```

Counts each category only once.

## 6. File format

`data/expenses.txt`:

```text
id|date|category|amountRupees|paymentMode|note
```

Example:

```text
1|2026-10-01|Food|300|CASH|Lunch
2|2026-10-01|Travel|100|ONLINE|Bus
3|2026-10-02|Study|200|CASH|Notebook
```

`data/budget.txt` contains one integer:

```text
2000
```

`data/report.txt` contains a readable summary and expense history.

## 7. Recommended demonstration

1. Add Food/Cash expense.
2. Add Travel/Online expense.
3. Add Study/Cash expense.
4. View all expenses.
5. Filter Food.
6. View summary and explain `String[4]` + `long[4]`.
7. Change budget to below total spending.
8. Show "Over budget by".
9. Save report.
10. Restart and show persistence.
