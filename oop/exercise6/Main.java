import java.util.*;
public class Main {
    public static void main(String[] args) {
        List<Employee> employees = List.of(
            new Employee("John",2000),
            new Manager("Tony",2500,500),
            new Developer("Peter",3000,"Java")
        );
        employees.forEach(System.out::println);
    }
}
