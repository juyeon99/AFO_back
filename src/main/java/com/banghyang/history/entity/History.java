package com.banghyang.history.entity;

import com.banghyang.common.type.ChatMode;
import com.banghyang.common.type.ChatType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table
@NoArgsConstructor
@Getter
public class History {

    @Id
    private Long id;
    private String chatId;
    private String content;
    private String imageUrl;
    private Long lineId;
    private Long memberId;
    private ChatMode mode;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "history_id")
    private List<Recommendation> recommendation;
    private LocalDateTime timeStamp;
    private ChatType type;


    @Builder
    public History(String chatId, String content, String imageUrl, Long lineId, Long memberId, ChatMode mode, List<Recommendation> recommendation, LocalDateTime timeStamp, ChatType type) {
        this.chatId = chatId;
        this.content = content;
        this.imageUrl = imageUrl;
        this.lineId = lineId;
        this.memberId = memberId;
        this.mode = mode;
        this.recommendation = recommendation;
        this.timeStamp = timeStamp;
        this.type = type;
    }
}
