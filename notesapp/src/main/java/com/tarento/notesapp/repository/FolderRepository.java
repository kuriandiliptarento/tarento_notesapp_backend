package com.tarento.notesapp.repository;

import com.tarento.notesapp.entity.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {

    // Find all root folders for a user (where parent_id is NULL)
    List<Folder> findByUserIdAndParentFolderIsNull(Long userId);

    // Find all sub-folders of a specific parent folder
    List<Folder> findByParentFolderId(Long parentFolderId);
    
    // Check for uniqueness of folder name within a user's top-level or specific parent folder
    boolean existsByUserIdAndNameAndParentFolderIsNull(Long userId, String name);
}