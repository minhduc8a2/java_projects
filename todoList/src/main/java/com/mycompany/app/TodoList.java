package com.mycompany.app;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class TodoList {
    private static final String FILE_NAME = "tasks.json";
    private List<Task> tasks;
    private Gson gson;
    private Scanner scanner;

    public TodoList() {
        scanner = new Scanner(System.in);
        gson = new Gson();
        loadTasks();
    }

    public void addTask() {
        System.out.println("Enter task description: ");
        String description = scanner.nextLine();
        Task task = new Task(description);
        tasks.add(task);
        System.out.println("Task added successfully");
        saveTasks();
    }

    public void viewTasks(){
        if(tasks.isEmpty()){
            System.out.println("No tasks to display");
        } else {
            for (int i = 0; i < tasks.size(); i++) {
                System.out.println((i + 1) + ". " + tasks.get(i));
            }
        }
    }

        public void updateTask(){
            viewTasks();
            System.out.println("Enter task number to update: ");
            int taskNumber = scanner.nextInt()-1;
            scanner.nextLine();
            if(taskNumber < 0 || taskNumber >= tasks.size()){
                System.out.println("Invalid task number");
            } else {
            
                Task task = tasks.get(taskNumber);
                task.setCompleted(true);
                System.out.println("Task marked as complete!");
                System.out.println("Updated Task: " + task);
                saveTasks();
            }
        }

    public void deleteTask(){
        viewTasks();
        System.out.println("Enter task number to delete: ");
        int taskNumber = scanner.nextInt()-1;
        scanner.nextLine();
        if(taskNumber < 0 || taskNumber >= tasks.size()){
            System.out.println("Invalid task number");
        } else {
            tasks.remove(taskNumber );
            System.out.println("Task deleted successfully");
            saveTasks();
        }
    }

    public void searchTasks(){
        System.out.println("Enter search query: ");
        String query = scanner.nextLine().toLowerCase();
        boolean found = false;
        for (Task task : tasks) {
            if(task.getDescription().toLowerCase().contains(query)){
                System.out.println(task);
                found = true;
            }
        }
        if(!found){
            System.out.println("No matching tasks found");
        }
    }

    private void saveTasks() {

        try (Writer writer = new FileWriter(FILE_NAME)) {
            gson.toJson(tasks, writer);
            
        } catch (IOException e) {
           System.out.println("Error saving tasks: "+e.getMessage());
        }
       
    }

    private void loadTasks() {
        try (Reader reader = new FileReader(FILE_NAME)) {
            tasks = gson.fromJson(reader, new TypeToken<List<Task>>() {}.getType());
            if(tasks==null) {
                tasks = new ArrayList<>();
            }
        } catch (IOException e) {
            tasks = new ArrayList<>();
        }
    }


}
