package com.banghyang.recommend.service;

import com.banghyang.recommend.entity.ChatImage;
import com.banghyang.recommend.repository.ChatImageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@Slf4j
public class ImageGenerationService {

    @Value("${base-url}")  // FastAPI 서버 URL 설정 (application.properties에서 관리)
    private String fastApiUrl;

    private final RestTemplate restTemplate;
    private final S3Service s3Service;
    private final ChatImageRepository chatImageRepository;

    public ImageGenerationService(RestTemplate restTemplate, S3Service s3Service, ChatImageRepository chatImageRepository) {
        this.restTemplate = restTemplate;
        this.s3Service = s3Service;
        this.chatImageRepository = chatImageRepository;
    }

    // FastAPI로 이미지를 전송하여 결과를 받음
    public Map<String, Object> generateImage(String prompt) {
        try {
            String url = fastApiUrl + "/image-generation/generate-image";
            log.info("전송하는 프롬프트: {}", prompt);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // JSON 요청 바디 생성
            ObjectMapper mapper = new ObjectMapper();
            String jsonBody = mapper.writeValueAsString(Map.of("prompt", prompt));

            HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);

            log.info("전송하는 JSON: {}", jsonBody);

            ResponseEntity<Map> responseEntity = restTemplate.postForEntity(url, requestEntity, Map.class);

            if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("FastAPI 서버 응답 실패");
            }

            Map<String, Object> response = responseEntity.getBody();
            log.info("FastAPI 응답: {}", response);

            // output_path가 Map인 경우를 처리
            Object outputPathObj = response.get("output_path");
            String generatedImagePath;

            if (outputPathObj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, String> outputPathMap = (Map<String, String>) outputPathObj;
                generatedImagePath = outputPathMap.get("output_path");
            } else if (outputPathObj instanceof String) {
                generatedImagePath = (String) outputPathObj;
            } else {
                throw new RuntimeException("Unexpected output_path format");
            }

            // generatedImageUrl 생성
            String generatedImageUrl = fastApiUrl;
            if (fastApiUrl.endsWith("/") && generatedImagePath.startsWith("/")) {
                generatedImageUrl += generatedImagePath.substring(1);
            } else if (!fastApiUrl.endsWith("/") && !generatedImagePath.startsWith("/")) {
                generatedImageUrl += "/" + generatedImagePath;
            } else {
                generatedImageUrl += generatedImagePath;
            }

            ResponseEntity<byte[]> imageResponse = restTemplate.getForEntity(generatedImageUrl, byte[].class);

            if (!imageResponse.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("이미지 다운로드 실패");
            }

            byte[] imageBytes = imageResponse.getBody();
            if (imageBytes == null || imageBytes.length == 0) {
                throw new RuntimeException("이미지 데이터가 비어있습니다");
            }

            String s3ImageUrl = s3Service.byteUploadImage(imageBytes, "generated-image.png");

            ChatImage chatImage = ChatImage.builder()
                    .imageUrl(s3ImageUrl)
                    .build();
            chatImageRepository.save(chatImage);
            log.info("데이터베이스에 이미지 정보 저장 완료");

            response.put("s3_url", s3ImageUrl);
            return response;

        } catch (Exception e) {
            log.error("이미지 생성 중 오류 발생. URL: {}, 에러: {}", fastApiUrl, e.getMessage());
            throw new RuntimeException("이미지 생성 오류: " + e.getMessage());
        }
    }
}