package com.tarento.notesapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

// import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "notes")
public class Note {

    @Schema(description = "Auto Generated Note ID", accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId; // Foreign Key to the User table

    private String title;
    
    @Lob // Use @Lob for large text fields like note content
    @Column(name = "content", nullable = false)
    private String content;

    // @Column(name = "folder_id")
    // private long folderId;

    // --- Relationship to Folder (Many-to-One) ---
    // Many notes can belong to one folder
    @ManyToOne(fetch = FetchType.LAZY)
    // @ManyToOne
    @JoinColumn(name = "folder_id") // This column holds the ID of the parent folder
    // @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Folder folder;

    // --- Relationship to Tags (Many-to-Many) ---
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "note_tags", // The junction table name
        joinColumns = @JoinColumn(name = "note_id"), // Column pointing to the Note ID
        inverseJoinColumns = @JoinColumn(name = "tag_id") // Column pointing to the Tag ID
    )
    private Set<Tag> tags = new HashSet<>();

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public void setContent(String content) {
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }

}
