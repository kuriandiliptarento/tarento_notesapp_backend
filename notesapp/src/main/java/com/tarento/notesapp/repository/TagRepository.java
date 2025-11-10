package com.tarento.notesapp.repository;

import com.tarento.notesapp.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    // Retrieve all tags created by a specific user. This is fast due to the user_id column.
    List<Tag> findByUserId(Long userId);

    // Find a specific tag by name and user ID (used for preventing duplicate tags by a user)
    Optional<Tag> findByUserIdAndNameIgnoreCase(Long userId, String name);

    // Check if a tag with a specific name already exists for a user
    boolean existsByUserIdAndNameIgnoreCase(Long userId, String name);
}