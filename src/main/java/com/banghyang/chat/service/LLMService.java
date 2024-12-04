package com.banghyang.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class LLMService {

    @Value("${base-url}") // FastAPI 서버 URL 을 외부 설정으로 관리(application.yml)
    private String fastApiUrl;

    private final RestTemplate restTemplate;

    // 사용자 입력을 처리하여 FastAPI 에 요청을 보냄
    public Map<String, Object> processInputFromFastAPI(String userInput) {
        try {
            // FastAPI 의 /process-input 엔드포인트 호출
            String url = fastApiUrl + "/llm/process-input";  // FastAPI 서버의 엔드포인트
            Map<String, String> requestBody = Map.of("user_input", userInput);

            // FastAPI 의 /process-input API 를 호출
            ResponseEntity<Map> responseEntity = restTemplate.postForEntity(url, requestBody, Map.class);

            // FastAPI 로부터 받은 응답
            return responseEntity.getBody();
        } catch (Exception e) {
            throw new RuntimeException("FastAPI 호출 오류: " + e.getMessage());
        }
    }
}

