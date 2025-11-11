package com.tarento.notesapp.controllers;

import com.tarento.notesapp.entity.User;
import com.tarento.notesapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.tarento.notesapp.dto.LoginRequest;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "API for user registration and account retrieval.")
public class UserController {

    private final UserService userService;

    // --- GET /api/v1/users (NEW ENDPOINT) ---
    @Operation(summary = "Get a list of all registered users (Admin function)")
    @ApiResponse(responseCode = "200", description = "List of users retrieved.")
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        // Calls the new service method
        List<User> users = userService.findAllUsers();
        
        // Returns the list with a 200 OK status
        return ResponseEntity.ok(users);
    }

    // --- POST /api/v1/users/login (NEW ENDPOINT) ---
    @Operation(summary = "Authenticate user and receive user details/ID")
    @ApiResponse(responseCode = "200", description = "Login successful. Returns user details.")
    @ApiResponse(responseCode = "401", description = "Invalid username or password.")
    @PostMapping("/login")
    public ResponseEntity<User> loginUser(@RequestBody LoginRequest loginRequest) {
        
        return userService.authenticate(loginRequest.getUsername(), loginRequest.getPassword())
                .map(user -> {
                    // Success! Return the full user object (including the ID)
                    return ResponseEntity.ok(user);
                })
                .orElseGet(() -> 
                    // Failure! Invalid credentials
                    new ResponseEntity<>(HttpStatus.UNAUTHORIZED) 
                );
    }

    // --- POST /api/v1/users/register (Register New User) ---
    @Operation(summary = "Register a new user account")
    @ApiResponse(responseCode = "201", description = "User successfully registered.")
    @ApiResponse(responseCode = "400", description = "Invalid input or user already exists.")
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User newUser) {
        try {
            User registeredUser = userService.registerNewUser(newUser);
            // NOTE: Never return the password in a real response!
            return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            // Catches the exception thrown by the service if email is not unique
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // --- GET /api/v1/users/{id} (Get User Details) ---
    @Operation(summary = "Get user details by ID")
    @ApiResponse(responseCode = "200", description = "User details retrieved.")
    @ApiResponse(responseCode = "404", description = "User not found.")
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}