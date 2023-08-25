package com.example.springsecurityclient.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.springsecurityclient.entity.PasswordResetToken;
import com.example.springsecurityclient.entity.User;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    PasswordResetToken findByToken(String token);

    PasswordResetToken findByUser(User user);

    // @Modifying
    // @Query("SELECT p FROM password_reset_token p WHERE p.user_id=?1")
    // PasswordResetToken findByIdUser(Long id);

    // @Query("SELECT p FROM password_reset_token p WHERE p.user_id=?4")
    // Optional<PasswordResetToken> findByIdUser(Long id);

    // @Modifying
    // @Query("DELETE FROM PasswordResetToken p WHERE p.user_id=:user_id")
    // void deleteByIdUser(@Param("user_id") Long user_id);

}
