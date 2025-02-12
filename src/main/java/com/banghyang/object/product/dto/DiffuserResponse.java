package com.banghyang.object.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class DiffuserResponse {

    private List<Recommendation> recommendations;
    @JsonProperty("usage_routine")
    private String usageRoutine;

    @Data
    public static class Recommendation {
        @JsonProperty("product_id")
        private long productId;
        private String name;
        private String brand;
    }

}
