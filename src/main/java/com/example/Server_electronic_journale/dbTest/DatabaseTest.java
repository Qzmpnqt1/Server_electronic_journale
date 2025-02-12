package com.example.Server_electronic_journale.dbTest;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseTest {
    public static void main(String[] args) {
        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/electronic_journal", "root", "Karnegi1.");
            if (connection != null) {
                System.out.println("Соединение успешно!");
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
