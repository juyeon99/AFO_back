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
            @RequestParam(value = "user_input", required = false, defaultValue = "") String userInput,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        Map<String, Object> response = new HashMap<>();

        try {
            String processedFeeling = "";
            if (image != null && !image.isEmpty()) {
                Map<String, Object> imageProcessingResult = imageProcessingService.processImage(image);
                response.put("imageProcessed", imageProcessingResult);

                // 여기를 수정: result 객체에서 description 가져오기
                Map<String, Object> result = (Map<String, Object>) imageProcessingResult.get("result");
                processedFeeling = (String) result.get("feeling");

                if (userInput == null || userInput.isEmpty()) {
                    userInput = processedFeeling;
                } else {
                    userInput += " " + processedFeeling;
                }
            } else {
                response.put("message", "No image provided.");
            }

            Map<String, Object> finalResponse = recommendService.processInputAndImage(userInput);
            return ResponseEntity.ok(finalResponse);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", "Processing error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
