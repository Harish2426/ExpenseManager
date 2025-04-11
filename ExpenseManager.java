import java.sql.*;
import java.util.Scanner;

public class ExpenseManager {
    private static final String DB_URL = "jdbc:sqlite:expenses.db";

    public static void main(String[] args) {
        createTableIfNotExists();

        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n=== Expense Management System ===");
            System.out.println("1. Add Expense");
            System.out.println("2. View All Expenses");
            System.out.println("3. Filter by Category");
            System.out.println("4. Filter by Date");
            System.out.println("5. Delete Expense");
            System.out.println("6. Total Expense");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();  // consume newline

            switch (choice) {
                case 1 -> addExpense(scanner);
                case 2 -> viewExpenses();
                case 3 -> filterByCategory(scanner);
                case 4 -> filterByDate(scanner);
                case 5 -> deleteExpense(scanner);
                case 6 -> totalExpense();
                case 0 -> System.out.println("👋 Exiting...");
                default -> System.out.println("❌ Invalid choice.");
            }
        } while (choice != 0);
    }

    private static void createTableIfNotExists() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            String sql = """
                CREATE TABLE IF NOT EXISTS expenses (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    description TEXT NOT NULL,
                    amount REAL NOT NULL,
                    category TEXT,
                    date TEXT NOT NULL
                )
            """;
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private static void addExpense(Scanner scanner) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "INSERT INTO expenses (description, amount, category, date) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            System.out.print("Description: ");
            String description = scanner.nextLine();

            System.out.print("Amount: ");
            double amount = scanner.nextDouble();
            scanner.nextLine();

            System.out.print("Category: ");
            String category = scanner.nextLine();

            System.out.print("Date (YYYY-MM-DD): ");
            String date = scanner.nextLine();

            pstmt.setString(1, description);
            pstmt.setDouble(2, amount);
            pstmt.setString(3, category);
            pstmt.setString(4, date);

            pstmt.executeUpdate();
            System.out.println("✅ Expense added successfully.");
        } catch (SQLException e) {
            System.out.println("❌ Error adding expense: " + e.getMessage());
        }
    }

    private static void viewExpenses() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM expenses ORDER BY date DESC")) {

            System.out.println("\n📋 All Expenses:");
            while (rs.next()) {
                System.out.printf("%d. %s - $%.2f | %s | %s%n",
                        rs.getInt("id"),
                        rs.getString("description"),
                        rs.getDouble("amount"),
                        rs.getString("category"),
                        rs.getString("date"));
            }
        } catch (SQLException e) {
            System.out.println("❌ Error viewing expenses: " + e.getMessage());
        }
    }

    private static void filterByCategory(Scanner scanner) {
        System.out.print("Enter category: ");
        String category = scanner.nextLine();

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "SELECT * FROM expenses WHERE category = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, category);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\n📂 Filtered by Category:");
            while (rs.next()) {
                System.out.printf("%d. %s - $%.2f | %s | %s%n",
                        rs.getInt("id"),
                        rs.getString("description"),
                        rs.getDouble("amount"),
                        rs.getString("category"),
                        rs.getString("date"));
            }
        } catch (SQLException e) {
            System.out.println("❌ Error filtering: " + e.getMessage());
        }
    }

    private static void filterByDate(Scanner scanner) {
        System.out.print("Enter date (YYYY-MM-DD): ");
        String date = scanner.nextLine();

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "SELECT * FROM expenses WHERE date = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, date);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\n📅 Filtered by Date:");
            while (rs.next()) {
                System.out.printf("%d. %s - $%.2f | %s | %s%n",
                        rs.getInt("id"),
                        rs.getString("description"),
                        rs.getDouble("amount"),
                        rs.getString("category"),
                        rs.getString("date"));
            }
        } catch (SQLException e) {
            System.out.println("❌ Error filtering: " + e.getMessage());
        }
    }

    private static void deleteExpense(Scanner scanner) {
        System.out.print("Enter ID to delete: ");
        int id = scanner.nextInt();

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "DELETE FROM expenses WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("✅ Expense deleted.");
            } else {
                System.out.println("⚠️ Expense not found.");
            }
        } catch (SQLException e) {
            System.out.println("❌ Error deleting: " + e.getMessage());
        }
    }

    private static void totalExpense() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.out.println("❌ SQLite JDBC driver not found.");
        }
        
    }
}
