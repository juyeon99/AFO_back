package com.banghyang.history.entity;

import com.banghyang.member.entity.Member;
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

    private Long lineId;
    private String chatId;
    private LocalDateTime timeStamp;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "history", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Recommendation> recommendation;

    @PrePersist
    public void prePersist() {
        if (this.timeStamp == null) {
            this.timeStamp = LocalDateTime.now();
        }
    }

    @Builder
    public History(
            Long lineId,
            String chatId,
            Member member,
            List<Recommendation> recommendation
    ) {
        this.lineId = lineId;
        this.chatId = chatId;
        this.member = member;
        this.recommendation = recommendation;
    }
}
