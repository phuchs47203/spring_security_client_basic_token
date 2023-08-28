package com.example.springsecurityclient.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.springsecurityclient.entity.User;
import com.example.springsecurityclient.entity.VerificationToken;
import com.example.springsecurityclient.model.UserModel;

public interface UserService {

    User registerUser(UserModel userModel);

    void saveVerificationTokenForUser(String token, User user);

    String validateVerifiationToken(String token);

    VerificationToken generateNewVerificationToken(String oldToken, String email);

    User findUserByEmail(String email);

    void createPasswordResetTokenForUser(User user, String token);

    String validatePasswordResetToken(String token);

    Optional<User> getUserByPasswordResetToken(String token);

    void changePassword(User user, String newPassword);

    boolean checkIfValidOldPassword(User user, String oldPassword);

    boolean EmailExists(String email);

    VerificationToken findVerificationTokenByuser(User user);

    List<User> getAllUser();

    Date setExpirationTimeOfSession(User user);

    boolean checkExpirationTimeOfSession(Date expirationTimeOfSession);

}
