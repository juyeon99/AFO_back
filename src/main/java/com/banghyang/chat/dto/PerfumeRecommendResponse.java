package com.banghyang.chat.dto;

import com.banghyang.common.type.ChatMode;
import lombok.Data;

import java.util.List;

@Data
public class PerfumeRecommendResponse {
    private ChatMode mode;
    private Long lineId;
    private String content;
    private List<Recommendation> recommendations;

    @Data
    public static class Recommendation {
        private String id;
        private String reason;
        private String situation;
    }
}
