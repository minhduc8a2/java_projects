package com.example;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import java.net.URI;
import java.util.List;
import java.util.stream.LongStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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

import com.example.model.UserModel;
import com.example.model.dto.CartDTO;
import com.example.model.dto.OrderItemDTO;
import com.example.model.entity.CartItem;
import com.example.model.entity.User;
import com.example.model.entity.User.Role;
import com.example.repository.CartRepository;
import com.example.repository.OrderRepository;
import com.example.repository.UserRepository;
import com.example.service.AuthService;
import com.example.service.CartService;
import com.example.service.OrderService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class OrderControllerTest {
    private static String token1;
    private static String token2;
    private static String adminToken;
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
    private OrderService orderService;

    private String generateToken(UserModel user) {
        return authService.register(user.username(), user.email(), user.password(), user.role());
    }

    @BeforeEach
    void setupContext() {
        try {
            token1 = generateToken(Helper.USER_1);
            token2 = generateToken(Helper.USER_2);
            adminToken = generateToken(Helper.ADMIN);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteUser(String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        cartRepository.findByUser(user).ifPresent(cartRepository::delete);
        orderRepository.deleteAll(orderRepository.findByUser(user));
        userRepository.delete(user);
    }

    @AfterEach
    void clearContext() {
        try {
            deleteUser(Helper.USER_1.username());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initCartItems() {
        // add cartItems first
        long id = 1;
        for (CartItem cartItem : Helper.sampleCartItems) {
            cartService.addToCart(Helper.USER_1.username(), id++, cartItem.getQuantity());
        }
        //
    }

    private ResponseEntity<String> createBearerAuthResponseEntity(String token, String url, HttpMethod method) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> httpEntity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, method, httpEntity,
                String.class);
    }

    @Test
    @DirtiesContext
    // @Disabled
    void shouldReturnLocationOfNewOrder() {
        initCartItems();
        ResponseEntity<String> response = createBearerAuthResponseEntity(token1, "/api/orders", HttpMethod.POST);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isBlank();

        // cart must be empty
        CartDTO cart = cartService.getCart(Helper.USER_1.username());

        assertThat(cart.getCartItems()).isEmpty();

        URI location = response.getHeaders().getLocation();

        if (location == null) {
            fail("Location must not null");
            return;
        }
        ResponseEntity<String> orderResponse = createBearerAuthResponseEntity(token1, location.toString(),
                HttpMethod.GET);

        assertThat(orderResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(orderResponse.getBody());

        int id = documentContext.read("$.id");
        assertThat(id).isEqualTo(1);

        int orderItemsLength = documentContext.read("$.orderItems.length()");
        assertThat(orderItemsLength).isEqualTo(3);

    }

    @Test
    @DirtiesContext
    // @Disabled

    void shouldGetOrderById() {
        initCartItems();
        orderService.placeOrder(Helper.USER_1.username());

        ResponseEntity<String> response = createBearerAuthResponseEntity(token1, "/api/orders/1", HttpMethod.GET);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        try {

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode orderItemsNode = root.path("orderItems");

            List<OrderItemDTO> orderItems = objectMapper.readValue(
                    orderItemsNode.toString(),
                    new TypeReference<List<OrderItemDTO>>() {
                    });
            assertThat(orderItems).hasSize(3);

            assertThat(orderItems.stream().map(o -> o.getProductId()).toList())
                    .isEqualTo(LongStream.rangeClosed(1, Helper.sampleCartItems.size()).boxed().toList());
        } catch (Exception e) {
            e.printStackTrace();
            fail("JSON parsing failed: " + e.getMessage());
        }

    }

    @Test
    @DirtiesContext
    void NotOwnershouldNotGetOrderById() {
        initCartItems();
        orderService.placeOrder(Helper.USER_1.username());

        ResponseEntity<String> response = createBearerAuthResponseEntity(token2, "/api/orders/1", HttpMethod.GET);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        response = createBearerAuthResponseEntity(token1, "/api/orders/1", HttpMethod.GET);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DirtiesContext
    void AdminshouldGetAnyOrderById() {
        initCartItems();
        orderService.placeOrder(Helper.USER_1.username());

        ResponseEntity<String> response = createBearerAuthResponseEntity(adminToken, "/api/orders/1", HttpMethod.GET);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    }

    

    @Test
    @DirtiesContext
    // @Disabled
    void shouldGetAllOrderOfUser() {
        initCartItems();
        orderService.placeOrder(Helper.USER_1.username());

        ResponseEntity<String> response = createBearerAuthResponseEntity(token1, "/api/orders", HttpMethod.GET);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        int size = documentContext.read("$.length()");

        assertThat(size).isEqualTo(1);

    }

}
