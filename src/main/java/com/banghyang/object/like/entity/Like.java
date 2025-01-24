package com.banghyang.object.like.entity;

import com.banghyang.member.entity.Member;
import com.banghyang.object.product.entity.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 좋아요 아이디
    private LocalDateTime timeStamp; // 좋아요 생성일시

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member; // 좋아요 생성자 아이디

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product; // 좋아요 한 제품 아이디

    // 좋아요 생성일시 자동 생성
    @PrePersist
    protected void onCreate() {
        this.timeStamp = LocalDateTime.now();
    }

    // 빌더
    @Builder
    public Like(Member member, Product product) {
        this.member = member;
        this.product = product;
    }
}
