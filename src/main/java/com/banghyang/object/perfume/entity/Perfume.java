package com.banghyang.object.perfume.entity;

import com.banghyang.object.note.entity.Note;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Perfume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nameEn;
    private String nameKr;
    private String brand;
    private String grade;
    private String description;
    private String sizeOption;
    private String mainAccord;
    private String ingredients;

    @OneToMany(mappedBy = "perfume", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<PerfumeImage> perfumeImages;

    @OneToMany(mappedBy = "perfume", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Note> notes;

    @Builder
    public Perfume(String nameEn, String nameKr, String brand, String grade, String sizeOption, String description, String mainAccord, String ingredients) {
        this.nameEn = nameEn;
        this.nameKr = nameKr;
        this.brand = brand;
        this.grade = grade;
        this.sizeOption = sizeOption;
        this.description = description;
        this.mainAccord = mainAccord;
        this.ingredients = ingredients;
    }

    public void modify(Perfume modifyPerfumeEntity) {
        this.nameEn = modifyPerfumeEntity.getNameEn();
        this.nameKr = modifyPerfumeEntity.getNameKr();
        this.brand = modifyPerfumeEntity.getBrand();
        this.grade = modifyPerfumeEntity.getGrade();
        this.sizeOption = modifyPerfumeEntity.getSizeOption();
        this.description = modifyPerfumeEntity.getDescription();
        this.mainAccord = modifyPerfumeEntity.getMainAccord();
        this.ingredients = modifyPerfumeEntity.getIngredients();
    }
}
