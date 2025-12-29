
import java.sql.*;
import java.time.Instant;
import java.util.Scanner;

public class ConsoleEmployeeInserter {

    // Your Supabase connection details
    private static final String URL = "jdbc:postgresql://aws-1-ap-southeast-2.pooler.supabase.com:6543/postgres";
    private static final String USER = "postgres.mrqhxgvlqtovzgajwtwg";
    private static final String PASSWORD = "devang@786devang";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        showMenu(scanner);
        scanner.close();
    }

    private static void showMenu(Scanner scanner) {
        while (true) {
            System.out.println("\n=== Supabase Employee Management System ===");
            System.out.println("1. Add New Employee        2. View All Employees");
            System.out.println("3. View Employee by ID     4. Update Employee");
            System.out.println("5. Delete Employee         6. Add New Task");
            System.out.println("7. View All Tasks          8. View Task by ID");
            System.out.println("9. Update Task             10. Delete Task");
            System.out.println("0. Exit");
            System.out.print("\nChoose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    addEmployee(scanner);
                    break;
                case "2":
                    viewAllEmployees();
                    break;
                case "3":
                    viewEmployeeById(scanner);
                    break;
                case "4":
                    updateEmployee(scanner);
                    break;
                case "5":
                    deleteEmployee(scanner);
                    break;
                case "6":
                    addTask(scanner);
                    break;
                case "7":
                    viewAllTasks();
                    break;
                case "8":
                    viewTaskById(scanner);
                    break;
                case "9":
                    updateTask(scanner);
                    break;
                case "10":
                    deleteTask(scanner);
                    break;
                case "0":
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("❌ Invalid option. Please try again.");
            }
        }
    }

    // === EMPLOYEE METHODS ===
    private static void addEmployee(Scanner scanner) {
        System.out.print("Employee Name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("❌ Name cannot be empty.");
            return;
        }
        System.out.print("Salary: ");
        try {
            int salary = Integer.parseInt(scanner.nextLine().trim());
            insertEmployee(name, salary);
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid salary. Enter a valid number.");
        }
    }

    private static void insertEmployee(String name, int salary) {
        String sql = "INSERT INTO employees (name, salary, created_at) VALUES (?, ?, ?) RETURNING id";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, salary);
            pstmt.setTimestamp(3, Timestamp.from(Instant.now()));
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                long id = rs.getLong("id");
                System.out.println("\n✅ Success! Employee added:");
                System.out.println("   ID: " + id + ", Name: " + name + ", Salary: $" + salary);
            }
        } catch (SQLException e) {
            System.err.println("❌ Database Error: " + e.getMessage());
        }
    }

    private static void viewAllEmployees() {
        String sql = "SELECT id, name, salary, created_at FROM employees ORDER BY id DESC";
        printEmployeeTable(sql, "📋 All Employees:");
    }

    private static void viewEmployeeById(Scanner scanner) {
        System.out.print("Enter Employee ID: ");
        try {
            long id = Long.parseLong(scanner.nextLine().trim());
            String sql = "SELECT id, name, salary, created_at FROM employees WHERE id = ?";
            printEmployeeDetails(id, sql, "👤 Employee Details:");
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid ID format.");
        }
    }

    // === TASK METHODS ===
    private static void addTask(Scanner scanner) {
        System.out.print("Task Title: ");
        String title = scanner.nextLine().trim();
        if (title.isEmpty()) {
            System.out.println("❌ Title cannot be empty.");
            return;
        }
        System.out.print("Description: ");
        String description = scanner.nextLine().trim();
        System.out.print("Assigned Employee ID: ");
        try {
            long employeeId = Long.parseLong(scanner.nextLine().trim());
            if (!employeeExists(employeeId)) {
                System.out.println("❌ Employee ID not found. Add employee first.");
                return;
            }
            insertTask(title, description, employeeId);
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid Employee ID.");
        }
    }

    private static void insertTask(String title, String description, long employeeId) {
        String sql = "INSERT INTO tasks (title, description, status, employee_id, created_at) VALUES (?, ?, ?, ?, ?) RETURNING id";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, description.isEmpty() ? null : description);
            pstmt.setString(3, "pending");  // ✅ Fixed: Added status column
            pstmt.setLong(4, employeeId);
            pstmt.setTimestamp(5, Timestamp.from(Instant.now()));
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                long id = rs.getLong("id");
                System.out.println("\n✅ Success! Task added:");
                System.out.println("   ID: " + id + ", Title: " + title + ", Employee ID: " + employeeId);
            }
        } catch (SQLException e) {
            System.err.println("❌ Database Error: " + e.getMessage());
        }
    }

    private static void viewAllTasks() {
        String sql = """
            SELECT t.id, t.title, t.description, t.status, t.employee_id, e.name as employee_name, t.created_at 
            FROM tasks t 
            LEFT JOIN employees e ON t.employee_id = e.id 
            ORDER BY t.id DESC
            """;
        printTaskTable(sql, "📋 All Tasks:");
    }

    private static void viewTaskById(Scanner scanner) {
        System.out.print("Enter Task ID: ");
        try {
            long id = Long.parseLong(scanner.nextLine().trim());
            String sql = """
                SELECT t.id, t.title, t.description, t.status, t.employee_id, e.name as employee_name, t.created_at 
                FROM tasks t 
                LEFT JOIN employees e ON t.employee_id = e.id 
                WHERE t.id = ?
                """;
            printTaskDetails(id, sql, "📝 Task Details:");
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid ID format.");
        }
    }

    // === PRINT METHODS ===
    private static void printEmployeeTable(String sql, String title) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {

            System.out.println("\n" + title);
            System.out.printf("%-5s %-20s %-10s %s%n", "ID", "Name", "Salary", "Created");
            System.out.println("-----------------------------------------------------");

            boolean hasRecords = false;
            while (rs.next()) {
                hasRecords = true;
                System.out.printf("%-5d %-20s $%-9d %s%n",
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getInt("salary"),
                        rs.getTimestamp("created_at").toString().substring(0, 19));
            }
            if (!hasRecords) {
                System.out.println("   No employees found.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }

    private static void printEmployeeDetails(long id, String sql, String title) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("\n" + title);
                    System.out.printf("ID: %d%nName: %s%nSalary: $%d%nCreated: %s%n",
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getInt("salary"),
                            rs.getTimestamp("created_at"));
                } else {
                    System.out.println("\n❌ Employee with ID " + id + " not found.");
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }

    private static void printTaskTable(String sql, String title) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n" + title);
            System.out.printf("%-5s %-20s %-10s %-10s %-15s %s%n", "ID", "Title", "Status", "Desc", "Employee", "Created");
            System.out.println("-------------------------------------------------------------------------------------");

            boolean hasRecords = false;
            while (rs.next()) {
                hasRecords = true;
                String desc = rs.getString("description");
                String empName = rs.getString("employee_name");
                System.out.printf("%-5d %-20s %-10s %-10s %-15s %s%n",
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("status"),
                        desc != null ? desc.substring(0, Math.min(10, desc.length())) : "",
                        empName != null ? empName : "N/A",
                        rs.getTimestamp("created_at").toString().substring(0, 19));
            }
            if (!hasRecords) {
                System.out.println("   No tasks found.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }

    private static void printTaskDetails(long id, String sql, String title) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("\n" + title);
                    System.out.printf("ID: %d%nTitle: %s%nStatus: %s%nDescription: %s%nEmployee ID: %d%nEmployee Name: %s%nCreated: %s%n",
                            rs.getLong("id"),
                            rs.getString("title"),
                            rs.getString("status"),
                            rs.getString("description") != null ? rs.getString("description") : "N/A",
                            rs.getLong("employee_id"),
                            rs.getString("employee_name") != null ? rs.getString("employee_name") : "N/A",
                            rs.getTimestamp("created_at"));
                } else {
                    System.out.println("\n❌ Task with ID " + id + " not found.");
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }

    // === UPDATE METHODS ===
    private static void updateEmployee(Scanner scanner) {
        System.out.print("Enter Employee ID to update: ");
        try {
            long id = Long.parseLong(scanner.nextLine().trim());
            if (!employeeExists(id)) {
                System.out.println("❌ Employee not found.");
                return;
            }
            System.out.print("New Name (Enter to skip): ");
            String name = scanner.nextLine().trim();
            System.out.print("New Salary (Enter to skip): ");
            String salaryInput = scanner.nextLine().trim();

            String sql = "UPDATE employees SET name = COALESCE(?, name), salary = COALESCE(?, salary) WHERE id = ?";
            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD); PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, name.isEmpty() ? null : name);
                pstmt.setObject(2, salaryInput.isEmpty() ? null : Integer.parseInt(salaryInput));
                pstmt.setLong(3, id);
                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    System.out.println("\n✅ Employee ID " + id + " updated!");
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    private static void updateTask(Scanner scanner) {
        System.out.print("Enter Task ID to update: ");
        try {
            long id = Long.parseLong(scanner.nextLine().trim());
            if (!taskExists(id)) {
                System.out.println("❌ Task not found.");
                return;
            }
            System.out.print("New Title (Enter to skip): ");
            String title = scanner.nextLine().trim();
            System.out.print("New Description (Enter to skip): ");
            String desc = scanner.nextLine().trim();
            System.out.print("New Status (Enter to skip): ");
            String status = scanner.nextLine().trim();
            System.out.print("New Employee ID (Enter to skip): ");
            String empIdInput = scanner.nextLine().trim();

            String sql = "UPDATE tasks SET title = COALESCE(?, title), description = COALESCE(?, description), status = COALESCE(?, status), employee_id = COALESCE(?, employee_id) WHERE id = ?";
            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD); PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, title.isEmpty() ? null : title);
                pstmt.setString(2, desc.isEmpty() ? null : desc);
                pstmt.setString(3, status.isEmpty() ? null : status);
                pstmt.setObject(4, empIdInput.isEmpty() ? null : Long.parseLong(empIdInput));
                pstmt.setLong(5, id);
                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    System.out.println("\n✅ Task ID " + id + " updated!");
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    // === DELETE METHODS ===
    private static void deleteEmployee(Scanner scanner) {
        deleteRecord(scanner, "employees", "Employee");
    }

    private static void deleteTask(Scanner scanner) {
        deleteRecord(scanner, "tasks", "Task");
    }

    private static void deleteRecord(Scanner scanner, String table, String type) {
        System.out.print("Enter " + type + " ID to delete: ");
        try {
            long id = Long.parseLong(scanner.nextLine().trim());
            if (!recordExists(id, table)) {
                System.out.println("❌ " + type + " ID " + id + " not found.");
                return;
            }
            System.out.print("Are you sure? (yes/no): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("yes")) {
                String sql = "DELETE FROM " + table + " WHERE id = ?";
                try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD); PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setLong(1, id);
                    int rows = pstmt.executeUpdate();
                    if (rows > 0) {
                        System.out.println("\n🗑️ " + type + " ID " + id + " deleted!");
                    }
                }
            } else {
                System.out.println("Operation cancelled.");
            }
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    // === HELPER METHODS ===
    private static boolean employeeExists(long id) {
        return recordExists(id, "employees");
    }

    private static boolean taskExists(long id) {
        return recordExists(id, "tasks");
    }

    private static boolean recordExists(long id, String table) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD); PreparedStatement pstmt = conn.prepareStatement("SELECT 1 FROM " + table + " WHERE id = ?")) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            return false;
        }
    }
}
