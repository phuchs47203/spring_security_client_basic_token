package com.example.springsecurityclient.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@CrossOrigin("http://localhost:3000/")

public class HelloController {
    @GetMapping("/hello")
    public String hello() {
        return "hello, Phuc handsome";
    }

}
