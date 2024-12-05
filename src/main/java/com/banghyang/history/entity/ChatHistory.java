package com.banghyang.history.entity;

import com.banghyang.common.type.ChatMode;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table
@NoArgsConstructor
@Getter
public class ChatHistory {

    @Id
    private Long id;
    private String chatId;
    private String content;
    private String imageUrl;
    private Long lineId;
    private Long memberId;
    private ChatMode mode;
//    private Recommendation recommendation;
    private LocalDateTime timeStamp;
    private String type;



}
