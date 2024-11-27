package com.banghyang.object.spice.dto;

import com.banghyang.object.line.entity.Line;
import com.banghyang.object.spice.entity.Spice;
import com.banghyang.object.spice.entity.SpiceImage;
import lombok.Data;

@Data
public class SpiceResponse {

    private Long id;
    private String name;
    private String nameKr;
    private String description;
    private String imageUrl;
    private String line;
    private String color;

    public SpiceResponse from(
            Spice spiceEntity,
            SpiceImage imageEntity,
            Line lineEntity
    ) {
        this.id = spiceEntity.getId();
        this.name = spiceEntity.getName();
        this.nameKr = spiceEntity.getNameKr();
        this.description = spiceEntity.getDescription();
        this.imageUrl = imageEntity.getUrl();
        this.line = lineEntity.getName();
        this.color = lineEntity.getColor();
        return this;
    }
}
