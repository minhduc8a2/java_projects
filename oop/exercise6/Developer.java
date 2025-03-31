public class Developer extends Employee {

    public Developer(String name, double salary, String programmingLanguage) {
        super(name, salary);
        this.programmingLanguage = programmingLanguage;
    }

    private String programmingLanguage;

    public String getProgramingLanguage() {
        return programmingLanguage;
    }

    public void setProgramingLanguage(String programmingLanguage) {
        this.programmingLanguage = programmingLanguage;
    }

    @Override
    public String toString() {
        return "[Developer] name: " + super.getName() + ", salary: " + super.getSalary() + ", programmingLanguage: "
                + programmingLanguage;
    }
}
