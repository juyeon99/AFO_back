package com.banghyang.object.noteSpice.entity;

import com.banghyang.object.note.entity.Note;
import com.banghyang.object.spice.entity.Spice;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NoteSpice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "noteSpice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Note> notes;

    @OneToMany(mappedBy = "noteSpice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Spice> spices;

    @Builder
    public NoteSpice(List<Note> notes, List<Spice> spices) {
        this.notes = notes;
        this.spices = spices;
    }
}
