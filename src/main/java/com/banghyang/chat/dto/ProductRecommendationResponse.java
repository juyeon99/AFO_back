package com.banghyang.chat.dto;

import com.banghyang.common.type.ChatMode;
import lombok.Data;

import java.util.List;

@Data
public class ProductRecommendationResponse {
    private ChatMode mode;
    private Long lineId;
    private String content;
    private List<Recommendation> recommendations;

    @Data
    public static class Recommendation {
        private Long id;
        private String reason;
        private String situation;
    }
}
