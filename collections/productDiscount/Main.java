import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        List<Product> products = Arrays.asList(
                new Product("Laptop", 1200),
                new Product("Phone", 800),
                new Product("Tablet", 500),
                new Product("Monitor", 300));
        List<Product> discounteProducts = products.stream()
                .map(pro -> new Product(pro.getName(), pro.getDiscountedPrice())).toList();

        double averagePrice = discounteProducts.stream().mapToDouble(Product::getPrice).average().orElse(0);


        double totalCost = discounteProducts.stream().collect(Collectors.summingDouble(Product::getPrice));
        // double totalCost = discounteProducts.stream().mapToDouble(Product::getPrice).sum();

        Optional<Product> cheapestProduct = discounteProducts.stream().min(Comparator.comparing(Product::getPrice));

        Optional<Product> mostExpensiveProduct = discounteProducts.stream()
                .max(Comparator.comparing(Product::getPrice));

        List<Product> top3ExpensiveProducts = discounteProducts.stream()
                .sorted(Comparator.comparing(Product::getPrice).reversed()).limit(3).toList();

        System.out.println(discounteProducts);
        System.out.println("Total cost: " + totalCost);

        System.out.println("Cheapest Product: ");
        cheapestProduct.ifPresent(System.out::println);

        System.out.println("Most expensive Product: ");
        mostExpensiveProduct.ifPresent(System.out::println);

        System.out.println("Top 3 expensive products");
        System.out.println(top3ExpensiveProducts);

        System.out.println("Average price: " + averagePrice);

    }
}
