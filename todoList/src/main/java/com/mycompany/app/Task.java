package com.mycompany.app;

import java.io.Serializable;

public class Task implements Serializable{

    public static final long serialVersionUID =1L;
    private String description;
    private boolean isCompleted;

    public Task(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
   
    public boolean isCompleted() {
        return isCompleted;
    }
    public void setCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    @Override
    public String toString() {
        return (isCompleted ? "[x] " : "[ ] ") + description;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Task) {
            Task task = (Task) obj;
            return description.toLowerCase().equals(task.description.toLowerCase());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return description.hashCode();
    }
    
}