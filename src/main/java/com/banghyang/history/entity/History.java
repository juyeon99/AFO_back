package com.banghyang.history.entity;

import com.banghyang.member.entity.Member;
import com.banghyang.object.line.entity.Line;
import com.fasterxml.jackson.annotation.JsonBackReference;
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
    private Long id; // 히스토리 아이디

    private String chatId; // 히스토리 생성시 사용한 채팅 아이디
    private LocalDateTime timeStamp; // 히스토리 생성일시

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member; // 히스토리 생성 사용자 아이디

    @OneToMany(mappedBy = "history", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Recommendation> recommendation; // 추천 정보

    @OneToOne
    @JoinColumn(name = "line_id", referencedColumnName = "id", nullable = false)
    @JsonBackReference
    private Line line; // 히스토리 생성한 추천의 계열 아이디

    // 생성시간 자동 입력
    @PrePersist
    public void prePersist() {
        if (this.timeStamp == null) {
            this.timeStamp = LocalDateTime.now();
        }
    }

    // 빌더
    @Builder
    public History(
            String chatId,
            Member member,
            List<Recommendation> recommendation,
            Line line
    ) {
        this.chatId = chatId;
        this.member = member;
        this.recommendation = recommendation;
        this.line = line;
    }
}
