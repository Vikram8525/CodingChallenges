package com.chainsys.jfs.copy2;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class StockManagementMain implements StockManagementInterface {

    public static void main(String[] args) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        List<StockManagementInformation> items = new ArrayList<>();

        while (true) {
            System.out.println("\t\t" + " .....WELCOME TO STOCK MANAGEMENT SYSTEM.....");
            System.out.println("Are you an Employee or a Customer?");
            System.out.println("For Employee, press 1.");
            System.out.println("For Customer, press 2.");
            System.out.println("For Exiting, press 3.");
            System.out.println("Enter your choice: ");
            System.out.println("==================================");

            int userType = scanner.nextInt();
            scanner.nextLine(); 

            switch (userType) {
                case 1:
                    employeeMenu(scanner, items);
                    break;
                case 2:
                    customerMenu(items);
                    break;
                case 3:
                    System.out.println("Exiting the Stock Management System..");
                    System.out.println("Thanks for visiting..");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice! Please enter 1 for Employee or 2 for Customer.");
            }
        }
    }

    private static void employeeMenu(Scanner scanner, List<StockManagementInformation> items) throws SQLException {
        System.out.println("\t\t" +"..Welcome Employee..");
        System.out.println("Do you have an Employee Account (yes or no)");
        String hasAccount = scanner.nextLine().toLowerCase();

        if (hasAccount.equals("no")) {
            // Register new employee
            System.out.println("Creating Employee Account...");
            System.out.println("Enter your username: ");
            String username = scanner.nextLine();
            System.out.println("Enter your password: ");
            String password = scanner.nextLine();
            System.out.println("Are you a supervisor? (yes or no)");
            boolean isSupervisor = scanner.nextLine().equalsIgnoreCase("yes");
            if (isSupervisor) {
                System.out.println("Enter supervisor password: ");
                String supervisorPassword = scanner.nextLine();
                if (supervisorPassword.equals("Admin@123")) {
                    addEmployee(username, password, isSupervisor);
                    System.out.println("Employee Account Created Successfully!");
                } else {
                    isSupervisor = false;
                    addEmployee(username, password, isSupervisor);
                    System.out.println("Incorrect supervisor password. Assigned as normal employee.");
                }
            } else {
                addEmployee(username, password, isSupervisor); 
                System.out.println("Employee Account Created Successfully!");
            }
        } else if (hasAccount.equals("yes")) {
            System.out.println("Enter your username: ");
            String username = scanner.nextLine();
            System.out.println("Enter your password: ");
            String password = scanner.nextLine();

            if (adminLogin(username, password)) {
                System.out.println("Login Successful!");
                boolean isSupervisor = isAdminSupervisor(username, password);
                employeeMenuOptions(isSupervisor, scanner, items);
            } else {
                System.out.println("Incorrect username or password. Please try again.");
            }
        } else {
            System.out.println("Invalid choice! Please enter 'yes' or 'no'.");
        }
    }

    private static void employeeMenuOptions(boolean isSupervisor, Scanner scanner, List<StockManagementInformation> items) throws SQLException {
        boolean exitEmployee = false;
        while (!exitEmployee) {
            System.out.println("\nEmployee Menu:");
            if (isSupervisor) {
            	System.out.println("\t\t" +"..Welcome Supervisor..");
            }
            else {
            	System.out.println("\t\t" +"..Welcome Employee..");
            }
            
            System.out.println("1. View Stock");
            if (isSupervisor) {
                System.out.println("2. Add Stock");
                System.out.println("3. Delete Stock");
            }
            System.out.println("4. Check Stock Freshness");
            System.out.println("5. Check Stock Availability");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    Connectivity.displayStocks();
                    break;
                case 2:
                    if (isSupervisor) {
                        addStock(items);
                    } else {
                        System.out.println("You are not authorized to add stock.");
                    }
                    break;
                case 3:
                    if (isSupervisor) {
                        deleteStock(items);
                    } else {
                        System.out.println("You are not authorized to delete stock.");
                    }
                    break;
                case 4:
                    Connectivity.checkStockAgeAndAvailability(items);
                    break;
                case 5:
                    stockAvailability(items);
                    break;
                case 6:
                    exitEmployee = true;
                    System.out.println("Exiting Employee Menu.");
                    break;
                default:
                    System.out.println("Invalid choice! Please enter a number between 1 and 6.");
            }
        }
    }

    private static void addStock(List<StockManagementInformation> items) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter item name: ");
        String itemName = scanner.nextLine();
        StockManagementInformation item = new StockManagementInformation(itemName);
        items.add(item);
        try {
            LocalDate startDate = item.getStartDate();
            java.util.Date utilStartDate = java.sql.Date.valueOf(startDate);
            Connectivity.addStock(0, itemName, item.getItemQuantity(), utilStartDate);
            System.out.println("Item added successfully!");
        } catch (SQLException e) {
            System.out.println("Failed to add item to database: " + e.getMessage());
        }
    }

    private static void deleteStock(List<StockManagementInformation> items) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the name of the item to delete: ");
        String itemName = scanner.nextLine();

        boolean success = Connectivity.deleteStock(itemName);
        if (success) {
            System.out.println("Item '" + itemName + "' deleted successfully.");
      
            items.removeIf(item -> item.getItemName().equalsIgnoreCase(itemName));
        } else {
            System.out.println("Failed to delete item '" + itemName + "'.");
        }
    }


    private static void customerMenu(List<StockManagementInformation> items) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        boolean exitCustomer = false;
        while (!exitCustomer) {
            System.out.println("\nCustomer Menu:");
            System.out.println("1. Check Stock Details");
            System.out.println("2. Check Product Freshness");
            System.out.println("3. Buy Your Product");
            System.out.println("4. Exit!");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    Connectivity.displayStocks();
                    break;
                case 2:
                    Connectivity.checkStockAgeAndAvailability(items);
                    break;
                case 3:
                    Connectivity.buyStock();
                    break;
                case 4:
                    exitCustomer = true;
                    System.out.println("Exiting Customer Menu.");
                    break;
                default:
                    System.out.println("Invalid choice! Please enter a number between 1 and 4.");
            }
        }
    }

    private static void addEmployee(String username, String password, boolean isSupervisor) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = Connectivity.getConnection();
            String insertQuery = "INSERT INTO employees (username, password, is_supervisor) VALUES (?, ?, ?)";
            statement = connection.prepareStatement(insertQuery);
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setBoolean(3, isSupervisor);
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Employee added successfully!");
            }
        } finally {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    private static boolean adminLogin(String name, String passWord) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        boolean loggedIn = false;
        try {
            connection = Connectivity.getConnection();
            String loginQuery = "SELECT * FROM employees WHERE username = ? AND password = ?";
            statement = connection.prepareStatement(loginQuery);
            statement.setString(1, name);
            statement.setString(2, passWord);
            resultSet = statement.executeQuery();
            loggedIn = resultSet.next();
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
        return loggedIn;
    }

    private static boolean isAdminSupervisor(String username, String password) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        boolean isSupervisor = false;
        try {
            connection = Connectivity.getConnection();
            String supervisorQuery = "SELECT is_supervisor FROM employees WHERE username = ? AND password = ?";
            statement = connection.prepareStatement(supervisorQuery);
            statement.setString(1, username);
            statement.setString(2, password);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int supervisorValue = resultSet.getInt("is_supervisor");
                isSupervisor = (supervisorValue == 1);
            }
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
        return isSupervisor;
    }
    private static void stockAvailability(List<StockManagementInformation> items) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println("Select an option:");
            System.out.println("1. Available Enough");
            System.out.println("2. Will Depleted");
            System.out.println("3. Required Item");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    checkAvailability("available_enough");
                    break;
                case 2:
                    checkAvailability("Will_Depleted");
                    break;
                case 3:
                    checkAvailability("Required_item");
                    break;
                case 4:
                    System.out.println("Exiting stock availability check.");
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice! Please enter a number between 1 and 4.");
            }
        }
    }

    private static void checkAvailability(String status) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = Connectivity.getConnection();
            String query = "SELECT item_name, item_quantity FROM stock";
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();

            System.out.println("Stock Availability:");
            while (resultSet.next()) {
                String itemName = resultSet.getString("item_name");
                int quantity = resultSet.getInt("item_quantity");

                switch (status) {
                    case "available_enough":
                        if (quantity >= 50) {
                            System.out.println(itemName + ": There is enough stock available.");
                        }
                        break;
                    case "Will_Depleted":
                        if (quantity >= 10 && quantity < 50) {
                            System.out.println(itemName + ": Stock will be depleted soon.");
                        }
                        break;
                    case "Required_item":
                        if (quantity > 0 && quantity < 10) {
                            System.out.println(itemName + ": Final few stock! Add it ASAP.");
                        }
                        break;
                }
            }
        } finally {
            // Closing resources
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

}
