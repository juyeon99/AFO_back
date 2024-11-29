package com.banghyang.object.perfume.dto;

import com.banghyang.object.note.entity.BaseNote;
import com.banghyang.object.note.entity.MiddleNote;
import com.banghyang.object.note.entity.SingleNote;
import com.banghyang.object.note.entity.TopNote;
import com.banghyang.object.perfume.entity.Perfume;
import com.banghyang.object.perfume.entity.PerfumeImage;
import lombok.Data;

@Data
public class PerfumeCreateRequest {
    private String name;
    private String description;
    private String brand;
    private String grade;

    private String imageUrl;

    private String singleNote;
    private String topNote;
    private String middleNote;
    private String baseNote;

    public Perfume toPerfumeEntity() {
        return Perfume.builder()
                .name(this.name)
                .description(this.description)
                .brand(this.brand)
                .grade(this.grade)
                .build();
    }

    public PerfumeImage toPerfumeImageEntity() {
        return PerfumeImage.builder()
                .url(this.imageUrl)
                .perfume(this.toPerfumeEntity())
                .build();
    }

    public SingleNote toSingleNoteEntity() {
        return SingleNote.builder()
                .spices(this.singleNote)
                .perfume(this.toPerfumeEntity())
                .build();
    }

    public TopNote toTopNoteEntity() {
        return TopNote.builder()
                .spices(this.topNote)
                .perfume(this.toPerfumeEntity())
                .build();
    }

    public MiddleNote toMiddleNoteEntity() {
        return MiddleNote.builder()
                .spices(this.middleNote)
                .perfume(this.toPerfumeEntity())
                .build();
    }

    public BaseNote toBaseNoteEntity() {
        return BaseNote.builder()
                .spices(this.baseNote)
                .perfume(this.toPerfumeEntity())
                .build();
    }
}
