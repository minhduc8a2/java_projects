package com.example;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.IntStream;
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

import com.example.model.dto.request.AddToCartRequest;
import com.example.model.entity.CartItem;
import com.example.model.entity.User;
import com.example.repository.CartItemRepository;
import com.example.repository.CartRepository;
import com.example.repository.UserRepository;
import com.example.service.AuthService;
import com.example.service.CartService;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
// @Disabled
class CartControllerTest {

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
    void setupContext() {
        try {
            token = authService.register(Helper.USER_1.username(), Helper.USER_1.email(), Helper.USER_1.password());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void clearContext() {
        try {

            User user = userRepository.findByUsername(Helper.USER_1.username()).orElseThrow();
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
            cartService.addToCart(Helper.USER_1.username(), id++, cartItem.getQuantity());
        }
        //
    }

    @Test
    @DirtiesContext
    void shouldGetCartItems() {
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
    void shouldAddProductToCart() {
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
    void shouldDeleteCartItem() {
        initCartItems();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<Void> response = restTemplate.exchange("/api/carts/cartItems/" + 1, HttpMethod.DELETE,
                httpEntity, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        assertThat(cartItemRepository.findById(1L)).isNotPresent();

    }

}
