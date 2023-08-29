package com.example.springsecurityclient.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@CrossOrigin("http://localhost:3000/")

public class UserController {
    @GetMapping("/user/{username}/editPost")
    public String adminEditPost(@PathVariable("username") String username) {
        return "USER" + username + "\n" + "You can edit POST at here";
    }

    @GetMapping("/user/{username}/addPost")
    public String adminAddPost(@PathVariable("username") String username) {
        return "USER" + username + "\n" + "You can ADD NEW POST at here";
    }

    @GetMapping("/user/{username}/addNewFriend")
    public String adminAddFriend(@PathVariable("username") String username) {
        return "USER" + username + "\n" + "You can ADD NEW FRIENDsssssss at here";
    }

    @GetMapping("/user/{username}")
    public String homePageUser(@PathVariable("username") String username) {
        // Thực hiện các xử lý liên quan đến trang home
        return "home of role USER " + username;
    }
}
