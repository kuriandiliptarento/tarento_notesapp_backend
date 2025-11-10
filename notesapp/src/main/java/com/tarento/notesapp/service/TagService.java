package com.tarento.notesapp.service;

import com.tarento.notesapp.entity.Tag;
import com.tarento.notesapp.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
// import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagService {

    private final TagRepository tagRepository;

    /**
     * Creates a new tag, ensuring the name is unique for the user.
     */
    @Transactional
    public Tag createTag(Tag tag) {
        // Business Logic: Prevent duplicate tag names for the same user
        if (tagRepository.existsByUserIdAndNameIgnoreCase(tag.getUserId(), tag.getName())) {
            throw new IllegalArgumentException("Tag '" + tag.getName() + "' already exists for this user.");
        }
        
        return tagRepository.save(tag);
    }

    /**
     * Retrieves all tags belonging to a specific user.
     */
    public List<Tag> getAllTagsByUserId(Long userId) {
        return tagRepository.findByUserId(userId);
    }

    /**
     * Deletes a tag. Hibernate will automatically clean up entries in the note_tags junction table.
     */
    @Transactional
    public boolean deleteTag(Long tagId) {
        if (tagRepository.existsById(tagId)) {
            tagRepository.deleteById(tagId);
            return true;
        }
        return false;
    }
}