CREATE DATABASE employee_db;
USE employee_db;

CREATE TABLE employee(
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    position VARCHAR(100) NOT NULL,
    salary DOUBLE NOT NULL
);