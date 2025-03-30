package com.example;

import java.util.List;

import com.example.model.UserModel;
import com.example.model.entity.CartItem;
import com.example.model.entity.Product;
import com.example.model.entity.User.Role;

public class Helper {
        public static List<Product> sampleProducts = List.of(
                        new Product(null, "Samsung Galaxy S24 Ultra",
                                        "Flagship smartphone with Snapdragon 8 Gen 3 and 200MP camera", 1199.99, 30),
                        new Product(null, "Apple iPhone 15 Pro Max",
                                        "Premium smartphone with A17 Bionic chip and 48MP main camera",
                                        1299.99, 25),
                        new Product(null, "Xiaomi 14 Ultra", "High-end smartphone with Leica camera and HyperOS",
                                        999.99, 40),
                        new Product(null, "OnePlus 12 Pro",
                                        "Powerful smartphone with 100W fast charging and Hasselblad camera",
                                        899.99, 35),
                        new Product(null, "Google Pixel 8 Pro",
                                        "AI-powered smartphone with Google Tensor G3 chip and amazing camera", 999.99,
                                        20),
                        new Product(null, "Samsung Galaxy Z Fold5",
                                        "Foldable smartphone with 7.6-inch Dynamic AMOLED 2X display",
                                        1799.99, 15),
                        new Product(null, "Apple iPhone SE (3rd Gen)",
                                        "Compact iPhone with A15 Bionic chip and 5G connectivity",
                                        429.99, 50),
                        new Product(null, "Xiaomi Redmi Note 13 Pro",
                                        "Budget smartphone with 200MP camera and 120Hz AMOLED display", 349.99, 100),
                        new Product(null, "Asus ROG Phone 8",
                                        "Gaming smartphone with Snapdragon 8 Gen 3 and 165Hz display",
                                        1099.99, 20),
                        new Product(null, "Sony Xperia 1 V",
                                        "Sony’s flagship phone with 4K OLED display and pro-grade camera",
                                        1199.99, 10));
        public static final List<CartItem> sampleCartItems = List.of(
                        new CartItem(null, null, sampleProducts.get(0), 10),
                        new CartItem(null, null, sampleProducts.get(1), 20),
                        new CartItem(null, null, sampleProducts.get(2), 30));

        public static final UserModel USER_1 = new UserModel("username1", "user1@gmail.com", "user1password",
                        Role.ROLE_USER);

        public static final UserModel USER_2 = new UserModel("username2", "user2@gmail.com", "user2password",
                        Role.ROLE_USER);

        public static final UserModel ADMIN = new UserModel("admin", "admin@gmail.com", "admin123456", Role.ROLE_ADMIN);
}
