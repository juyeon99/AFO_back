package com.banghyang.object.spice.dto;

import lombok.Data;

import java.util.List;

@Data
public class SpiceResponse {
    private Long id;
    private String nameEn;
    private String nameKr;
    private String descriptionEn;
    private String descriptionKr;
    private List<String> imageUrl;

    private Long lineId;
    private String lineName;
}
