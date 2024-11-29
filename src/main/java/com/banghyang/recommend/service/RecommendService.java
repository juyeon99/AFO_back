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
            // 1. 사용자 입력 처리 (LLMService 사용)
            Map<String, Object> llmResponse = llmService.processInputFromFastAPI(userInput);
            String mode = (String) llmResponse.get("mode");

            if ("recommendation".equals(mode)) {
                // 향수 추천 및 공통 감정 처리
                response.put("recommendedPerfumes", llmResponse.get("recommended_perfumes"));
                response.put("commonFeeling", llmResponse.get("common_feeling"));

                // 2. 이미지 처리 (이미지 파일이 있으면 처리)
                if (image != null) {
                    Map<String, Object> imageProcessingResult = imageProcessingService.processImage(image);
                    response.put("imageProcessed", imageProcessingResult);
                }

                // 3. 공통 감정에 따라 이미지 생성
                String prompt = "공통 감정: " + llmResponse.get("common_feeling");
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
