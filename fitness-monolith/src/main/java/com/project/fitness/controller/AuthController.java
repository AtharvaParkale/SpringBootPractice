package com.project.fitness.controller;

import ch.qos.logback.core.Context;
import com.project.fitness.model.User;
import com.project.fitness.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// This is where you add all your endpoints
@RestController // As we are building rest api
@RequestMapping("/api/auth") // Same is appended everywhere
@RequiredArgsConstructor
public class AuthController {


    private final UserService userService;

    //You dont need to write this , just annotate with lombok, it will take care
    //    public AuthController(UserService userService) {
    //        this.userService = userService;
    //    }

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return userService.register(user);
    }
}
