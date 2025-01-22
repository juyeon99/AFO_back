package com.banghyang.object.note.entity;

import com.banghyang.common.type.NoteType;
import com.banghyang.object.noteSpice.entity.NoteSpice;
import com.banghyang.object.perfume.entity.Perfume;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private NoteType noteType;

    @ManyToOne
    @JoinColumn(name = "perfume_id", nullable = false)
    private Perfume perfume;

    @ManyToOne
    @JoinColumn(name = "note_spice_id", nullable = false)
    private NoteSpice noteSpice;

    @Builder
    public Note(NoteType noteType, Perfume perfume, NoteSpice noteSpice) {
        this.noteType = noteType;
        this.perfume = perfume;
    }
}
