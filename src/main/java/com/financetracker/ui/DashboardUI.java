package com.financetracker.ui;

import com.financetracker.controller.AddTransactionDialog;
import com.financetracker.repository.TransactionRepository;
import com.financetracker.model.Transaction;
import com.financetracker.model.TransactionType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class DashboardUI {
    private final ObservableList<Transaction> transactions =
            FXCollections.observableArrayList(TransactionRepository.loadTransactions());
    private final TabPane root = new TabPane();

    public DashboardUI() {
        TableView<Transaction> table = new TableView<>(transactions);
        setupTable(table);

        Button addBtn = new Button("Add Transaction");
        addBtn.setOnAction(e -> {
            AddTransactionDialog dialog = new AddTransactionDialog();
            dialog.showAndWait().ifPresent(transaction -> {
                transactions.add(transaction);
                TransactionRepository.saveTransactions(transactions);
            });
        });

        Button deleteBtn = new Button("Delete Selected");
        deleteBtn.setOnAction(e -> {
            Transaction sel = table.getSelectionModel().getSelectedItem();
            if (sel != null) {
                transactions.remove(sel);
                TransactionRepository.saveTransactions(transactions);
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "No transaction selected!", ButtonType.OK);
                alert.setHeaderText("Warning");
                alert.showAndWait();
            }
        });

        Button exportBtn = new Button("Export to CSV");
        exportBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Transactions as CSV");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            fileChooser.setInitialFileName("transactions.csv");

            Stage stage = (Stage) root.getScene().getWindow();
            var file = fileChooser.showSaveDialog(stage);

            if (file != null) {
                TransactionRepository.exportTransactionsToCSV(transactions, file.getAbsolutePath());
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Export successful!", ButtonType.OK);
                alert.setHeaderText(null);
                alert.showAndWait();
            }
        });

        HBox actions = new HBox(10, addBtn, deleteBtn, exportBtn);
        VBox transactionsLayout = new VBox(10, table, actions);
        Tab transactionsTab = new Tab("Transactions", transactionsLayout);
        transactionsTab.setClosable(false);

        // === Charts tab ===
        PieChart pieChart = new PieChart();
        pieChart.setTitle("Expenses by Category");
        Button refreshPieBtn = new Button("Refresh Pie");
        refreshPieBtn.setOnAction(e -> updatePieChart(pieChart));

        BarChart<String, Number> barChart = createIncomeExpenseChart();
        barChart.setTitle("Income vs Expenses per Month");
        Button refreshBarBtn = new Button("Refresh Bar");
        refreshBarBtn.setOnAction(e -> updateIncomeExpenseChart(barChart));

        VBox chartsLayout = new VBox(15, pieChart, refreshPieBtn, barChart, refreshBarBtn);
        Tab chartsTab = new Tab("Charts", chartsLayout);
        chartsTab.setClosable(false);

        root.getTabs().addAll(transactionsTab, chartsTab);
    }

    public TabPane getRoot() {
        return root;
    }

    private void setupTable(TableView<Transaction> table) {
        TableColumn<Transaction, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Transaction, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<Transaction, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<Transaction, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

        TableColumn<Transaction, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<Transaction, TransactionType> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));

        table.getColumns().addAll(idCol, titleCol, categoryCol, amountCol, dateCol, typeCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    }

    private void updatePieChart(PieChart pieChart) {
        Map<String, Double> totals = new HashMap<>();
        for (Transaction tx : transactions) {
            if (tx.getType() == TransactionType.EXPENSE) {
                totals.put(tx.getCategory(),
                        totals.getOrDefault(tx.getCategory(), 0.0) + tx.getAmount());
            }
        }

        pieChart.setData(FXCollections.observableArrayList(
                totals.entrySet().stream()
                        .map(e -> new PieChart.Data(e.getKey(), e.getValue()))
                        .toList()
        ));
    }

    private BarChart<String, Number> createIncomeExpenseChart() {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Month");

        xAxis.setCategories(FXCollections.observableArrayList(
                "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        ));

        xAxis.setTickLabelRotation(0);

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Amount");

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setCategoryGap(10);
        chart.setBarGap(3);
        chart.setTitle("Income vs Expenses per Month");

        return chart;
    }

    private void updateIncomeExpenseChart(BarChart<String, Number> chart) {
        XYChart.Series<String, Number> incomeSeries = new XYChart.Series<>();
        incomeSeries.setName("Income");
        XYChart.Series<String, Number> expenseSeries = new XYChart.Series<>();
        expenseSeries.setName("Expenses");

        Map<Integer, Double> incomeTotals = new HashMap<>();
        Map<Integer, Double> expenseTotals = new HashMap<>();

        for (Transaction tx : transactions) {
            int month = tx.getDate().getMonthValue(); // 1–12
            if (tx.getType() == TransactionType.INCOME) {
                incomeTotals.put(month, incomeTotals.getOrDefault(month, 0.0) + tx.getAmount());
            } else {
                expenseTotals.put(month, expenseTotals.getOrDefault(month, 0.0) + tx.getAmount());
            }
        }

        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        for (int m = 1; m <= 12; m++) {
            double inc = incomeTotals.getOrDefault(m, 0.0);
            double exp = expenseTotals.getOrDefault(m, 0.0);
            incomeSeries.getData().add(new XYChart.Data<>(months[m - 1], inc));
            expenseSeries.getData().add(new XYChart.Data<>(months[m - 1], exp));
        }

        chart.getData().setAll(incomeSeries, expenseSeries);
    }
}
