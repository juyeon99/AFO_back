package com.banghyang.object.product.dto;

import com.banghyang.object.review.dto.ReviewResponse;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class ProductDetailResponse {
    private Map<String, List<SimilarPerfumeResponse>> similarPerfumes;

    public ProductDetailResponse(Map<String, List<SimilarPerfumeResponse>> similarPerfumes) {
        this.similarPerfumes = similarPerfumes;
    }
}
