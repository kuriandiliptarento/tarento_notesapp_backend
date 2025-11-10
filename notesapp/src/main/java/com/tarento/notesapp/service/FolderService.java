package com.tarento.notesapp.service;

import com.tarento.notesapp.entity.Folder;
import com.tarento.notesapp.repository.FolderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FolderService {

    private final FolderRepository folderRepository;

    /**
     * Creates a new folder. Includes logic to link to a parent folder if provided.
     */
    @Transactional
    public Folder createFolder(Folder folder) {
        // Validation: Check if the folder name is unique within its scope (parent/root)
        // (Simplified check for root level in this example)
        if (folder.getParentFolder() == null && 
            folderRepository.existsByUserIdAndNameAndParentFolderIsNull(folder.getUserId(), folder.getName())) {
            throw new IllegalArgumentException("Root folder name already exists for this user.");
        }
        
        return folderRepository.save(folder);
    }
    
    /**
     * Gets all root-level folders for a user.
     */
    public List<Folder> getRootFoldersByUserId(Long userId) {
        return folderRepository.findByUserIdAndParentFolderIsNull(userId);
    }
    
    /**
     * Gets a single folder by ID.
     */
    public Optional<Folder> getFolderById(Long folderId) {
        return folderRepository.findById(folderId);
    }

    /**
     * Updates folder details (name and/or parent).
     */
    @Transactional
    public Optional<Folder> updateFolder(Long folderId, Folder folderDetails) {
        return folderRepository.findById(folderId).map(existingFolder -> {
            
            if (folderDetails.getName() != null) {
                existingFolder.setName(folderDetails.getName());
            }
            // Logic to handle changing the parent folder (moving the folder)
            if (folderDetails.getParentFolder() != null) {
                existingFolder.setParentFolder(folderDetails.getParentFolder());
            }
            
            return folderRepository.save(existingFolder);
        });
    }

    /**
     * Deletes a folder and all its contents/sub-folders (due to CascadeType.ALL).
     */
    @Transactional
    public boolean deleteFolder(Long folderId) {
        if (folderRepository.existsById(folderId)) {
            folderRepository.deleteById(folderId);
            return true;
        }
        return false;
    }
}