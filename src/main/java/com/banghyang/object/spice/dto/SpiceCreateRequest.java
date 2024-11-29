package com.banghyang.object.spice.dto;

import lombok.Data;

@Data
public class SpiceCreateRequest {
    private String name;
    private String nameKr;
    private String description;
    private String imageUrl;
    private String lineName;
}
