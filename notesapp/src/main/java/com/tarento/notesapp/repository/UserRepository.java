package com.tarento.notesapp.repository;

import com.tarento.notesapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Custom method for finding a user during the login process
    Optional<User> findByUsername(String username);

    // Custom method to check if a user with a given email already exists
    boolean existsByEmail(String email);
}