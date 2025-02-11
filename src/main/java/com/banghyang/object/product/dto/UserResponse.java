package com.banghyang.object.product.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserResponse {

    private List<DiffuserResponse.Recommendation> recommendations;
    private String usageRoutine;
    private String imageUrl;
}
