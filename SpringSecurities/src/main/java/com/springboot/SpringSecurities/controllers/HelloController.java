package com.springboot.SpringSecurities.controllers;

import com.springboot.SpringSecurities.JwtUtils;
import com.springboot.SpringSecurities.LoginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

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

    @PostMapping("/signin")
    public String login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication;

        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()

                    )
            );
        } catch (AuthenticationException e) {
            e.printStackTrace();

            return "Could not authenticate";
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String jwtToken = jwtUtils.generateTokenFromUsername(userDetails);

        return jwtToken;
    }
}
