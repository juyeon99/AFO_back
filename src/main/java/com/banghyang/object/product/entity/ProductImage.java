package com.banghyang.object.product.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 제품 이미지 아이디
    private String url; // 제품 이미지 url

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product; // 제품 아이디

    // 빌더
    @Builder
    public ProductImage(String url, Product product) {
        this.product = product;
        this.url = url;
    }

    // 제품 이미지 정보 수정 메소드
    public void modify(ProductImage modifyProductImageEntity) {
        this.product = modifyProductImageEntity.product;
        this.url = modifyProductImageEntity.url;
    }
}
