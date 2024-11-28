package com.banghyang.recommend.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import java.util.Map;

@Service
public class ImageGenerationService {

    @Value("${base-url}")  // FastAPI 서버 URL 설정 (application.properties에서 관리)
    private String fastApiUrl;

    private final RestTemplate restTemplate;

    public ImageGenerationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // FastAPI로 이미지를 전송하여 결과를 받음
    public Map<String, Object> generateImage(String prompt) {
        try {
            String url = fastApiUrl + "/generate-image";  // FastAPI의 이미지 생성 엔드포인트

            // 텍스트 프롬프트 데이터를 MultiValueMap으로 변환
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("prompt", prompt);

            HttpHeaders headers = new HttpHeaders();
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // FastAPI의 /generate-image API 호출
            ResponseEntity<Map> responseEntity = restTemplate.postForEntity(url, requestEntity, Map.class);

            // FastAPI의 응답 반환
            return responseEntity.getBody();
        } catch (Exception e) {
            throw new RuntimeException("이미지 생성 오류: " + e.getMessage());
        }
    }
}
