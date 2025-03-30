package com.example;

import java.net.URI;
import java.util.Date;
import java.util.logging.Logger;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import com.example.config.JwtUtils;
import com.example.repository.UserRepository;
import com.example.request.LoginRequest;
import com.example.request.RegisterRequest;
import com.example.service.AuthService;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
// @Disabled
public class AuthControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    private Logger logger = Logger.getLogger(AuthControllerTest.class.getName());

    @BeforeEach
    public void clearDatabase() {
        userRepository.deleteByUsername(Helper.username);
    }

    @Test
    @DirtiesContext
    public void shouldReturnJwtTokenWhenRegistering() {
        RegisterRequest registerRequest = new RegisterRequest(Helper.username, Helper.email, Helper.password);
        ResponseEntity<String> response = restTemplate.postForEntity("/register", registerRequest, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        String token = documentContext.read("$.accessToken");

        assertThat(token).isNotEmpty();

        boolean isValidToken = jwtUtils.isValid(token);
        assertThat(isValidToken).isTrue();

        String tkUsername = jwtUtils.getUsername(token);
        assertThat(tkUsername).isEqualTo(Helper.username);

        Date issuedAt = jwtUtils.getIssuedAt(token);
        Date expiration = jwtUtils.getExpiration(token);

        assertThat(expiration.getTime() - issuedAt.getTime()).isEqualTo(jwtUtils.getJwtExpirationMs());
    }

    @Test
    @DirtiesContext
    public void shouldReturnJwtTokenWhenLogin() {

        // Mock user
        try {
            authService.register(Helper.username, Helper.email, Helper.password);
        } catch (Exception e) {
            fail("Fail to mock user");
        }

        // Login
        LoginRequest loginRequest = new LoginRequest(Helper.username, Helper.password);
        ResponseEntity<String> response = restTemplate.postForEntity("/login", loginRequest, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        String token = documentContext.read("$.accessToken");

        assertThat(token).isNotEmpty();

        boolean isValidToken = jwtUtils.isValid(token);
        assertThat(isValidToken).isTrue();

        String tkUsername = jwtUtils.getUsername(token);
        assertThat(tkUsername).isEqualTo(Helper.username);

        Date issuedAt = jwtUtils.getIssuedAt(token);
        Date expiration = jwtUtils.getExpiration(token);

        assertThat(expiration.getTime() - issuedAt.getTime()).isEqualTo(jwtUtils.getJwtExpirationMs());
    }

}
