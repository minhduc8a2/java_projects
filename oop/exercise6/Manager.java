public class Manager extends Employee {

    public Manager(String name, double salary, double bonus) {
        super(name, salary);
        this.bonus = bonus;
    }

    private double bonus;

    public double getBonus() {
        return bonus;
    }

    public void setBonus(double bonus) {
        this.bonus = bonus;
    }

    @Override
    public String toString() {
        return "[Manager] name: " + super.getName() + ", salary: " + super.getSalary() + ", bonus: " + bonus;
    }

}
