package com.banghyang.object.note.repository;

import com.banghyang.object.note.entity.SingleNote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SingleNoteRepository extends JpaRepository<SingleNote, Long> {
}
