package com.banghyang.object.perfume.entity;

import com.banghyang.object.note.entity.BaseNote;
import com.banghyang.object.note.entity.MiddleNote;
import com.banghyang.object.note.entity.SingleNote;
import com.banghyang.object.note.entity.TopNote;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Perfume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private String brand;
    private String grade;

    @OneToOne(mappedBy = "perfume", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private PerfumeImage perfumeImage;

    @OneToOne(mappedBy = "perfume", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private SingleNote singleNote;

    @OneToOne(mappedBy = "perfume", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private TopNote topNote;

    @OneToOne(mappedBy = "perfume", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private MiddleNote middleNote;

    @OneToOne(mappedBy = "perfume", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private BaseNote baseNote;

    @Builder(toBuilder = true)
    public Perfume(Long id, String name, String description, String brand, String grade) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.brand = brand;
        this.grade = grade;
    }

    public Perfume modify(Perfume modifyPerfumeEntity) {
        this.name = modifyPerfumeEntity.getName();
        this.description = modifyPerfumeEntity.getDescription();
        this.brand = modifyPerfumeEntity.getBrand();
        this.grade = modifyPerfumeEntity.getGrade();
        return this;
    }
}
