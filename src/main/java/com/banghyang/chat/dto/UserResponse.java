package com.banghyang.chat.dto;

import com.banghyang.chat.entity.Chat;
import com.banghyang.common.type.ChatMode;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserResponse {
    private String id;
    private Long memberId;
    private ChatMode mode;
    private String content;
    private Long lineId;
    private String imageUrl;
    private List<Chat.Recommendation> recommendations;
    private LocalDateTime timeStamp;
}
