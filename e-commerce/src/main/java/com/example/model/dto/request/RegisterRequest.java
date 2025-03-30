package com.example.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Username is required") 
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters") 
        String username,

        @NotBlank(message = "Email is required") 
        @Email(message = "Invalid email format") 
        String email,

        @NotBlank(message = "Password is required") 
        @Size(min = 8, message = "Password must be at least 8 characters long") 
        String password) {

}
