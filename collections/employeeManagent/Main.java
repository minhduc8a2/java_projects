import java.util.List;
import java.util.Map;
import java.util.Set;
public class Main {

    public static void main(String[] args) {
        EmployeeManager employeeManager = new EmployeeManager();

        System.out.println("Emploees with salary greater than 50000: ");
        System.out.println(employeeManager.getEmployeeWithSalaryGreaterThan(50000));

        System.out.println("Sorted Employees by age: ");
        System.out.println(employeeManager.getSortedEmployeesByAge());

        System.out.println("Grouped Employees by department: ");
        var grouped =  employeeManager.getGroupedEmployeesByDepartment();
        for(Map.Entry<String,List<Employee>> entry: grouped.entrySet()){
            System.out.println("["+entry.getKey()+"]"+entry.getValue());
        }

        System.out.println("Sum of salary by department: ");
        var groupedSalary =  employeeManager.getSumOfSalaryPerDepartment();
        for(Map.Entry<String,Double> entry: groupedSalary.entrySet()){
            System.out.println("["+entry.getKey()+"]"+entry.getValue());
        }

        System.out.println("Salary per person: ");
        var salaryPerPerson =  employeeManager.getNameAndSalaryOfEmployees();
        for(Map.Entry<String,Double> entry: salaryPerPerson.entrySet()){
            System.out.println("["+entry.getKey()+"]"+entry.getValue());
        }

        Set<String> departments = employeeManager.getDepartments();
        System.out.println("Departments: "+departments);


    }
}
