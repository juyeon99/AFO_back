package com.banghyang.recommend.controller;

import com.banghyang.recommend.dto.ChatDto;
import com.banghyang.recommend.service.ImageProcessingService;
import com.banghyang.recommend.service.RecommendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/")
public class RecommendController {

    private final RecommendService recommendService;
    private final ImageProcessingService imageProcessingService;

    public RecommendController(RecommendService recommendService, ImageProcessingService imageProcessingService) {
        this.recommendService = recommendService;
        this.imageProcessingService = imageProcessingService;
    }

    @PostMapping("/recommend")
    public ResponseEntity<Map<String, Object>> processInputAndImage(
            @RequestParam(value = "user_input", required = false, defaultValue = "") String userInput,
            @RequestParam(value = "userId", required = false) Long memberId,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        log.info("요청 받음 - 회원 ID: {}, 사용자 입력: {}", memberId, userInput);
        Map<String, Object> response = new HashMap<>();

        try {
            String processedFeeling = "";
            String imageUrl = null;

            // 이미지 처리
            if (image != null && !image.isEmpty()) {
                try {
                    log.info("회원 ID: {}의 이미지 처리 중", memberId);
                    Map<String, Object> imageProcessingResult = imageProcessingService.processImage(image);
                    response.put("imageProcessed", imageProcessingResult);

                    // 이미지 처리 결과에서 감정과 URL 가져오기
                    Map<String, Object> result = (Map<String, Object>) imageProcessingResult.get("result");
                    processedFeeling = (String) result.get("feeling");
                    imageUrl = (String) imageProcessingResult.get("imageUrl"); // imageUrl 위치 수정
                    log.info("이미지 처리 완료. 추출된 감정: {}, URL: {}", processedFeeling, imageUrl);

                    // 감정을 사용자 입력에 추가
                    if (userInput == null || userInput.isEmpty()) {
                        userInput = processedFeeling;
                        log.info("처리된 감정을 사용자 입력으로 사용: {}", userInput);
                    } else {
                        userInput += " " + processedFeeling;
                        log.info("처리된 감정을 사용자 입력에 추가: {}", userInput);
                    }
                } catch (Exception e) {
                    log.error("이미지 처리 중 오류 발생: {}", e.getMessage(), e);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Map.of("error", "이미지 처리에 실패했습니다"));
                }
            }

            // recommendService 호출
            log.info("추천 서비스 호출 - 회원 ID: {}, 사용자 입력: {}, 이미지 URL: {}",
                    memberId, userInput, image);
            Map<String, Object> finalResponse = recommendService.processInputAndImage(userInput, image, memberId);

            if (finalResponse == null || finalResponse.isEmpty()) {
                log.error("추천 처리 결과가 null이거나 비어있습니다");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "추천 처리에 실패했습니다"));
            }

            // 최종 응답에 이미지 처리 결과 포함
            if (response.containsKey("imageProcessed")) {
                finalResponse.put("imageProcessed", response.get("imageProcessed"));
            }

            log.info("회원 ID: {}의 요청 처리 완료", memberId);
            return ResponseEntity.ok(finalResponse);

        } catch (Exception e) {
            log.error("회원 ID: {}의 요청 처리 중 오류 발생", memberId, e);
            e.printStackTrace();
            response.put("error", "처리 중 오류가 발생했습니다");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 봇 응답 생성 API
    @PostMapping("/recommend/response")
    public ResponseEntity<Map<String, Object>> getBotResponse(@RequestParam("userInput") String userInput) {
        Map<String, Object> response = new HashMap<>();

        try {
            // 봇 응답 생성
            String botResponse = recommendService.generateBotResponse(userInput);
            response.put("userInput", userInput);
            response.put("botResponse", botResponse);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", "Failed to generate bot response: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("recommend/{memberId}")
    public ResponseEntity<List<ChatDto>> getChatHistory(@PathVariable Long memberId) {
        List<ChatDto> chatHistory = recommendService.getChatHistory(memberId);
        return ResponseEntity.ok(chatHistory);
    }
}


