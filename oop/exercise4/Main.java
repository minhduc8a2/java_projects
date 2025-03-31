import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Vehicle> vehicles = List.of(new Car(), new Motorcycle());
        vehicles.forEach(Vehicle::makeDescription);
    }
}
