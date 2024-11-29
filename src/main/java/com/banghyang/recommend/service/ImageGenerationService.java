package com.banghyang.recommend.service;

import com.banghyang.recommend.entity.ChatImage;
import com.banghyang.recommend.repository.ChatImageRepository;
import lombok.extern.slf4j.Slf4j;
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
            String url = fastApiUrl + "/image-generation/generate-image";  // FastAPI의 이미지 생성 엔드포인트

            // 텍스트 프롬프트 데이터를 MultiValueMap으로 변환
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("prompt", prompt);

            HttpHeaders headers = new HttpHeaders();
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // FastAPI의 /generate-image API 호출
            ResponseEntity<Map> responseEntity = restTemplate.postForEntity(url, requestEntity, Map.class);
            if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("FastAPI 서버 응답 실패");
            }

            Map<String, Object> response = responseEntity.getBody();

            // output_path가 Map 형태로 들어있으므로 이를 처리
            Map<String, String> pathInfo = (Map<String, String>) response.get("output_path");
            String generatedImagePath = pathInfo.get("output_path");

            // generatedImageUrl을 생성할 때 슬래시 중복 방지 처리
            String generatedImageUrl = fastApiUrl;
            if (fastApiUrl.endsWith("/") && generatedImagePath.startsWith("/")) {
                generatedImageUrl += generatedImagePath.substring(1);  // 첫 번째 슬래시 제거
            } else if (!fastApiUrl.endsWith("/") && !generatedImagePath.startsWith("/")) {
                generatedImageUrl += "/" + generatedImagePath;  // 슬래시 추가
            } else {
                generatedImageUrl += generatedImagePath;  // 그대로 사용
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
            throw new RuntimeException("이미지 생성 오류: " + e.getMessage());
        }
    }
}
