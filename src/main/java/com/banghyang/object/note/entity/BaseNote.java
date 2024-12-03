package com.banghyang.object.note.entity;

import com.banghyang.object.perfume.entity.Perfume;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BaseNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String spices;

    @OneToOne
    @JoinColumn(name = "perfume_id", unique = true, nullable = false)
    @JsonIgnore
    private Perfume perfume;

    @Builder(toBuilder = true)
    public BaseNote(Long id, String spices, Perfume perfume) {
        this.id = id;
        this.spices = spices;
        this.perfume = perfume;
    }

    public BaseNote modify(BaseNote modifyBaseNoteEntity) {
        this.spices = modifyBaseNoteEntity.getSpices();
        this.perfume = modifyBaseNoteEntity.getPerfume();
        return this;
    }
}
