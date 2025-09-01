# Finance Tracker

A personal finance desktop application built with **Java 17**, **JavaFX**, and **Maven**.  
The app helps users manage transactions, visualize expenses and income, and export data for reporting.

---

## ✨ Features

- **Transaction Management**
    - Add new transactions (title, category, amount, date, type: income/expense)
    - Delete transactions with validation
    - Auto-generated unique IDs for each transaction

- **Data Persistence**
    - Save transactions in `transactions.json` (using Gson)
    - Load data automatically on startup
    - Export all transactions to **CSV** (Excel/Google Sheets compatible)

- **Data Visualization**
    - **Pie Chart**: Expenses grouped by category
    - **Bar Chart**: Income vs Expenses by month

- **User Experience**
    - Clean, modular architecture (`model`, `controller`, `ui`, `repository`)
    - Dialogs for input validation and error handling
    - File chooser for CSV export

---

## 🛠️ Tech Stack

- **Java 17**
- **JavaFX 21**
- **Maven**
- **Gson** (JSON persistence)
- **JavaFX Controls**