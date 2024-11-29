package com.banghyang.recommend.controller;

import com.banghyang.recommend.service.ImageProcessingService;
import com.banghyang.recommend.service.RecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/")
public class RecommendController {

    @Autowired
    private RecommendService recommendService;
    private ImageProcessingService imageProcessingService;

    @PostMapping("/recommend")
    public ResponseEntity<Map<String, Object>> processInputAndImage(
            @RequestParam(value = "user_input", required = false) String userInput,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        Map<String, Object> response = new HashMap<>();

        try {
            // 이미지가 없을 경우 처리
            if (image != null && !image.isEmpty()) {
                Map<String, Object> imageProcessingResult = imageProcessingService.processImage(image);
                response.put("imageProcessed", imageProcessingResult);
            } else {
                // 이미지가 없을 때 처리 로직
                response.put("message", "No image provided.");
            }

            // 사용자 입력 처리
            if (userInput != null && !userInput.isEmpty()) {
                // 사용자 입력 처리
                response.put("userInputProcessed", userInput);
            } else {
                // 사용자 입력이 비어 있을 때 처리 로직
                response.put("message", "No user input provided.");
            }

            // 성공 응답 반환
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", "Processing error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);  // 500 응답
        }
    }
}
