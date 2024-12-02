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

    @Autowired
    private ImageProcessingService imageProcessingService;

    @PostMapping("/recommend")
    public ResponseEntity<Map<String, Object>> processInputAndImage(
            @RequestParam(value = "user_input", required = false) String userInput,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        Map<String, Object> response = new HashMap<>();

        try {
            // 이미지가 있을 경우 처리
            String processedDescription = "";
            if (image != null && !image.isEmpty()) {
                Map<String, Object> imageProcessingResult = imageProcessingService.processImage(image);
                response.put("imageProcessed", imageProcessingResult);

                // 이미지 처리 결과를 user_input에 추가
                processedDescription = (String) imageProcessingResult.get("description");
                if (userInput == null || userInput.isEmpty()) {
                    // user_input이 없으면 처리된 이미지 설명을 user_input으로 사용
                    userInput = processedDescription;
                } else {
                    // 이미지 설명을 기존 user_input에 추가
                    userInput += " " + processedDescription;
                }
            } else {
                // 이미지가 없을 때 처리
                response.put("message", "No image provided.");
            }

            // 2. 사용자 입력 처리 후, 서비스로 넘기기
            Map<String, Object> finalResponse = recommendService.processInputAndImage(userInput);

            // 3. 최종 응답 반환
            return ResponseEntity.ok(finalResponse);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", "Processing error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);  // 500 응답
        }
    }
}
