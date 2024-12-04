package com.banghyang.chat.entity;

import com.banghyang.common.type.ChatType;
import jakarta.persistence.PrePersist;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "chat")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Chat {

    @Id
    private String chatId; // MongoDB 에서 자동 생성되는 ID

    @Field("member_id")
    private Long memberId; // 회원 ID

    @Field("type")
    private ChatType type; // 메시지 타입 (USER, AI)

    @Field("timestamp")
    private LocalDateTime timeStamp; // 메시지 생성 시간

    @Field("chat_image")
    private String imageUrl; // 이미지 URL 또는 null

    @Field("line_id")
    private Integer lineId; // 메시지 구분자

    @Field("content")
    private String content;

    @PrePersist
    public void prePersist() {
        timeStamp = LocalDateTime.now();
    }

    @Builder
    public Chat(Long memberId, ChatType type, String imageUrl, Integer lineId, String content) {
        this.memberId = memberId;
        this.type = type;
        this.imageUrl = imageUrl;
        this.lineId = lineId;
        this.content = content;
    }
}
