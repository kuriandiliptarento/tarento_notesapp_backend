package com.tarento.notesapp.controllers;

import com.tarento.notesapp.entity.Tag;
import com.tarento.notesapp.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tags")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Tag Management", description = "API for creating and retrieving user-specific tags.")
public class TagController {

    private final TagService tagService;

    // --- POST /api/v1/tags (Create Tag) ---
    @Operation(summary = "Create a new tag for the authenticated user")
    @ApiResponse(responseCode = "201", description = "Tag created successfully.")
    @ApiResponse(responseCode = "400", description = "Invalid input or tag name already exists for user.")
    @PostMapping
    public ResponseEntity<?> createTag(@RequestBody Tag newTag) {
        if (newTag.getUserId() == null || newTag.getName() == null) {
            return new ResponseEntity<>("User ID and Name are required.", HttpStatus.BAD_REQUEST);
        }
        try {
            Tag savedTag = tagService.createTag(newTag);
            return new ResponseEntity<>(savedTag, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // --- GET /api/v1/tags/user/{userId} (Get All Tags for User) ---
    @Operation(summary = "Get all tags created by a specific user")
    @ApiResponse(responseCode = "200", description = "Tags retrieved successfully.")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Tag>> getAllTagsByUser(@PathVariable Long userId) {
        List<Tag> tags = tagService.getAllTagsByUserId(userId);
        return ResponseEntity.ok(tags);
    }
    
    // --- DELETE /api/v1/tags/{id} (Delete Tag) ---
    @Operation(summary = "Delete a tag by its ID")
    @ApiResponse(responseCode = "204", description = "Tag successfully deleted.")
    @ApiResponse(responseCode = "404", description = "Tag not found.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
        if (tagService.deleteTag(id)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}