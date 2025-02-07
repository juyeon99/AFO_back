package com.banghyang.object.review.controller;

import com.banghyang.object.review.dto.ReviewRequest;
import com.banghyang.object.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/reviews")
@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 리뷰 생성 메소드
     */
    @PostMapping
    public ResponseEntity<?> createReview(@RequestBody ReviewRequest reviewRequest) {
        reviewService.createReview(reviewRequest);
        return ResponseEntity.ok().build();
    }

    /**
     * 리뷰 수정 메소드
     */
    @PutMapping
    public ResponseEntity<?> updateReview(@RequestBody ReviewRequest reviewRequest) {
        reviewService.modifyReview(reviewRequest);
        return ResponseEntity.ok().build();
    }

    /**
     * 리뷰 삭제 메소드
     */
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok().build();
    }
}
