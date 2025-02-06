package com.banghyang.object.like.controller;

import com.banghyang.object.like.dto.LikeRequest;
import com.banghyang.object.like.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/likes")
@RestController
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    /**
     * 좋아요 생성
     */
    @PostMapping
    public ResponseEntity<?> createLike(@RequestBody LikeRequest likeRequest) {
        likeService.createLike(likeRequest);
        return ResponseEntity.ok().build();
    }

    /**
     * 좋아요 삭제
     */
    @DeleteMapping("/{likeId}")
    public ResponseEntity<?> deleteLike(@PathVariable Long likeId) {
        likeService.deleteLike(likeId);
        return ResponseEntity.ok().build();
    }
}
