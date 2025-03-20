package com.mycompany.app;

import java.util.Scanner;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        TodoList toDoList = new TodoList();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n📌 To-Do List");
            System.out.println("1 Add Task");
            System.out.println("2 View Tasks");
            System.out.println("3 Update Task");
            System.out.println("4 Delete Task");
            System.out.println("5 Search task");
            System.out.println("6 Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> toDoList.addTask();
                case 2 -> toDoList.viewTasks();
                case 3 -> toDoList.updateTask();
                case 4 -> toDoList.deleteTask();
                case 5 -> toDoList.searchTasks();
                case 6 -> {
                    System.out.println("📌 Exiting... Goodbye!");
                    scanner.close();
                    System.exit(0);
                }
                default -> System.out.println("❌ Invalid option, try again.");
            }
        }
    }
    
}
