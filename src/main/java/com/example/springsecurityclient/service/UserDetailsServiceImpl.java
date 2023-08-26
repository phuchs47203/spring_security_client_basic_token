package com.example.springsecurityclient.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.springsecurityclient.entity.User;
import com.example.springsecurityclient.repository.UserRepository;

@Service("userDetailsServiceImpl")
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        // Tạo đối tượng UserDetails từ đối tượng User
        // Bạn cần truyền thông tin, ví dụ như tên người dùng, mật khẩu, quyền hạn, ...
        List<SimpleGrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority(user.getRole()));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                // Cung cấp danh sách các phân quyền của người dùng
                // Ví dụ: Collections.singletonList(new SimpleGrantedAuthority(user.getRole()))
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole())));
    }
}
