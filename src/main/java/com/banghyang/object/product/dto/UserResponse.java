package com.banghyang.object.product.dto;

import com.banghyang.object.product.entity.ProductImage;
import lombok.Data;

import java.util.List;

@Data
public class UserResponse {

    private List<DiffuserResponse.Recommendation> recommendations;
    private String usageRoutine;
    private List<String> imageUrls;

}
