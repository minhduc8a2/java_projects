import java.util.Arrays;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.Set;

public class EmployeeManager {
    List<Employee> employees = Arrays.asList(
            new Employee("Alice", 30, 60000, "IT"),
            new Employee("Bob", 28, 40000, "HR"),
            new Employee("Charlie", 35, 75000, "Finance"),
            new Employee("David", 40, 50000, "IT"),
            new Employee("Emma", 32, 90000, "Finance"));

    public List<Employee> getEmployeeWithSalaryGreaterThan(double amount) {
        return employees.stream().filter(emp -> emp.getSalary() > amount).toList();
    }

    public List<Employee> getSortedEmployeesByAge() {
        return employees.stream().sorted((a, b) -> Integer.compare(a.getAge(), b.getAge())).toList();
    }

    public Map<String, List<Employee>> getGroupedEmployeesByDepartment() {
        return employees.stream().collect(Collectors.groupingBy(Employee::getDepartment));
    }

    public Map<String, Long> getNumberOfEmployeesPerDepartment() {
        return employees.stream().collect(Collectors.groupingBy(Employee::getDepartment, Collectors.counting()));
    }

    public Map<String, Double> getSumOfSalaryPerDepartment() {
        return employees.stream().collect(
                Collectors.groupingBy(Employee::getDepartment, Collectors.summingDouble(Employee::getSalary)));
    }

    public Map<String,Double> getNameAndSalaryOfEmployees(){
        return employees.stream().collect(Collectors.toMap(Employee::getName,Employee::getSalary ));
    }

    public Set<String> getDepartments(){
        return employees.stream().map(Employee::getDepartment).collect(Collectors.toSet());
    }



}
