package com.banghyang.object.perfume.dto;

import lombok.Data;

import java.util.List;

@Data
public class PerfumeResponse {
    private Long id;
    private String name;
    private String description;
    private List<String> imageUrlList;
    private String singleNote;
    private String topNote;
    private String middleNote;
    private String baseNote;
}
