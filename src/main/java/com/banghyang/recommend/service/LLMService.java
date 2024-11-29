package com.banghyang.recommend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@Service
public class LLMService {

    @Value("${base-url}") // FastAPI 서버 URL을 외부 설정으로 관리
    private String fastApiUrl;

    private final RestTemplate restTemplate;

    public LLMService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // 사용자 입력을 처리하여 FastAPI에 요청을 보냄
    public Map<String, Object> processInputFromFastAPI(String userInput) {
        try {
            // FastAPI의 /process-input 엔드포인트 호출
            String url = fastApiUrl + "/llm/process-input";  // FastAPI 서버의 엔드포인트
            Map<String, String> requestBody = Map.of("user_input", userInput);

            // FastAPI의 /process-input API를 호출
            ResponseEntity<Map> responseEntity = restTemplate.postForEntity(url, requestBody, Map.class);

            // FastAPI로부터 받은 응답
            return responseEntity.getBody();
        } catch (Exception e) {
            throw new RuntimeException("FastAPI 호출 오류: " + e.getMessage());
        }
    }
}

