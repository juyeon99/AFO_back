package com.banghyang.recommend.entity;

import com.banghyang.recommend.type.ChatMode;
import com.banghyang.recommend.type.ChatType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "ChatHistory")
public class ChatHistory {

    @Id
    private String id;
    @Field("chat_id")
    private String chatId;
    @Field("member_id")
    private Long memberId;
    @Field("type")
    private ChatType type;
    @Field("timestamp")
    private LocalDateTime timeStamp;
    @Field("imageUrl")
    private String imageUrl;
    @Field("content")
    private String content;
    @Field("mode")
    private ChatMode mode;
    @Field("line_id")
    private Long lineId;
    @Field("recommendations")
    private List<Chat.Recommendation> recommendations;


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


    @Builder
    public ChatHistory(
            String  chatId,
            ChatType type,
            Long memberId,
            String content,
            String imageUrl,
            LocalDateTime timeStamp,
            ChatMode mode,
            Long lineId,
            List<Chat.Recommendation> recommendations
    ) {
        this.chatId = chatId;
        this.type = type;
        this.memberId = memberId;
        this.content = content;
        this.imageUrl = imageUrl;
        this.timeStamp = timeStamp;
        this.mode = mode;
        this.lineId = lineId;
        this.recommendations = recommendations;
    }
}
