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

import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "perfume", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<PerfumeImage> imageList = new ArrayList<>();

    @OneToOne(mappedBy = "perfume", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private SingleNote singleNote;

    @OneToOne(mappedBy = "perfume", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private TopNote topNote;

    @OneToOne(mappedBy = "perfume", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private MiddleNote middleNote;

    @OneToOne(mappedBy = "perfume", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private BaseNote baseNote;

    @Builder(toBuilder = true)
    public Perfume(String name, String description, String brand, String grade) {
        this.name = name;
        this.description = description;
        this.brand = brand;
        this.grade = grade;
    }
}
