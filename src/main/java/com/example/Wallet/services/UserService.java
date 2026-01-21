package com.example.Wallet.services;

import org.springframework.stereotype.Service;
import com.example.Wallet.repositories.UserRepository;
import java.util.List;


import com.example.Wallet.entities.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public User createUser(User user) {
        log.info("Creating user: {}", user.getEmail());
        User newUser = userRepository.save(user);
        log.info("User created with id: {} in database: {}", newUser.getId(), (newUser.getId() % 2 + 1));
        return newUser;
    }

    public User getUserById(Long id) {
        log.info("Fetching user with id: {}", id);
        return userRepository.findById(id).orElse(null);
    }
    public List<User> getUsersByName(String name) {
        log.info("Fetching users with name: {}", name);
        return userRepository.findByNameContainingIgnoreCase(name);
    }

}
