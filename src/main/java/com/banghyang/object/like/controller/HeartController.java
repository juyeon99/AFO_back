package com.banghyang.object.like.controller;

import com.banghyang.object.like.dto.HeartRequest;
import com.banghyang.object.like.service.HeartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/likes")
@RestController
@RequiredArgsConstructor
public class HeartController {

    private final HeartService heartService;

    /**
     * 좋아요 생성
     */
    @PostMapping
    public ResponseEntity<?> createLike(@RequestBody HeartRequest heartRequest) {
        heartService.createLike(heartRequest);
        return ResponseEntity.ok().build();
    }

    /**
     * 좋아요 삭제
     */
    @DeleteMapping("/{likeId}")
    public ResponseEntity<?> deleteLike(@PathVariable Long likeId) {
        heartService.deleteLike(likeId);
        return ResponseEntity.ok().build();
    }
}
