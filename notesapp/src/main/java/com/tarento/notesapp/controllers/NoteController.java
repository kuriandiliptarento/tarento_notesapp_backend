package com.tarento.notesapp.controllers;

import com.tarento.notesapp.entity.Note; 
import com.tarento.notesapp.service.NoteService; // Use the service layer!
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse; // Import this
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notes")
@Tag(name = "Notes Management", description = "API for handling note CRUD operations (Content is Markdown).")
public class NoteController {

    private final NoteService noteService; // Use the service instead of repository

    @Autowired
    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    // --- POST /api/v1/notes (Create Note) ---
    @Operation(summary = "Create a new note for the authenticated user")
    @ApiResponse(responseCode = "201", description = "Note successfully created and saved.")
    @ApiResponse(responseCode = "400", description = "Invalid input or missing user/content.")
    @PostMapping
    public ResponseEntity<Note> createNote(@RequestBody Note newNote) {
        if (newNote.getUserId() == null || newNote.getContent() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
        Note savedNote = noteService.createNote(newNote); // Call service method
        return new ResponseEntity<>(savedNote, HttpStatus.CREATED);
    }
    
    // --- GET /api/v1/notes/user/{userId} (Get All Notes for User) ---
    @Operation(summary = "Retrieve all notes (Markdown content) for a specific user")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of notes.")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Note>> getAllNotesByUser(@PathVariable Long userId) {
        List<Note> notes = noteService.getNotesByUserId(userId); // Call service method
        return ResponseEntity.ok(notes);
    }
    
    // --- PUT /api/v1/notes/{id} (Update Note) ---
    @Operation(summary = "Update an existing note's title or Markdown content")
    @ApiResponse(responseCode = "200", description = "Note successfully updated.")
    @ApiResponse(responseCode = "404", description = "Note not found with the given ID.")
    @PutMapping("/{id}")
    public ResponseEntity<Note> updateNote(@PathVariable Long id, @RequestBody Note updatedNoteDetails) {
        
        return noteService.updateNote(id, updatedNoteDetails) // Call service method
                .map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    // --- DELETE /api/v1/notes/{id} (Delete Note) ---
    @Operation(summary = "Delete a note by its ID")
    @ApiResponse(responseCode = "204", description = "Note successfully deleted.")
    @ApiResponse(responseCode = "404", description = "Note not found.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id) {
        boolean deleted = noteService.deleteNote(id); // Call service method
        
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}