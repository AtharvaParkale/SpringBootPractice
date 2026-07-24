package com.springboot.SpringSecurities.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    private String sayHello() {
        return "Hello";
    }

    @GetMapping("/admin/hello")
    private String sayAdminHello() {
        return "Hello Admin!";
    }

    @GetMapping("/user/hello")
    private String sayUserHello() {
        return "Hello User!";
    }
}
