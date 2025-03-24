package com.example;

record Employee(Integer id, String name, String position, double salary) {
    public Employee {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (position == null || position.isBlank()) {
            throw new IllegalArgumentException("Position cannot be null or empty");
        }
       
    }
}