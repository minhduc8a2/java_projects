package com.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class EmployeeDAO {

    private static final Logger LOGGER = Logger.getLogger(EmployeeDAO.class.getName());

    public void save(Employee employee) {
        String sql = "INSERT INTO employee ( name, position, salary) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, employee.name());
            statement.setString(2, employee.position());
            statement.setDouble(3, employee.salary());
            statement.executeUpdate();
            LOGGER.info("Employee saved successfully");
        } catch (Exception e) {
            LOGGER.severe("Error saving employee: " + e.getMessage());
        }

    }

    public void update(Employee employee) {
        String sql = "UPDATE employee SET name = ?, position = ?, salary = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, employee.name());
            statement.setString(2, employee.position());
            statement.setDouble(3, employee.salary());
            statement.setInt(4, employee.id());
            statement.executeUpdate();
            LOGGER.info("Employee updated successfully");
        } catch (Exception e) {
            LOGGER.severe("Error updating employee: " + e.getMessage());
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM employee WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
            LOGGER.info("Employee deleted successfully");
        } catch (Exception e) {
            LOGGER.severe("Error deleting employee: " + e.getMessage());
        }
    }

    public Employee findByName(String name) {
        String sql = "SELECT * FROM employee WHERE name LIKE \"%?%\" ";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, name);
            var result = statement.executeQuery();
            if (result.next()) {
                return new Employee(result.getInt("id"), result.getString("name"), result.getString("position"),
                        result.getDouble("salary"));
            }
        } catch (Exception e) {
            LOGGER.severe("Error finding employee: " + e.getMessage());
        }
        return null;
    }

    public List<Employee> findAll() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM employee";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement statement = conn.prepareStatement(sql)) {
            var result = statement.executeQuery();
            while (result.next()) {
                employees.add(new Employee(result.getInt("id"), result.getString("name"), result.getString("position"),
                        result.getDouble("salary")));

            }
        } catch (Exception e) {
            LOGGER.severe("Error finding employee: " + e.getMessage());
        }
        return employees;
    }
}
