package com.svashishtha.csvparser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang.StringUtils;

public class CsvParser {

    public static void main(String[] args) throws IOException {
        String filePath = "/Users/shrikant/Google Drive/Money Transfer/Transactions_15_10_2012_2.csv";
        Reader in = new BufferedReader(new FileReader(new File(filePath)));
        CSVParser parser = new CSVParser(in, CSVFormat.DEFAULT);
        int count = 0;
        Map<String, Double> expenses = new HashMap<String, Double>();
        for (CSVRecord record : parser) {
            if (count == 0) {
                count++;
                continue;
            }
            String expenseName = record.get(3);
            String expenseStr = record.get(5);
            double expense = 0;
            if (StringUtils.isNotEmpty(expenseStr))
                expense = Double.parseDouble(expenseStr);
            List<String> list = Arrays.asList(expenseName.split(" "));
            String expenseType = list.get(0);
            Double existingExpense = expenses.get(expenseType);
            if (existingExpense == null) {
                expenses.put(expenseType, new Double(expense));
            } else {
                expenses.put(expenseType, (existingExpense + expense));
            }
        }
        Iterator<String> expensesKeys = expenses.keySet().iterator();
        while (expensesKeys.hasNext()) {
            String expenseType = expensesKeys.next();
            Double expense = expenses.get(expenseType);
            if (expense.doubleValue() != 0) {
                System.out.println(expenseType + "," + expense);
            }
        }
    }
}
