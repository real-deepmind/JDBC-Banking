import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;


public class Panel {

    public static void main(String[] args) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        Connection connection = getConnection();

        System.out.println("Connected to database successfully!");

        boolean exit = false;
        while (!exit) {
            System.out.println("--- Banking Menu ---");
            System.out.println("1. Create New Account");
            System.out.println("2. Deposit Money");
            System.out.println("3. Withdraw Money");
            System.out.println("4. Transfer Money");
            System.out.println("5. Delete Account");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    Account accountToCreate = new Account();
                    System.out.print("Enter Customer ID: ");
                    accountToCreate.setCustomerId(scanner.nextInt());
                    scanner.nextLine();
                    System.out.print("Enter Account Number: ");
                    accountToCreate.setAccountNumber(scanner.nextLine());
                    createAccount(connection, accountToCreate);
                    break;
                case 2:
                    Account accountToDeposit = new Account();
                    System.out.print("Enter Account Number: ");
                    accountToDeposit.setAccountNumber(scanner.nextLine());
                    System.out.print("Enter Deposit Amount: ");
                    accountToDeposit.setBalance(scanner.nextBigDecimal());
                    scanner.nextLine();
                    deposit(connection, accountToDeposit);
                    break;
                case 3:
                    Account accountToWithdraw = new Account();
                    System.out.print("Enter Account Number: ");
                    accountToWithdraw.setAccountNumber(scanner.nextLine());
                    System.out.print("Enter Withdrawal Amount: ");
                    accountToWithdraw.setBalance(scanner.nextBigDecimal());
                    scanner.nextLine();
                    withdraw(connection, accountToWithdraw);
                    break;
                case 4:
                    System.out.print("Enter From Account Number: ");
                    String fromAccountNum = scanner.nextLine();
                    System.out.print("Enter To Account Number: ");
                    String toAccountNum = scanner.nextLine();
                    System.out.print("Enter Amount to Transfer: ");
                    BigDecimal transferAmount = scanner.nextBigDecimal();
                    scanner.nextLine();
                    transfer(connection, fromAccountNum, toAccountNum, transferAmount);
                    break;
                case 5:
                    System.out.print("Enter Account Number to Delete: ");
                    String accountNumToDelete = scanner.nextLine();
                    deleteAccount(connection, accountNumToDelete);
                    break;
                case 0:
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }

        scanner.close();
        connection.close();
        System.out.println("\nConnection closed.");
    }

    public static void createAccount(Connection connection, Account account) throws SQLException {
        String sql = "INSERT INTO Account(customer_id, account_number, balance) VALUES (" + account.getCustomerId() + ", '" + account.getAccountNumber() + "', 0.00)";
        Statement statement = connection.createStatement();
        int rows = statement.executeUpdate(sql);
        System.out.println(rows + " account created successfully.");
        statement.close();
    }

    public static void deposit(Connection connection, Account account) throws SQLException {
        String sql = "UPDATE Account SET balance = balance + " + account.getBalance() + " WHERE account_number = '" + account.getAccountNumber() + "'";
        Statement statement = connection.createStatement();
        int rows = statement.executeUpdate(sql);

        if (rows > 0) {
            System.out.println("Deposit successful.");
        } else {
            System.out.println("Account not found.");
        }
        statement.close();
    }

    public static void withdraw(Connection connection, Account account) throws SQLException {
        Statement statement = connection.createStatement();
        String checkSql = "SELECT balance FROM Account WHERE account_number = '" + account.getAccountNumber() + "'";
        ResultSet rs = statement.executeQuery(checkSql);

        if (rs.next()) {
            BigDecimal currentBalance = rs.getBigDecimal("balance");
            if (currentBalance.compareTo(account.getBalance()) >= 0) {
                String updateSql = "UPDATE Account SET balance = balance - " + account.getBalance() + " WHERE account_number = '" + account.getAccountNumber() + "'";
                statement.executeUpdate(updateSql);
                System.out.println("Withdrawal successful.");
            } else {
                System.out.println("Insufficient funds.");
            }
        } else {
            System.out.println("Account not found.");
        }
        rs.close();
        statement.close();
    }

    public static void transfer(Connection connection, String fromAccountNumber, String toAccountNumber, BigDecimal amount) throws SQLException {
        Statement statement = connection.createStatement();

        String withdrawSql = "UPDATE Account SET balance = balance - " + amount + " WHERE account_number = '" + fromAccountNumber + "'";
        statement.executeUpdate(withdrawSql);

        String depositSql = "UPDATE Account SET balance = balance + " + amount + " WHERE account_number = '" + toAccountNumber + "'";
        statement.executeUpdate(depositSql);

        statement.close();
        System.out.println("Transfer operation executed.");
    }

    public static void deleteAccount(Connection connection, String accountNumber) throws SQLException {
        String sql = "DELETE FROM Account WHERE account_number = '" + accountNumber + "'";
        Statement statement = connection.createStatement();
        int rows = statement.executeUpdate(sql);

        if (rows > 0) {
            System.out.println("Account deleted successfully.");
        } else {
            System.out.println("Account not found.");
        }
        statement.close();
    }

    public static Connection getConnection() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/banking_db";
        String username = "postgres";
        String password = "1234";
        return DriverManager.getConnection(url, username, password);
    }
}