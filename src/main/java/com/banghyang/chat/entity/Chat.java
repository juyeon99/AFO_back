package com.banghyang.chat.entity;

import com.banghyang.common.type.ChatMode;
import com.banghyang.common.type.ChatType;
import jakarta.persistence.PrePersist;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "chat")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Chat {

    @Id
    private String id; // MongoDB 에서 자동 생성되는 ID

    @Field("type")
    private ChatType type; // 메시지 타입 (USER, AI)

    @Field("member_id")
    private Long memberId; // 회원 ID

    @Field("content")
    private String content;

    @Field("chat_image")
    private String imageUrl;

    @Field("timeStamp")
    private LocalDateTime timeStamp; // 메시지 생성 시간

    @Field("mode")
    private ChatMode mode;

    @Field("line_id")
    private Long lineId;

    @Field("recommendations")
    private List<Recommendation> recommendations;

    @Data
    public static class Recommendation {
        private Long perfumeId;
        private String perfumeImageUrl;
        private String perfumeName;
        private String perfumeBrand;
        private String perfumeGrade;
        private String reason;
        private String situation;
    }

    @PrePersist
    public void prePersist() {
        timeStamp = LocalDateTime.now();
    }

    @Builder
    public Chat(
            ChatType type,
            Long memberId,
            String content,
            String imageUrl,
            ChatMode mode,
            Long lineId,
            List<Recommendation> recommendations
    ) {
        this.type = type;
        this.memberId = memberId;
        this.content = content;
        this.imageUrl = imageUrl;
        this.mode = mode;
        this.lineId = lineId;
        this.recommendations = recommendations;
    }
}
