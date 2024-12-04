package com.banghyang.chat.dto;

import com.banghyang.common.type.ChatType;
import com.banghyang.member.entity.Member;
import com.banghyang.chat.entity.Chat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ChatDto {
    private String id;
    private Long memberId;
    private String content; // 사용자 입력 또는 AI 응답 텍스트
    private ChatType type;
    private LocalDateTime timestamp;
    private String chatImage;
    private Integer lineId; // 메시지 구분자
    private List<Chat.Recommendation> recommendations; // 추천 리스트
    private String commonFeeling; // 공통 감정
    private String imagePrompt; // 이미지 프롬프트

    // Entity -> DTO
    public static ChatDto fromEntity(Chat chat) {
        return ChatDto.builder()
                .id(chat.getChatId()) // MongoDB의 기본 키
                .memberId(chat.getMemberId())
                .content(chat.getMessageText())
                .type(chat.getType())
                .timestamp(chat.getTimestamp())
                .chatImage(chat.getChatImage())
                .lineId(chat.getLineId())
                .recommendations(chat.getRecommendations())
                .commonFeeling(chat.getCommonFeeling())
                .imagePrompt(chat.getImagePrompt())
                .build();
    }

    // DTO -> Entity
    public Chat toEntity(Member member) {
        return Chat.builder()
                .chatId(id) // MongoDB의 기본 키
                .memberId(member.getId())
                .messageText(this.content)
                .type(this.type)
                .timestamp(this.timestamp)
                .chatImage(this.chatImage)
                .lineId(this.lineId)
                .recommendations(this.recommendations)
                .commonFeeling(this.commonFeeling)
                .imagePrompt(this.imagePrompt)
                .build();
    }
}