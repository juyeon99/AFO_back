package com.banghyang.object.note.repository;

import com.banghyang.object.note.entity.TopNote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopNoteRepository extends JpaRepository<TopNote, Long> {
    TopNote findByPerfumeId(Long perfumeId);
}
