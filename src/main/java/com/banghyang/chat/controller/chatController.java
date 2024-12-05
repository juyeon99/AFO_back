package com.banghyang.chat.controller;

import com.banghyang.chat.dto.UserRequest;
import com.banghyang.chat.dto.UserResponse;
import com.banghyang.chat.service.ChatService;
import com.banghyang.chat.service.RecommendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/chats")
public class chatController {

    private final ChatService chatService;
    private final RecommendService recommendService;

    @PostMapping
    public ResponseEntity<UserResponse> processInputAndImage(@ModelAttribute UserRequest userRequest) {
        // ModelAttribute 를 이용하여 http 요청 데이터를 dto 로 바인딩(multipart 포함한 데이터 처리 가능)
        // service 에서 유저의 입력에 대한 답변을 dto 로 담아 전달
        return ResponseEntity.ok(chatService.answerToUserRequest(userRequest));


//        log.info("요청 받음 - 회원 ID: {}, 사용자 입력: {}", memberId, content);
//        Map<String, Object> response = new HashMap<>();
//
//        try {
//            String processedFeeling = "";
//            String imageUrl = null;
//
//            // 이미지 처리
//            if (image != null && !image.isEmpty()) {
//                try {
//                    log.info("회원 ID: {}의 이미지 처리 중", memberId);
//                    Map<String, Object> imageProcessingResult = imageProcessingService.processImage(image);
//                    response.put("imageProcessed", imageProcessingResult);
//
//                    // 이미지 처리 결과에서 감정과 URL 가져오기
//                    Map<String, Object> result = (Map<String, Object>) imageProcessingResult.get("result");
//                    processedFeeling = (String) result.get("feeling");
//                    imageUrl = (String) imageProcessingResult.get("imageUrl"); // imageUrl 위치 수정
//                    log.info("이미지 처리 완료. 추출된 감정: {}, URL: {}", processedFeeling, imageUrl);
//
//                    // 감정을 사용자 입력에 추가
//                    if (content == null || content.isEmpty()) {
//                        content = processedFeeling;
//                        log.info("처리된 감정을 사용자 입력으로 사용: {}", content);
//                    } else {
//                        content += " " + processedFeeling;
//                        log.info("처리된 감정을 사용자 입력에 추가: {}", content);
//                    }
//                } catch (Exception e) {
//                    log.error("이미지 처리 중 오류 발생: {}", e.getMessage(), e);
//                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                            .body(Map.of("error", "이미지 처리에 실패했습니다"));
//                }
//            }
//
//            // recommendService 호출
//            log.info("추천 서비스 호출 - 회원 ID: {}, 사용자 입력: {}, 이미지 URL: {}",
//                    memberId, content, image);
//            Map<String, Object> finalResponse = recommendService.processInputAndImage(content, image, memberId);
//
//            if (finalResponse == null || finalResponse.isEmpty()) {
//                log.error("추천 처리 결과가 null이거나 비어있습니다");
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                        .body(Map.of("error", "추천 처리에 실패했습니다"));
//            }
//
//            // 최종 응답에 이미지 처리 결과 포함
//            if (response.containsKey("imageProcessed")) {
//                finalResponse.put("imageProcessed", response.get("imageProcessed"));
//            }
//
//            log.info("회원 ID: {}의 요청 처리 완료", memberId);
//            return ResponseEntity.ok(finalResponse);
//
//        } catch (Exception e) {
//            log.error("회원 ID: {}의 요청 처리 중 오류 발생", memberId, e);
//            e.printStackTrace();
//            response.put("error", "처리 중 오류가 발생했습니다");
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
    }

    // 봇 응답 생성 API
    @PostMapping("/response")
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

    @GetMapping("/{memberId}")
    public ResponseEntity<List<ChatDto>> getChatHistory(@PathVariable Long memberId) {
        List<ChatDto> chatHistory = recommendService.getChatHistory(memberId);
        return ResponseEntity.ok(chatHistory);
    }
}


