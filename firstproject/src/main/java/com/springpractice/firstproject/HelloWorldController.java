package com.springpractice.firstproject;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {

    // Open 'http://localhost:8080/hello' and you will see "Hello World" there
    @GetMapping(path  ="/hello")
    public String helloWorld(){
        return "Hello World !";
    }
}
