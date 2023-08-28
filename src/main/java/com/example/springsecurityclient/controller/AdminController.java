package com.example.springsecurityclient.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.springsecurityclient.entity.User;
import com.example.springsecurityclient.service.UserService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class AdminController {
    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationEventPublisher publisher;

    @GetMapping("/admin/{username}/all")
    public List<User> getAllByAdmin(@PathVariable("username") String username) {
        User user = userService.findUserByEmail(username);
        if (user.getRole().equals("USER")) {
            return Collections.emptyList();
        }
        if (userService.checkExpirationTimeOfSession(user.getExpirationTimeOfSession())) {
            return Collections.emptyList();
        }
        return userService.getAllUser();
    }

    @GetMapping("/admin/{username}/register")
    public String adminRegister(@PathVariable("username") String username) {
        return "AMIN" + username + "\n" + "REGISTER";
    }

    @GetMapping("/admin/{username}/resetPassword")
    public String adminResetPassword(@PathVariable("username") String username) {
        return "AMIN" + username + "\n" + "RESET PASWORD";
    }

    @GetMapping("/admin/{username}/changePassword")
    public String adminChangePassword(@PathVariable("username") String username) {
        return "AMIN" + username + "\n" + "CHANGE PASWORD";
    }

    @GetMapping("/admin")
    public String admin() {
        return "this is Admine page";
    }

    @GetMapping("/admin/{username}")
    public String homePageAdmin(@PathVariable("username") String username) {
        // Thực hiện các xử lý liên quan đến trang home
        return "home of role ADMIN " + username;
    }
}
