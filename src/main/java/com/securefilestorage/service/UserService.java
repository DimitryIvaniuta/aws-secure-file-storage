package com.securefilestorage.service;

import com.securefilestorage.model.User;
import com.securefilestorage.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service class for managing user operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Registers a new user.
     *
     * @param user User object with registration details.
     * @return The registered user.
     */
    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        log.info("User registered with login: {}", savedUser.getLogin());
        return savedUser;
    }

    /**
     * Finds a user by login.
     *
     * @param username The login to search.
     * @return Optional containing the user if found.
     */
    public Optional<User> findByLogin(String username) {
        return userRepository.findByLogin(username);
    }

    /**
     * Deletes a user by ID.
     *
     * @param userId ID of the user to delete.
     */
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
        log.info("User with ID {} deleted", userId);
    }
}