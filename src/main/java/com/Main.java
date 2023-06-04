package com;

import java.sql.*;
import java.util.Scanner;



public class Main {
    private static Connection conn;

    static void AccountCreate() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Account Holder Name: ");
        String Accholdname = sc.nextLine();
        System.out.println("Enter Your Mobile No: ");
        long Mobileno = sc.nextLong();
        System.out.println("Choose which Type of Account You Want");
        System.out.println("1.Savings 2.Current");
        int ch = sc.nextInt();
        String Acctyp = "";
        switch (ch) {
            case 1:
                Acctyp = "Savings";
                break;
            case 2:
                Acctyp = "Current";
                break;
        }
        System.out.println("Enter 4 Digit Pin:");
        int pin = sc.nextInt();
        System.out.println("Enter amount:");
        int amount = sc.nextInt();

        try {
            String insertQuery = "INSERT INTO accounts (name, mobile_no, account_type, pin, balance) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(insertQuery);
            pstmt.setString(1, Accholdname);
            pstmt.setLong(2, Mobileno);
            pstmt.setString(3, Acctyp);
            pstmt.setInt(4, pin);
            pstmt.setInt(5, amount);
            pstmt.executeUpdate();
            pstmt.close();

            System.out.println("Account created successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void ViewAccount() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Your Mobile No: ");
        long Mobileno = sc.nextLong();
        System.out.println("Enter 4 Digit Pin:");
        int pin = sc.nextInt();

        try {
            String selectQuery = "SELECT * FROM accounts WHERE mobile_no = ? AND pin = ?";
            PreparedStatement pstmt = conn.prepareStatement(selectQuery);
            pstmt.setLong(1, Mobileno);
            pstmt.setInt(2, pin);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int accountNumber = rs.getInt("account_number");
                String name = rs.getString("name");
                String accountType = rs.getString("account_type");

                System.out.println("Account Number: " + accountNumber);
                System.out.println("Account Holder Name: " + name);
                System.out.println("Account Type: " + accountType);
            } else {
                System.out.println("Account not found or invalid PIN.");
            }

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void WithdrawMoney() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Your Mobile No: ");
        long Mobileno = sc.nextLong();
        System.out.println("Enter 4 Digit Pin:");
        int pin = sc.nextInt();
        System.out.println("Enter Amount:");
        int amount = sc.nextInt();

        try {
            String updateQuery = "UPDATE accounts SET balance = balance - ? WHERE mobile_no = ? AND pin = ?";
            PreparedStatement pstmt = conn.prepareStatement(updateQuery);
            pstmt.setDouble(1, amount);
            pstmt.setLong(2, Mobileno);
            pstmt.setInt(3, pin);
            int rowsAffected = pstmt.executeUpdate();
            pstmt.close();

            if (rowsAffected == 0) {
                System.out.println("Account not found or invalid PIN.");
            } else {
                System.out.println("Amount withdrawn successfully.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void DepositMoney() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Your Mobile No: ");
        long Mobileno = sc.nextLong();
        System.out.println("Enter Amount:");
        int amount = sc.nextInt();

        try {
            String updateQuery = "UPDATE accounts SET balance = balance + ? WHERE mobile_no = ?";
            PreparedStatement pstmt = conn.prepareStatement(updateQuery);
            pstmt.setDouble(1, amount);
            pstmt.setLong(2, Mobileno);
            int rowsAffected = pstmt.executeUpdate();
            pstmt.close();

            if (rowsAffected == 0) {
                System.out.println("Account not found.");
            } else {
                System.out.println("Amount deposited successfully.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void ViewBalance() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Your Mobile No: ");
        long Mobileno = sc.nextLong();
        System.out.println("Enter 4 Digit Pin:");
        int pin = sc.nextInt();

        try {
            String selectQuery = "SELECT balance FROM accounts WHERE mobile_no = ? AND pin = ?";
            PreparedStatement pstmt = conn.prepareStatement(selectQuery);
            pstmt.setLong(1, Mobileno);
            pstmt.setInt(2, pin);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                double balance = rs.getDouble("balance");
                System.out.println("Account Balance: ₹" + balance);
            } else {
                System.out.println("Account not found or invalid PIN.");
            }

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank", "root", "admin");
            createTable(conn); // Create necessary table if it doesn't exist
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        while (true) {
            System.out.println("1.Create Account");
            System.out.println("2.View Account");
            System.out.println("3.Withdraw Money");
            System.out.println("4.Deposit Money");
            System.out.println("5.View Balance");
            System.out.println("6.Exit");
            System.out.println("Enter Your Choice");
            Scanner sc = new Scanner(System.in);
            int choice = sc.nextInt();
            switch (choice) {
                case 1:
                    AccountCreate();
                    break;
                case 2:
                    ViewAccount();
                    break;
                case 3:
                    WithdrawMoney();
                    break;
                case 4:
                    DepositMoney();
                    break;
                case 5:
                    ViewBalance();
                    break;
                case 6:
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    System.exit(0);
                    break;
                default:
                    System.out.println("Enter Valid Choice");
                    break;
            }
        }
    }

    private static void createTable(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        String accountTableQuery = "CREATE TABLE IF NOT EXISTS accounts (" +
                "account_number INT PRIMARY KEY AUTO_INCREMENT," +
                "name VARCHAR(100) NOT NULL," +
                "mobile_no BIGINT NOT NULL," +
                "account_type VARCHAR(10) NOT NULL," +
                "balance BIGINT(10) NOT NULL," +
                "pin INT NOT NULL" +
                ")";
        stmt.executeUpdate(accountTableQuery);
        stmt.close();
    }
}
