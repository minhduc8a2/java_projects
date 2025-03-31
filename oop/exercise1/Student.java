public class Student extends Person {
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public void displayInfo() {
        System.out.println("I'm a student");
        System.out.println("My id: " + id);
        super.displayInfo();

    }

}
