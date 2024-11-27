package com.banghyang.object.perfume.dto;

import com.banghyang.object.note.entity.BaseNote;
import com.banghyang.object.note.entity.MiddleNote;
import com.banghyang.object.note.entity.TopNote;
import com.banghyang.object.perfume.entity.Perfume;
import com.banghyang.object.perfume.entity.PerfumeImage;
import lombok.Data;

@Data
public class MultiPerfumeResponse {
    private Long id;
    private String name;
    private String description;
    private String brand;
    private String grade;
    private String imageUrl;
    private String topNote;
    private String middleNote;
    private String baseNote;

    public MultiPerfumeResponse from(
            Perfume perfumeEntity,
            PerfumeImage imageEntity,
            TopNote topNoteEntity,
            MiddleNote middleNoteEntity,
            BaseNote baseNoteEntity
    ) {
        this.id = perfumeEntity.getId();
        this.name = perfumeEntity.getName();
        this.description = perfumeEntity.getDescription();
        this.brand = perfumeEntity.getBrand();
        this.grade = perfumeEntity.getGrade();
        this.imageUrl = imageEntity.getUrl();
        this.topNote = topNoteEntity.getSpices();
        this.middleNote = middleNoteEntity.getSpices();
        this.baseNote = baseNoteEntity.getSpices();
        return this;
    }
}
