package com.banghyang.object.perfume.dto;

import lombok.Data;

import java.util.List;

@Data
public class PerfumeCreateRequest {
    private String nameEn;
    private String nameKr;
    private String brand;
    private String grade;
    private String sizeOption;
    private String description;
    private String mainAccord;
    private String ingredients;
    private List<String> imageUrls;
    private String singleNote;
    private String topNote;
    private String middleNote;
    private String baseNote;
}
