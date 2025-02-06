package com.banghyang.object.product.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProductCreateRequest {
    private String nameEn;
    private String nameKr;
    private String brand;
    private String grade;
    private String sizeOption;
    private String content;
    private String mainAccord;
    private String ingredients;
    private List<String> imageUrlList;
    private List<String> singleNoteList;
    private List<String> topNoteList;
    private List<String> middleNoteList;
    private List<String> baseNoteList;
}
