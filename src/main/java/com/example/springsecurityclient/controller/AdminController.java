package com.example.springsecurityclient.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.springsecurityclient.entity.User;
import com.example.springsecurityclient.model.Post;
import com.example.springsecurityclient.service.UserService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@CrossOrigin("http://localhost:3000/")
public class AdminController {
    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationEventPublisher publisher;

    @GetMapping("/admin/{username}/all")
    public ResponseEntity<List<User>> getAllByAdmin(
            @PathVariable("username") String username) {
        User user = userService.findUserByEmail(username);
        if (user.getRole().equals("USER")) {
            return new ResponseEntity<List<User>>(HttpStatus.UNAUTHORIZED);
        }
        if (userService.checkExpirationTimeOfSession(user.getExpirationTimeOfSession())) {
            return new ResponseEntity<List<User>>(HttpStatus.GONE);
        }
        List<User> allUser = userService.getAllUser();
        return new ResponseEntity<>(allUser, HttpStatus.OK);
    }

    @PostMapping("/admin/{username}/addPost")
    public ResponseEntity<Post> authenticateUser(
            @PathVariable("username") String username,
            @RequestBody Post post) {
        User user = userService.findUserByEmail(username);

        if (user.getRole().equals("USER")) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    ///
    ///
    ///
    //
    //

    // editi oingvkd svosgn osgmrg gndsjog o
    /// //
    ///

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
