package com.tarento.notesapp.service;

import com.tarento.notesapp.entity.Note;
import com.tarento.notesapp.repository.NoteRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor // Lombok annotation to inject the final NoteRepository
@Transactional(readOnly = true) // Default all methods to read-only for safety
public class NoteService {

    // Dependency Injection via Lombok's @RequiredArgsConstructor (fields must be final)
    private final NoteRepository noteRepository;

    /**
     * Retrieves all notes belonging to a specific user.
     * @param userId The ID of the owner.
     * @return A list of notes.
     */
    public List<Note> getNotesByUserId(Long userId) {
        return noteRepository.findByUserId(userId);
    }

    /**
     * Retrieves a single note by its ID.
     * @param noteId The ID of the note.
     * @return An Optional containing the Note, or empty if not found.
     */
    public Optional<Note> getNoteById(Long noteId) {
        return noteRepository.findById(noteId);
    }

    /**
     * Creates and saves a new note.
     * NOTE: In a complete application, this method would also handle validation
     * and linking to Folder and Tags entities.
     * @param note The Note object to save.
     * @return The saved Note object.
     */
    @Transactional // Override read-only for write operations
    public Note createNote(Note note) {
        // Business Logic Example: Ensure timestamps are set correctly on creation
        note.setCreatedAt(LocalDateTime.now());
        note.setUpdatedAt(LocalDateTime.now());
        
        // You might add logic here to sanitize content, validate markdown, etc.

        return noteRepository.save(note);
    }

    /**
     * Updates an existing note.
     * @param noteId The ID of the note to update.
     * @param noteDetails The Note object containing updated details.
     * @return The updated Note object, or null if not found.
     */
    @Transactional
    public Optional<Note> updateNote(Long noteId, Note noteDetails) {
        
        return noteRepository.findById(noteId).map(existingNote -> {
            
            // 1. Apply updates from the request details
            existingNote.setTitle(noteDetails.getTitle());
            existingNote.setContent(noteDetails.getContent());
            
            // NOTE: Once Folder and Tags are implemented, update logic goes here:
            // existingNote.setFolder(noteDetails.getFolder());
            // existingNote.setTags(noteDetails.getTags());
            
            // 2. Set updated timestamp
            existingNote.setUpdatedAt(LocalDateTime.now());
            
            // 3. Save the updated entity
            return noteRepository.save(existingNote);
            
        });
    }

    /**
     * Deletes a note by its ID.
     * @param noteId The ID of the note to delete.
     * @return True if deletion was successful, false otherwise.
     */
    @Transactional
    public boolean deleteNote(Long noteId) {
        if (noteRepository.existsById(noteId)) {
            noteRepository.deleteById(noteId);
            return true;
        }
        return false;
    }
}