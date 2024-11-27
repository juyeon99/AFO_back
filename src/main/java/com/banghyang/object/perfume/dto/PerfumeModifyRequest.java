package com.banghyang.object.perfume.dto;

import lombok.Data;

@Data
public class PerfumeModifyRequest {
    private Long id;
    private String name;
    private String description;
    private String brand;
    private String grade;
    private String imageUrl;
    private String singleNote;
    private String topNote;
    private String middleNote;
    private String baseNote;
}
