package com.banghyang.object.review.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ReviewResponse {
    private Long id;
    private String name;  // 리뷰 작성자
    private String content; // 리뷰 내용
    private LocalDateTime createdAt; // 작성 날짜
}
