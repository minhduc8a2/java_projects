package com.example.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class MainController {
    @GetMapping("/")
    public String greet() {
        return "Hello World!";
    }

}
