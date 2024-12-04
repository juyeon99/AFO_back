package com.banghyang.recommend.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Document(collection = "chat")
public class Chat {

    @Id
    private String chatId; // MongoDB에서 자동 생성되는 ID

    @Field("member_id")
    private Long memberId; // 회원 ID

    @Field("type")
    private MessageType type; // 메시지 타입 (USER, AI)

    @Field("timestamp")
    private LocalDateTime timestamp; // 메시지 생성 시간

    @Field("chat_image")
    private String chatImage; // 이미지 URL 또는 null

    @Field("line_id")
    private Integer lineId; // 메시지 구분자

    // Recommendation 관련 필드
    @Field("recommendations")
    private List<Recommendation> recommendations; // 추천 리스트

    @Field("common_feeling")
    private String commonFeeling; // 공통 감정

    @Field("image_prompt")
    private String imagePrompt; // 이미지 생성 프롬프트

    // Chat 텍스트 필드
    @Field("message_text")
    private String messageText; // 사용자 입력 또는 AI 응답 텍스트

    // MessageType enum 정의
    public enum MessageType {
        USER, // 사용자 메시지
        AI    // AI 메시지
    }

    // Recommendation 내부 클래스 정의
    @Getter
    @Builder
    public static class Recommendation {
        @Field("id")
        private String id; // 향수 ID

        @Field("reason")
        private String reason; // 추천 이유

        @Field("situation")
        private String situation; // 추천 상황
    }
}
