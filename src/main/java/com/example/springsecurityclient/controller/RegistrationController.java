package com.example.springsecurityclient.controller;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.springsecurityclient.entity.User;
import com.example.springsecurityclient.entity.VerificationToken;
import com.example.springsecurityclient.event.RegistrationCompleteEvent;
import com.example.springsecurityclient.model.LoginRequest;
import com.example.springsecurityclient.model.PasswordModel;
import com.example.springsecurityclient.model.UserModel;
import com.example.springsecurityclient.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.server.PathParam;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@CrossOrigin("http://localhost:3000/")
public class RegistrationController {

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/authToken")
    public String getAuthToken() {

        return "authToken";
    }

    @PostMapping("/signin")
    public ResponseEntity<String> authenticateUser(
            @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager
                    .authenticate(
                            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                                    loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            User user = userService.findUserByEmail(loginRequest.getUsername());
            userService.setExpirationTimeOfSession(user);
            return new ResponseEntity<>("Login Successfully", HttpStatus.OK);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>("Login Failed", HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/all")
    public List<User> getAll() {
        return userService.getAllUser();
    }

    @GetMapping("/home")
    public String home() {
        return "this is home page";
    }

    @GetMapping("/login-success")
    public String loginSuccess(Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findUserByEmail(username);
        userService.setExpirationTimeOfSession(user);
        return username + "     " + user.getExpirationTimeOfSession();
    }

    // @PostMapping("/login")
    // public String login(
    // @RequestParam("username") String username,
    // @RequestParam("password") String password,
    // HttpSession session) {
    // // Xác minh thông tin đăng nhập
    // // ...
    // // Lưu thông tin đăng nhập vào session và thiết lập thời gian hết hạn
    // session.setAttribute("username", username);
    // session.setAttribute("expirationTime", System.currentTimeMillis() + (10 * 60
    // * 1000)); // Hết hạn sau 10 phút
    // return "Login successful";
    // }

    @PostMapping("/register")
    public String registerUser(@RequestBody UserModel userModel, final HttpServletRequest request) {

        // Email of User is unique
        if (userService.EmailExists(userModel.getEmail())) {
            return "Email address already exists";
        }

        User user = userService.registerUser(userModel);
        publisher.publishEvent(new RegistrationCompleteEvent(
                user,
                applicationUrl(request)));
        VerificationToken verificcVerificationToken = userService.findVerificationTokenByuser(user);
        String url = "http://localhost:8080"
                + "/verifyRegistration?token="
                + verificcVerificationToken.getToken();
        return "Success " + "\nClick to verification for your account:  " + url;
    }

    @GetMapping("/resendVerifyToken")
    public String resendVerificationToken(
            @RequestParam("token") String oldToken,
            @RequestParam("email") String email,
            HttpServletRequest request) {

        VerificationToken verificationToken = userService.generateNewVerificationToken(oldToken, email);
        User user = verificationToken.getUser();

        resendVerificationTokenMail(user, applicationUrl(request), verificationToken);
        String url = "http://localhost:8080"
                + "/verifyRegistration?token="
                + verificationToken.getToken();
        return "Verification link sent" + "\nClick to verification for your account again:  " + url;
    }

    @PostMapping("/resetPassword")
    public String resetPassword(
            @RequestBody PasswordModel passwordModel,
            HttpServletRequest request) {

        User user = userService.findUserByEmail(passwordModel.getEmail());
        String url = "";
        if (user == null) {
            return "Email address does not exists";

        }
        if (user != null) {
            String token = UUID.randomUUID().toString();
            userService.createPasswordResetTokenForUser(user, token);
            url = passwordResetTokenMail(user, applicationUrl(request), token);
        }
        return url;
    }

    /// goirjg/grdkmrsk
    @GetMapping("/verifyRegistration")
    public String verifyRegistration(@RequestParam("token") String token) {
        String result = userService.validateVerifiationToken(token);
        if (result.equalsIgnoreCase("valid")) {
            return "User Verifies Successfully";
        }
        return "Bad User";
    }

    @PostMapping("/savePassword")
    public String savePassword(
            @RequestParam("token") String token,
            @RequestBody PasswordModel passwordModel) {
        String result = userService.validatePasswordResetToken(token);
        if (!result.equalsIgnoreCase("valid")) {
            return "Invalid Token";
        }
        Optional<User> user = userService.getUserByPasswordResetToken(token);
        if (user.isPresent()) {
            userService.changePassword(user.get(), passwordModel.getNewPassword());
            return "Password Reset Successfully";
        } else {
            return "Invalid Token";
        }
    }

    @PostMapping("/changePassword")
    public String changePassword(@RequestBody PasswordModel passwordModel) {
        User user = userService.findUserByEmail(passwordModel.getEmail());
        if (!userService.checkIfValidOldPassword(user, passwordModel.getOldPassword())) {
            return "Invalid Old Password";
        }

        // Save New password
        userService.changePassword(user, passwordModel.getNewPassword());
        return "Password Changed Successfully";
    }

    private String passwordResetTokenMail(User user, String applicationUrl, String token) {
        String url = applicationUrl
                + "/savePassword?token="
                + token;
        // send verification email()
        log.info("Click the link to Reset your Password: {}", url);
        return url;
    }

    private void resendVerificationTokenMail(User user, String applicationUrl, VerificationToken verificationToken) {
        String url = applicationUrl
                + "/verifyRegistration?token="
                + verificationToken.getToken();
        // send verification email()
        log.info("Click the link to verify your account: {}", url);
    }

    private String applicationUrl(HttpServletRequest request) {
        return "http://" +
                request.getServerName() +
                ":" +
                request.getServerPort() +
                request.getContextPath();
    }
}
