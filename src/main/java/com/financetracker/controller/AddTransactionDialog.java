package com.financetracker.controller;

import com.financetracker.model.Transaction;
import com.financetracker.model.TransactionType;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;

public class AddTransactionDialog extends Dialog<Transaction> {
    private final TextField titleField = new TextField();
    private final ChoiceBox<String> categoryChoiceBox = new ChoiceBox<>();
    private final TextField amountField = new TextField();
    private final DatePicker datePicker = new DatePicker(LocalDate.now());
    private final ChoiceBox<TransactionType> typeChoiceBox = new ChoiceBox<>();

    public AddTransactionDialog() {
        setTitle("Add Transaction");
        setHeaderText("Enter transaction details");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 10, 10, 10));

        categoryChoiceBox.getItems().addAll("Food", "Transport", "Entertainment", "Shopping", "Bills", "Salary", "Others");
        categoryChoiceBox.setValue("Food");

        typeChoiceBox.getItems().addAll(TransactionType.INCOME, TransactionType.EXPENSE);
        typeChoiceBox.setValue(TransactionType.EXPENSE);

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Category:"), 0, 1);
        grid.add(categoryChoiceBox, 1, 1);
        grid.add(new Label("Amount:"), 0, 2);
        grid.add(amountField, 1, 2);
        grid.add(new Label("Date:"), 0, 3);
        grid.add(datePicker, 1, 3);
        grid.add(new Label("Type:"), 0, 4);
        grid.add(typeChoiceBox, 1, 4);

        getDialogPane().setContent(grid);
        titleField.requestFocus();

        setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                String title = titleField.getText();
                String category = categoryChoiceBox.getValue();

                double amount;
                try {
                    amount = Double.parseDouble(amountField.getText());
                } catch (NumberFormatException e) {
                    showAlert("Amount must be a valid number!");
                    return null;
                }
                LocalDate date = datePicker.getValue();
                TransactionType type = typeChoiceBox.getValue();

                if (title.isEmpty() || category == null || date == null || type == null) {
                    showAlert("All fields are mandatory!");
                    return null;
                }

                return new Transaction(title, category, amount, date, type);
            }
            return null;
        });
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Input Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}