package com.example;

import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    private static Scanner scanner = new Scanner(System.in);
    private static EmployeeDAO employeeDAO = new EmployeeDAO();

    public static void start() {
        while (true) {
            System.out.println("\n--- Employee Management System ---");
            System.out.println("1. Add Employee");
            System.out.println("2. View Employees");
            System.out.println("3. Update Employee");
            System.out.println("4. Delete Employee");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> addEmployee();
                case 2 -> viewEmployees();
                case 3 -> updateEmployee();
                case 4 -> deleteEmployee();
                case 5 -> {
                    System.out.println("Exiting...");
                    return;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }

    }

    private static void addEmployee() {
        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        System.out.print("Enter position: ");
        String position = scanner.nextLine();
        System.out.print("Enter salary: ");
        double salary = scanner.nextDouble();
        employeeDAO.save(new Employee(null, name, position, salary));
    }

    private static void viewEmployees() {
        List<Employee> employees = employeeDAO.findAll();
        employees.forEach(System.out::println);
    }

    private static void updateEmployee() {
        System.out.print("Enter Employee ID to update: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter new name: ");
        String name = scanner.nextLine();
        System.out.print("Enter new position: ");
        String position = scanner.nextLine();
        System.out.print("Enter new salary: ");
        double salary = scanner.nextDouble();
        employeeDAO.update(new Employee(id, name, position, salary));
    }

    private static void deleteEmployee() {
        System.out.print("Enter Employee ID to delete: ");
        int id = scanner.nextInt();
        employeeDAO.delete(id);
    }
}
