package com.banghyang.object.product.dto;

import lombok.Data;

import java.util.Set;

@Data
public class ProductModifyRequest {
    private Long id;
    private String nameEn;
    private String nameKr;
    private String brand;
    private String grade;
    private String content;
    private String sizeOption;
    private String mainAccord;
    private String ingredients;
    private Set<String> imageUrlSet;
    private Set<String> singleNoteSet;
    private Set<String> topNoteSet;
    private Set<String> middleNoteSet;
    private Set<String> baseNoteSet;
}
