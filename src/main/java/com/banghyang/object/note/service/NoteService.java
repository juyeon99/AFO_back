package com.banghyang.object.note.service;

import com.banghyang.common.type.NoteType;
import com.banghyang.object.note.entity.Note;
import com.banghyang.object.note.repository.NoteRepository;
import com.banghyang.object.product.entity.Product;
import com.banghyang.object.spice.entity.Spice;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;

    /**
     * 새로운 노트 생성 메소드
     */
    public void createNote(Product product, Spice spice, NoteType noteType) {
        Note newNoteEntity = Note.builder()
                .noteType(noteType)
                .product(product)
                .spice(spice)
                .build();
        noteRepository.save(newNoteEntity);
    }

    /**
     * 제품에 해당하는 모든 노트 반환 메소드
     */
    public List<Note> findNoteByProduct(Product product) {
        return noteRepository.findByProduct(product);
    }

    /**
     * 매개변수로 넘어온 모든 노트 엔티티 리스트의 모든 노트들을 삭제하는 메소드
     */
    public void deleteAll(List<Note> noteEntityList) {
        noteRepository.deleteAll(noteEntityList);
    }
}
