package com.banghyang.object.perfume.dto;

import com.banghyang.object.note.entity.SingleNote;
import com.banghyang.object.perfume.entity.Perfume;
import com.banghyang.object.perfume.entity.PerfumeImage;
import lombok.Data;

@Data
public class SinglePerfumeResponse {
    private String name;
    private String description;
    private String brand;
    private String grade;
    private String imageUrl;
    private String singleNote;

    public SinglePerfumeResponse from(
            Perfume perfumeEntity,
            PerfumeImage imageEntity,
            SingleNote singleNoteEntity
    ) {
        this.name = perfumeEntity.getName();
        this.description = perfumeEntity.getDescription();
        this.brand = perfumeEntity.getBrand();
        this.grade = perfumeEntity.getGrade();
        this.imageUrl = imageEntity.getUrl();
        this.singleNote = singleNoteEntity.getSpices();
        return this;
    }
}
