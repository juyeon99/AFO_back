package com.banghyang.object.note.repository;

import com.banghyang.object.note.entity.BaseNote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BaseNoteRepository extends JpaRepository<BaseNote, Long> {
    BaseNote findByPerfumeId(Long perfumeId);
}
