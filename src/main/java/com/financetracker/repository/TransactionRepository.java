package com.financetracker.repository;

import com.financetracker.model.Transaction;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionRepository {
    private static final String FILE_NAME = "transactions.json";
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .setPrettyPrinting()
            .create();

    public static void saveTransactions(List<Transaction> transactions) {
        try (FileWriter writer = new FileWriter(FILE_NAME)) {
            gson.toJson(transactions, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Transaction> loadTransactions() {
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            saveTransactions(new ArrayList<>());
            return new ArrayList<>();
        }

        try (FileReader reader = new FileReader(file)) {
            Type listType = new TypeToken<ArrayList<Transaction>>() {}.getType();
            List<Transaction> data = gson.fromJson(reader, listType);

            if (data == null) data = new ArrayList<>();

            int maxId = data.stream().mapToInt(Transaction::getId).max().orElse(0);
            Transaction.resetIdCounter(maxId);

            return data;
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    public static void exportTransactionsToCSV(List<Transaction> transactions, String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {

            writer.append("ID,Title,Category,Amount,Date,Type\n");

            for (Transaction tx : transactions) {
                writer.append(String.valueOf(tx.getId())).append(",");
                writer.append(escape(tx.getTitle())).append(",");
                writer.append(escape(tx.getCategory())).append(",");
                writer.append(String.valueOf(tx.getAmount())).append(",");
                writer.append(String.valueOf(tx.getDate())).append(",");
                writer.append(String.valueOf(tx.getType())).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String escape(String value) {
        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
