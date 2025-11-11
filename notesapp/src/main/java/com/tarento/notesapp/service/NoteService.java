package com.tarento.notesapp.service;

import com.tarento.notesapp.entity.Note;
import com.tarento.notesapp.repository.NoteRepository;
import com.tarento.notesapp.entity.Tag;
// import com.tarento.notesapp.entity.User;
import com.tarento.notesapp.entity.Folder;
import com.tarento.notesapp.repository.FolderRepository; // NEW
import com.tarento.notesapp.repository.TagRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor // Lombok annotation to inject the final NoteRepository
@Transactional(readOnly = true) // Default all methods to read-only for safety
public class NoteService {

    // Dependency Injection via Lombok's @RequiredArgsConstructor (fields must be final)
    private final NoteRepository noteRepository;
    private final FolderRepository folderRepository;
    private final TagRepository tagRepository;

    @Transactional(readOnly = true)
    public List<Note> findAllNotes() {
        return noteRepository.findAll();
    }

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

        // --- 1. Link Folder ---
        if (note.getFolder() != null && note.getFolder().getId() != null) {
            Folder folder = folderRepository.findById(note.getFolder().getId())
                .orElseThrow(() -> new IllegalArgumentException("Folder not found with ID: " + note.getFolder().getId()));
            note.setFolder(folder);
        } else {
            note.setFolder(null); // Ensure unlinked if no ID is provided
        }

        // --- 2. Link Tags ---
        if (note.getTags() != null && !note.getTags().isEmpty()) {
            Set<Long> tagIds = note.getTags().stream()
                                  .map(Tag::getId)
                                  .collect(Collectors.toSet());
            
            List<Tag> foundTags = tagRepository.findAllById(tagIds);
            
            if (foundTags.size() != tagIds.size()) {
                // Find which ID was missing for a better error message
                String missingIds = tagIds.stream()
                                          .filter(id -> foundTags.stream().noneMatch(t -> t.getId().equals(id)))
                                          .map(Object::toString)
                                          .collect(Collectors.joining(", "));
                throw new IllegalArgumentException("One or more tags not found with IDs: " + missingIds);
            }
            note.setTags(new HashSet<>(foundTags));
        } else {
            note.setTags(new HashSet<>());
        }
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
            
            // 1. Update basic fields (using entity setter for content to update timestamp)
            if (noteDetails.getTitle() != null) {
                existingNote.setTitle(noteDetails.getTitle());
            }
            if (noteDetails.getContent() != null) {
                // This calls the custom setter in Note.java which updates updatedAt
                existingNote.setContent(noteDetails.getContent()); 
            }
            
            // --- 2. Update Folder Link ---
            // A null Folder object means remove the link (root folder)
            if (noteDetails.getFolder() != null) {
                 if (noteDetails.getFolder().getId() != null) {
                    Folder folder = folderRepository.findById(noteDetails.getFolder().getId())
                        .orElseThrow(() -> new IllegalArgumentException("Folder not found with ID: " + noteDetails.getFolder().getId()));
                    existingNote.setFolder(folder);
                 } else {
                    // Set folder to null if object is present but ID is null (meaning set to root)
                    existingNote.setFolder(null);
                 }
            }

            // --- 3. Update Tags Link ---
            // If the request provides a Set of tags, replace the existing tags
            if (noteDetails.getTags() != null) {
                Set<Long> tagIds = noteDetails.getTags().stream()
                                              .map(Tag::getId)
                                              .collect(Collectors.toSet());
                
                List<Tag> foundTags = tagRepository.findAllById(tagIds);
                
                if (foundTags.size() != tagIds.size()) {
                    // Handle missing tags (similar to createNote)
                    throw new IllegalArgumentException("One or more tags not found during update.");
                }
                existingNote.setTags(new HashSet<>(foundTags));
            }
            
            // Set updated timestamp (only needed if fields without custom setters were updated)
            existingNote.setUpdatedAt(LocalDateTime.now()); 
            
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