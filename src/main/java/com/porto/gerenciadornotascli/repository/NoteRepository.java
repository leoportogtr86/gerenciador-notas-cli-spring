package com.porto.gerenciadornotascli.repository;

import com.porto.gerenciadornotascli.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note, Long> {
}
