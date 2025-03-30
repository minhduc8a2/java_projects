package com.example.model;

public record UserModel(
        String username,
        String email,
        String password,
        com.example.model.entity.User.Role role) {

}
