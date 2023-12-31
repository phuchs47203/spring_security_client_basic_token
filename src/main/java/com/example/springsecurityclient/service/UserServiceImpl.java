package com.example.springsecurityclient.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.springsecurityclient.entity.PasswordResetToken;
import com.example.springsecurityclient.entity.User;
import com.example.springsecurityclient.entity.VerificationToken;
import com.example.springsecurityclient.model.UserModel;
import com.example.springsecurityclient.repository.PasswordResetTokenRepository;
import com.example.springsecurityclient.repository.UserRepository;
import com.example.springsecurityclient.repository.VerificationTokenRepository;

import jakarta.transaction.Transactional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User registerUser(UserModel userModel) {
        User user = new User();
        user.setEmail((userModel.getEmail()));
        user.setFirstName(userModel.getFirstName());
        user.setLastName((userModel.getLastName()));
        user.setRole("USER");
        user.setPassword(passwordEncoder.encode(userModel.getPassword()));

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.add(Calendar.MINUTE, 10);
        user.setExpirationTimeOfSession(new Date(calendar.getTime().getTime()));

        userRepository.save(user);

        return user;
    }

    @Override
    public Date setExpirationTimeOfSession(User user) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.add(Calendar.MINUTE, 10);
        user.setExpirationTimeOfSession(new Date(calendar.getTime().getTime()));

        userRepository.save(user);
        return new Date(calendar.getTime().getTime());

    }

    @Override
    public void saveVerificationTokenForUser(String token, User user) {
        VerificationToken verificationToken = new VerificationToken(user, token);

        verificationTokenRepository.save(verificationToken);
    }

    @Override
    public String validateVerifiationToken(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
        if (verificationToken == null) {
            return "invalid";
        }
        User user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();

        if ((verificationToken.getExpirationTime().getTime()
                - cal.getTime().getTime()) <= 0) {
            verificationTokenRepository.delete(verificationToken);
            return "expired";
        }
        user.setEnabled(true);
        userRepository.save(user);
        return "valid";

    }
    ///
    /// fisjfi

    @Override
    public VerificationToken generateNewVerificationToken(String oldToken, String email) {
        User user = userRepository.findByEmail(email);
        VerificationToken verificationToken = verificationTokenRepository.findByToken(oldToken);
        VerificationToken verificationTokenEmail = verificationTokenRepository.findByUser(user);
        if (verificationToken == null && verificationTokenEmail == null) {
            VerificationToken newVerificationToken = new VerificationToken(user, UUID.randomUUID().toString());
            verificationTokenRepository.save(newVerificationToken);
            return newVerificationToken;
        } else {
            if (verificationToken != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(new Date().getTime());
                calendar.add(Calendar.MINUTE, 10);

                verificationToken.setExpirationTime(new Date(calendar.getTime().getTime()));
                verificationToken.setToken(UUID.randomUUID().toString());
                verificationTokenRepository.save(verificationToken);

                return verificationToken;
            }
            if (verificationTokenEmail != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(new Date().getTime());
                calendar.add(Calendar.MINUTE, 10);

                verificationTokenEmail.setExpirationTime(new Date(calendar.getTime().getTime()));
                verificationTokenEmail.setToken(UUID.randomUUID().toString());
                verificationTokenRepository.save(verificationTokenEmail);

                return verificationTokenEmail;
            }
        }
        return null;

    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public void createPasswordResetTokenForUser(User user, String token) {
        // passwordResetTokenRepository.deleteByIdUser(user.getId());
        // passwordResetTokenRepository.deleteAll();
        PasswordResetToken oldPasswordResetToken = passwordResetTokenRepository.findByUser(user);
        if (oldPasswordResetToken != null) {
            // set time is very importtant
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(new Date().getTime());
            calendar.add(Calendar.MINUTE, 10);

            oldPasswordResetToken.setToken(token);
            oldPasswordResetToken.setExpirationTime(new Date(calendar.getTime().getTime()));
            passwordResetTokenRepository.save(oldPasswordResetToken);
        } else {
            PasswordResetToken passwordResetToken = new PasswordResetToken(user, token);
            passwordResetTokenRepository.save(passwordResetToken);
        }

        // Optional<PasswordResetToken> OldpasswordResetToken =
        // passwordResetTokenRepository
        // .findByIdUser(user.getId());

        // if (OldpasswordResetToken.isPresent()) {
        // throw new IllegalStateException(
        // "User Token with id " + user.getId() + " does not exists");
        // }
        // else{

        // }
    }

    @Override
    public String validatePasswordResetToken(String token) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);
        if (passwordResetToken == null) {
            return "invalid";
        }
        User user = passwordResetToken.getUser();
        Calendar cal = Calendar.getInstance();

        if ((passwordResetToken.getExpirationTime().getTime()
                - cal.getTime().getTime()) <= 0) {
            passwordResetTokenRepository.delete(passwordResetToken);
            return "expired";
        }

        return "valid";
    }

    @Override
    public Optional<User> getUserByPasswordResetToken(String token) {
        return Optional.ofNullable(passwordResetTokenRepository.findByToken(token).getUser());
    }

    @Override
    public void changePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

    }

    @Override
    public boolean checkIfValidOldPassword(User user, String oldPassword) {
        return passwordEncoder.matches(oldPassword, user.getPassword());
    }

    @Override
    public boolean EmailExists(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return false;
        } else {
            return true;

        }
    }

    @Override
    public VerificationToken findVerificationTokenByuser(User user) {
        VerificationToken verificationToken = verificationTokenRepository.findByUser(user);
        return verificationToken;
    }

    @Override
    public List<User> getAllUser() {
        return userRepository.findAll();

    }

    @Override
    public boolean checkExpirationTimeOfSession(Date expirationTimeOfSession) {
        if ((expirationTimeOfSession.getTime()
                - Calendar.getInstance().getTime().getTime()) <= 0) {
            return true;
        }
        return false;
    }

}
