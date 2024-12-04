package com.banghyang.recommend.dto;

import com.banghyang.member.entity.Member;
import com.banghyang.recommend.entity.Chat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatDto {
    private String id;
    private Long memberId;
    private String content;
    private Chat.MessageType type;
    private LocalDateTime timestamp;
    private String chatImage;

    // Entity -> DTO
    public static ChatDto fromEntity(Chat chat) {
        return ChatDto.builder()
                .id(chat.getId())
                .memberId(chat.getMemberId())
                .content(chat.getContent())
                .type(chat.getType())
                .timestamp(chat.getTimestamp())
                .chatImage(chat.getChatImage())
                .build();
    }

    // DTO -> Entity
    public Chat toEntity(Member member) {
        return Chat.builder()
                .id(id)
                .memberId(member.getId())
                .content(this.content)
                .type(this.type)
                .timestamp(this.timestamp)
                .chatImage(this.chatImage)  // chatImage 설정
                .build();
    }
}