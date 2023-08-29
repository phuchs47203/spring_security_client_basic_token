package com.example.springsecurityclient.config;

import org.springframework.boot.autoconfigure.security.reactive.PathRequest;
import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private static final String[] WHITE_LIST_URLS = {
            "/hello",
            "/register",
            "/verifyRegistration*",
            "/resendVerifyToken*",
            "/resetPassword",
            "/savePassword",
            "/login",
            "/home",
            "/authToken",
            "/signin"
    };

    @Bean
    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(11);
    }

    /////
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    //
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors()
                .and()
                .csrf()
                .disable()
                .authorizeRequests()
                .requestMatchers(WHITE_LIST_URLS).permitAll()
                .requestMatchers("/admin/{username}").hasAnyAuthority("ADMIN")
                .requestMatchers("/admin/{username}/register").hasAnyAuthority("ADMIN")
                .requestMatchers("/admin/{username}/addPost").hasAnyAuthority("ADMIN")
                .requestMatchers("/admin/{username}/all").hasAnyAuthority("ADMIN")
                .requestMatchers("/admin/{username}/resetPassword").hasAnyAuthority("ADMIN")
                .requestMatchers("/admin/{username}/changePassword").hasAnyAuthority("ADMIN")
                .requestMatchers("/user/{username}").hasAnyAuthority("USER")
                .requestMatchers("/user/{username}/editPost").hasAnyAuthority("USER")
                .requestMatchers("/user/{username}/addPost").hasAnyAuthority("USER")
                .requestMatchers("/user/{username}/addNewFriend").hasAnyAuthority("USER")
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic()
                .and()
                .formLogin()
                .defaultSuccessUrl("/login-success")
                .and()
                .rememberMe()
                .tokenValiditySeconds(10)
                .rememberMeParameter("remember-me")
                .key("uniqueAndSecret");
        //
        //
        /// gsgsdh
        // gdsg/
        ///
        //
        //
        return http.build();

    }
    // .cors()
    // .and()
    // .csrf()
    // .disable()
    // .httpBasic().and()
    // .authorizeRequests()
    // .requestMatchers(WHITE_LIST_URLS).permitAll();
}
