package com.banghyang.recommend.dto;

import com.banghyang.recommend.entity.ChatImage;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatImageDto {
    private Long id;
    private String imageUrl;

    // Entity -> DTO
    public static ChatImageDto fromEntity(ChatImage chatImage) {
        return ChatImageDto.builder()
                .id(chatImage.getId())
                .imageUrl(chatImage.getImageUrl())
                .build();
    }

    // DTO -> Entity
    public ChatImage toEntity() {
        return ChatImage.builder()
                .id(this.id)
                .imageUrl(this.imageUrl)
                .build();
    }
}
