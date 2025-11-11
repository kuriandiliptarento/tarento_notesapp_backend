package com.tarento.notesapp.entity;

import jakarta.persistence.*;
// import lombok.Datae
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

// import io.swagger.v3.oas.annotations.media.Schema;

// @Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "folders")
public class Folder {

    // @Schema(description = "Auto Generated Folder ID", accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Foreign Key to the User table (ensures folder ownership)
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "name", nullable = false)
    private String name;

    // --- Self-Referencing Relationship (for nested folders) ---
    // Many folders can have one parent folder
    @JsonIgnore //this is to not create a self referencing loop b/w parent and subfolder
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id") // Column in the 'folders' table pointing to its parent folder's ID
    private Folder parentFolder;

    // One folder can have many sub-folders
    @OneToMany(mappedBy = "parentFolder", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Folder> subFolders;
    
    // One folder can contain many notes
    // @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL, orphanRemoval = true)
    // private Set<Note> notes; 

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}