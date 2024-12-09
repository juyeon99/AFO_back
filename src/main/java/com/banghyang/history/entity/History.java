package com.banghyang.history.entity;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "history_id")
    private List<Recommendations> recommendations;

    private String chatId;
    private Long lineId;
    private Long memberId;
    private LocalDateTime timeStamp;

    @Builder
    public History(
            String chatId,
            String content,
            Long lineId,
            Long memberId,
            List<Recommendations> recommendations,
            LocalDateTime timeStamp
    ) {
        this.chatId = chatId;
        this.content = content;
        this.lineId = lineId;
        this.memberId = memberId;
        this.recommendations = recommendations;
        this.timeStamp = timeStamp;
    }
}
