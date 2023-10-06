package com.TripBuddy.utilities;

import com.TripBuddy.Records.Sales;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

public class SalesReport {
    public SalesReport(ArrayList<Sales> sales, String absolutePath) {
        String csvFilePath = absolutePath + "/" + "salesReport.csv";
        try {
            FileWriter fileWriter = new FileWriter(csvFilePath);
            String headerLine = "Sale Type, Item Name, Total Sales, Profit\n";
            fileWriter.write(headerLine);
            for (Sales sale : sales) {
                String line = sale.sale_type() + "," + sale.item_name() + "," + sale.total_sales() + "," + sale.profit() + "\n";
                fileWriter.write(line);
            }
            fileWriter.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
