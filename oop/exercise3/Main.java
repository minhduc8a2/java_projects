import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Animal> animals = List.of(new Cat(),new Dog(), new Cat());
        animals.stream().forEach(Animal::makeSound);
    }
}
