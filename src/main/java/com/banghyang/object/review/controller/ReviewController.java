package com.banghyang.object.review.controller;

import com.banghyang.object.review.dto.ReviewRequest;
import com.banghyang.object.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/reviews")
@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<?> createReview(@RequestBody ReviewRequest reviewRequest) {
        reviewService.createReview(reviewRequest);
        return ResponseEntity.ok().build();
    }
}
