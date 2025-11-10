package com.tarento.notesapp.repository;

import com.tarento.notesapp.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    // Custom method to easily find all notes belonging to a specific user
    List<Note> findByUserId(Long userId);
}
