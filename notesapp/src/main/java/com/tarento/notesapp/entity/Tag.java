package com.tarento.notesapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

// import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tags", 
       uniqueConstraints = { 
           // Ensures a user cannot have two tags with the exact same name
           @UniqueConstraint(columnNames = {"user_id", "name"})
       })
public class Tag {

    // @Schema(description = "Auto Generated Tag ID", accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Foreign Key to the User table (ensures tag ownership)
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "name", nullable = false)
    private String name;

    // --- Many-to-Many Relationship to Notes ---
    // 'mappedBy' indicates that the 'Note' entity manages the relationship via the 'note_tags' table
    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    private Set<Note> notes;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}