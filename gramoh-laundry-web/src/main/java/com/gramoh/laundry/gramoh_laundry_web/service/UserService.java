package com.gramoh.laundry.gramoh_laundry_web.service;

import com.gramoh.laundry.gramoh_laundry_web.model.User;
import com.gramoh.laundry.gramoh_laundry_web.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(User user) {
        return userRepository.save(user);
    }

    public User login(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username) != null;
    }

    public String createRememberMeToken(User user) {
        String token = UUID.randomUUID().toString();
        user.setRememberToken(token);
        userRepository.save(user);
        return token;
    }

    public User getByRememberMeToken(String token) {
        return userRepository.findByRememberToken(token);
    }




}
