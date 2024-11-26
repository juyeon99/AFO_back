package com.banghyang.object.note.repository;

import com.banghyang.object.note.entity.MiddleNote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MiddleNoteRepository extends JpaRepository<MiddleNote, Long> {
    MiddleNote findByPerfumeId(Long perfumeId);
}
