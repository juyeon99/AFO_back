package com.banghyang.recommend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Service
public class RecommendService {

    private final LLMService llmService;
    private final ImageProcessingService imageProcessingService;
    private final ImageGenerationService imageGenerationService;

    // 생성자 주입
    public RecommendService(LLMService llmService, ImageProcessingService imageProcessingService, ImageGenerationService imageGenerationService) {
        this.llmService = llmService;
        this.imageProcessingService = imageProcessingService;
        this.imageGenerationService = imageGenerationService;
    }

    // 사용자 입력과 이미지를 처리하는 메서드
    public Map<String, Object> processInputAndImage(String userInput, MultipartFile image) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 1. 이미지 처리 (이미지 파일이 있으면 처리)
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
            }

            // 2. 사용자 입력 처리 (LLMService 사용)
            Map<String, Object> llmResponse = llmService.processInputFromFastAPI(userInput);
            String mode = (String) llmResponse.get("mode");

            if ("recommendation".equals(mode)) {
                // 향수 추천 및 공통 감정 처리
                response.put("recommendedPerfumes", llmResponse.get("recommended_perfumes"));
                response.put("commonFeeling", llmResponse.get("common_feeling"));

                // 3. 공통 감정에 따라 이미지 생성
                String commonFeeling = (String) llmResponse.get("common_feeling");
                String imagePrompt = (String) llmResponse.get("image_prompt"); // 이미지 프롬프트 받아오기

                // 공통 감정과 이미지 프롬프트 결합
                String prompt = " " + imagePrompt;
                System.out.println(prompt);

                // 공통 감정을 프롬프트에 포함시켜 이미지 생성 요청
                Map<String, Object> generatedImageResult = imageGenerationService.generateImage(prompt);
                response.put("generatedImage", generatedImageResult.get("output_path"));

            } else {
                // 대화 모드 처리
                String chatResponse = (String) llmResponse.get("response");
                response.put("mode", "chat");
                response.put("response", chatResponse);
            }

        } catch (Exception e) {
            response.put("error", "처리 중 오류가 발생했습니다.");
        }
        return response;
    }
}
