package com.tarento.notesapp.controllers;

import com.tarento.notesapp.entity.Folder;
import com.tarento.notesapp.service.FolderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/folders")
@RequiredArgsConstructor
@Tag(name = "Folder Management", description = "API for creating and managing hierarchical note folders.")
public class FolderController {

    private final FolderService folderService;

    // --- POST /api/v1/folders (Create Folder) ---
    @Operation(summary = "Create a new folder, optionally assigning a parent folder")
    @ApiResponse(responseCode = "201", description = "Folder created successfully.")
    @ApiResponse(responseCode = "400", description = "Invalid input or folder name conflict.")
    @PostMapping
    public ResponseEntity<?> createFolder(@RequestBody Folder newFolder) {
        if (newFolder.getUserId() == null || newFolder.getName() == null) {
            return new ResponseEntity<>("User ID and Name are required.", HttpStatus.BAD_REQUEST);
        }
        try {
            Folder savedFolder = folderService.createFolder(newFolder);
            return new ResponseEntity<>(savedFolder, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // --- GET /api/v1/folders/user/{userId} (Get Root Folders) ---
    @Operation(summary = "Get all root-level folders for a specific user")
    @ApiResponse(responseCode = "200", description = "Root folders retrieved.")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Folder>> getRootFolders(@PathVariable Long userId) {
        List<Folder> folders = folderService.getRootFoldersByUserId(userId);
        return ResponseEntity.ok(folders);
    }
    
    // --- PUT /api/v1/folders/{id} (Update Folder Name/Parent) ---
    @Operation(summary = "Update a folder (e.g., rename or move to a different parent)")
    @ApiResponse(responseCode = "200", description = "Folder updated successfully.")
    @ApiResponse(responseCode = "404", description = "Folder not found.")
    @PutMapping("/{id}")
    public ResponseEntity<Folder> updateFolder(@PathVariable Long id, @RequestBody Folder folderDetails) {
        return folderService.updateFolder(id, folderDetails)
                .map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    // --- DELETE /api/v1/folders/{id} (Delete Folder) ---
    @Operation(summary = "Delete a folder (and its sub-folders/notes if cascaded)")
    @ApiResponse(responseCode = "204", description = "Folder successfully deleted.")
    @ApiResponse(responseCode = "404", description = "Folder not found.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFolder(@PathVariable Long id) {
        if (folderService.deleteFolder(id)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}