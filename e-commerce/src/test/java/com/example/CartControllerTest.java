package com.example;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
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
import com.example.repositories.UserRepository;
import com.example.requests.AddToCartRequest;
import com.example.services.AuthService;
import com.example.services.CartService;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class CartControllerTest {

    private static String token;
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CartService cartService;

    @BeforeAll
    public static void setupContext(@Autowired AuthService authService) {
        try {
            token = authService.register(Helper.username, Helper.email, Helper.password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    public static void clearContext(@Autowired UserRepository userRepository) {
        try {
            User user = userRepository.findByUsername(Helper.username).orElseThrow();
            userRepository.delete(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        ResponseEntity<String> cartItemsResponse = restTemplate.exchange("/api/carts", HttpMethod.GET, getEntity, String.class);

        assertThat(cartItemsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Parse response JSON
        DocumentContext documentContext = JsonPath.parse(cartItemsResponse.getBody());
        int cartItemsSize = documentContext.read("$.length()");

        assertThat(cartItemsSize).isEqualTo(1);

    }

    


}
