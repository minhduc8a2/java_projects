package com.example;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import java.net.URI;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import com.example.entities.Cart;
import com.example.entities.CartItem;
import com.example.entities.Order;
import com.example.entities.OrderItem;
import com.example.entities.User;
import com.example.repositories.CartItemRepository;
import com.example.repositories.CartRepository;
import com.example.repositories.OrderRepository;
import com.example.repositories.UserRepository;
import com.example.services.AuthService;
import com.example.services.CartService;
import com.example.services.OrderService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class OrderControllerTest {
    private static String token;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private AuthService authService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartService cartService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private OrderService orderService;

    @BeforeEach
    public void setupContext() {
        try {
            token = authService.register(Helper.username, Helper.email, Helper.password);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    public void clearContext() {
        try {

            User user = userRepository.findByUsername(Helper.username).orElseThrow();
            cartRepository.findByUser(user).ifPresent(cartRepository::delete);
            orderRepository.deleteAll(orderRepository.findByUser(user));
            userRepository.delete(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initCartItems() {
        // add cartItems first
        long id = 1;
        for (CartItem cartItem : Helper.sampleCartItems) {
            cartService.addToCart(Helper.username, id++, cartItem.getQuantity());
        }
        //
    }

    @Test
    @DirtiesContext
    public void shouldReturnLocationOfNewOrder() {
        initCartItems();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange("/api/orders", HttpMethod.POST, httpEntity,
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isBlank();

        // cart must be empty
        Cart cart = cartService.getCart(Helper.username);
        List<CartItem> cartItems = cartItemRepository.findByCart(cart);
        assertThat(cartItems.size()).isEqualTo(0);

        URI location = response.getHeaders().getLocation();

        System.out.println("location: " + location.toString());
        ResponseEntity<String> orderResponse = restTemplate.exchange(location, HttpMethod.GET, httpEntity,
                String.class);

        assertThat(orderResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(orderResponse.getBody());

        int id = documentContext.read("$.id");
        assertThat(id).isEqualTo(1);

        int orderItemsLength = documentContext.read("$.orderItems.length()");
        assertThat(orderItemsLength).isEqualTo(3);

    }

    @Test
    @DirtiesContext
    public void shouldGetOrderById() {
        initCartItems();
        orderService.placeOrder(Helper.username);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange("/api/orders/1", HttpMethod.GET, httpEntity,
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        try {

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode orderItemsNode = root.path("orderItems");

            List<OrderItem> orderItems = objectMapper.readValue(
                    orderItemsNode.toString(),
                    new TypeReference<List<OrderItem>>() {
                    });
            assertThat(orderItems.size()).isEqualTo(3);

            assertThat(orderItems.stream().map(o -> o.getProduct().getId()).toList())
                    .isEqualTo(LongStream.rangeClosed(1, Helper.sampleCartItems.size()).boxed().toList());
        } catch (Exception e) {
            e.printStackTrace();
            fail("JSON parsing failed: " + e.getMessage());
        }

    }

    @Test
    @DirtiesContext
    public void shouldGetAllOrderOfUser() {
        initCartItems();
        orderService.placeOrder(Helper.username);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange("/api/orders", HttpMethod.GET, httpEntity,
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        int size = documentContext.read("$.length()");

        assertThat(size).isEqualTo(1);

    }

}
