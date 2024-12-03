package com.banghyang.recommend.controller;

import com.banghyang.recommend.service.ImageProcessingService;
import com.banghyang.recommend.service.RecommendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Slf4j
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
            @RequestPart(value = "image", required = false) MultipartFile image) {

        Map<String, Object> response = new HashMap<>();

        try {
            String processedFeeling = "";
            if (image != null && !image.isEmpty()) {
                try {
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
                } catch (Exception e) {
                    log.error("Error processing image : {}", e.getMessage(), e);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Map.of("error", "Image processing failed"));
                }
            } else {
                response.put("message", "No image provided.");
            }
            Map<String, Object> finalResponse = recommendService.processInputAndImage(userInput);
            if (finalResponse == null || finalResponse.isEmpty()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Recommendation processing failed"));
            }

            return ResponseEntity.ok(finalResponse);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", "Processing error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}

