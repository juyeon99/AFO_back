package com.banghyang.object.product.dto;

import com.banghyang.object.review.dto.ReviewResponse;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class ProductDetailResponse {
    private Map<String, List<SimilarPerfumeResponse>> similarPerfumes;
    private List<ReviewResponse> reviews;

    public ProductDetailResponse(Map<String, List<SimilarPerfumeResponse>> similarPerfumes, List<ReviewResponse> reviews) {
        this.similarPerfumes = similarPerfumes;
        this.reviews = reviews;
    }
}
