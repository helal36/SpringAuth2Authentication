package com.example.resource_server.controller;


import org.springframework.web.bind.annotation.*;

@RestController
public class AccountController {
    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/balance")
    public String getBalance() {
        System.out.println("AccountController-->getBalance called");
        return "🏦 Your balance is ₹10,000";
    }
    @GetMapping("/balance2")
    public String getBalance2() {
        System.out.println("AccountController-->getBalance2 called");
        return "🏦 Your balance is ₹12,000";
    }
}
