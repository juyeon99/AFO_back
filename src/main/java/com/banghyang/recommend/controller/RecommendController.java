package com.banghyang.recommend.controller;

import com.banghyang.recommend.service.RecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@RestController
@RequestMapping("/")
public class RecommendController {

    @Autowired
    private RecommendService recommendService;

    @PostMapping("/recommend")
    public ResponseEntity<Map<String, Object>> recommendPerfume(
            @RequestParam("user_input") String userInput,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        try {
            // 로그로 image 상태 확인
            if (image != null) {
                System.out.println("Image received: " + image.getOriginalFilename());
            } else {
                System.out.println("No image received");
            }

            Map<String, Object> response = recommendService.processInputAndImage(userInput, image);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace(); // 예외 출력
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Internal Server Error"));
        }
    }
}

