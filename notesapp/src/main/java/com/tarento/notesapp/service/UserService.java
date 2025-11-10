package com.tarento.notesapp.service;

import com.tarento.notesapp.entity.User;
import com.tarento.notesapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
// import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    /**
     * Registers a new user after checking for unique username/email.
     */
    @Transactional
    public User registerNewUser(User user) {
        // Business Logic/Validation: Check for existing email (and username)
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email is already in use.");
        }
        
        // NOTE: In production, the password would be hashed here (e.g., using BCrypt).
        
        user.setCreatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }

    /**
     * Finds a user by ID.
     */
    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }
    
    /**
     * Finds a user by username (used for login/authentication).
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    /**
     * Authenticates a user based on username and password.
     * NOTE: This is a DUMMY authentication for simple user management.
     * In production, the password should be compared against a hashed value.
     */
    @Transactional(readOnly = true)
    public Optional<User> authenticate(String username, String rawPassword) {
        // 1. Find the user by username
        return userRepository.findByUsername(username)
                .filter(user -> {
                    // 2. Compare the raw password (DUMMY CHECK)
                    // **In production, this would be: BCrypt.checkpw(rawPassword, user.getPassword())**
                    return user.getPassword().equals(rawPassword);
                });
    }
}