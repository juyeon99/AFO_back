package com.banghyang.history.dto;

import com.banghyang.common.type.ChatMode;
import com.banghyang.common.type.ChatType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
public class HistoryResponse {
    private String chatId;
    private String content;
    private String imageUrl;
    private Long lineId;
    private Long memberId;
    private ChatMode mode;
    private LocalDateTime timeStamp;
    private ChatType type;
    private List<RecommendationDto> recommendations;

    @Data
    @Builder
    public static class RecommendationDto {
        private String perfumeName;
        private String perfumeBrand;
        private String perfumeGrade;
        private String perfumeImageUrl;
        private String reason;
        private String situation;
    }
}
