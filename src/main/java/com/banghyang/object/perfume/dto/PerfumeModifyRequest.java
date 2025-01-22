package com.banghyang.object.perfume.dto;

import lombok.Data;

import java.util.List;

@Data
public class PerfumeModifyRequest {
    private Long id;
    private String nameEn;
    private String nameKr;
    private String brand;
    private String grade;
    private String description;
    private String sizeOption;
    private String mainAccord;
    private String ingredients;
    private List<String> imageUrls;
    private List<String> singleNote;
    private List<String> topNote;
    private List<String> middleNote;
    private List<String> baseNote;
}
