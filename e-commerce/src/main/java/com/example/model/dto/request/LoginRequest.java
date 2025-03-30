package com.example.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "Username must not be blank") 
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters") 
        String username,

        @NotBlank(message = "Password is required") 
        @Size(min = 8, message = "Password must be at least 8 characters long") 
        String password
) {

}
