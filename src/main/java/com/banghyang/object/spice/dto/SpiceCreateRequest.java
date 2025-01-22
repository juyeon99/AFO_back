package com.banghyang.object.spice.dto;

import lombok.Data;

import java.util.List;

@Data
public class SpiceCreateRequest {
    private String nameEn;
    private String nameKr;
    private String descriptionEn;
    private String descriptionKr;
    private List<String> imageUrls;
    private String lineName;
}
