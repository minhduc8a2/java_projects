package com.example;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
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

import com.example.entities.CartItem;
import com.example.entities.User;
import com.example.repositories.CartItemRepository;
import com.example.repositories.CartRepository;
import com.example.repositories.UserRepository;
import com.example.requests.AddToCartRequest;
import com.example.services.AuthService;
import com.example.services.CartService;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
// @Disabled
public class CartControllerTest {

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
    private CartItemRepository cartItemRepository;

    @Autowired
    private CartService cartService;

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
    public void shouldGetCartItems() {
        initCartItems();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange("/api/carts", HttpMethod.GET, httpEntity,
                String.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(responseEntity.getBody());

        int size = documentContext.read("$.length()");
        assertThat(size).isEqualTo(Helper.sampleCartItems.size());

        List<Integer> ids = documentContext.read("$[*].id");

        assertThat(ids).isEqualTo(IntStream.rangeClosed(1, Helper.sampleCartItems.size())
                .boxed()
                .toList());

        List<Integer> quantities = documentContext.read("$[*].quantity");

        assertThat(quantities)
                .isEqualTo(Helper.sampleCartItems.stream().map(CartItem::getQuantity).toList());

    }

    @Test
    @DirtiesContext
    public void shouldAddProductToCart() {
        AddToCartRequest request = new AddToCartRequest(1L, 50);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<AddToCartRequest> entity = new HttpEntity<>(request, headers);
        ResponseEntity<Void> response = restTemplate.exchange("/api/carts", HttpMethod.POST, entity, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        HttpEntity<Void> getEntity = new HttpEntity<>(headers);
        ResponseEntity<String> cartItemsResponse = restTemplate.exchange("/api/carts", HttpMethod.GET, getEntity,
                String.class);

        assertThat(cartItemsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Parse response JSON
        DocumentContext documentContext = JsonPath.parse(cartItemsResponse.getBody());
        int cartItemsSize = documentContext.read("$.length()");

        assertThat(cartItemsSize).isEqualTo(1);

    }

    @Test
    @DirtiesContext
    public void shouldDeleteCartItem() {
        initCartItems();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<Void> response = restTemplate.exchange("/api/carts/cartItems/" + 1, HttpMethod.DELETE,
                httpEntity, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        assertThat(cartItemRepository.findById(1L).isPresent()).isFalse();

    }

}
