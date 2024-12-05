package com.banghyang.history.entity;

import com.banghyang.common.type.ChatMode;
import jakarta.persistence.*;
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
    private String type;



}
