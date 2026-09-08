package com.tokenization.service;

import com.tokenization.repository.UserRepository;
import com.tokenization.tokenization.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(User user){
        String encodedpassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedpassword);
        return userRepository.save(user);
    }
}
