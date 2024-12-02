package com.banghyang.recommend.service;

import com.banghyang.recommend.entity.ChatImage;
import com.banghyang.recommend.repository.ChatImageRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import java.util.Map;

@Service
public class ImageProcessingService {

    @Value("${base-url}")  // FastAPI 서버 URL 설정 (application.properties에서 관리)
    private String fastApiUrl;

    private final RestTemplate restTemplate;
    private final S3Service s3Service;
    private final ChatImageRepository chatImageRepository;

    public ImageProcessingService(RestTemplate restTemplate, S3Service s3Service, ChatImageRepository chatImageRepository) {
        this.restTemplate = restTemplate;
        this.s3Service = s3Service;
        this.chatImageRepository = chatImageRepository;
    }

    // FastAPI로 이미지를 전송하여 결과를 받음
    public Map<String, Object> processImage(MultipartFile image) {
        try {
            // S3에 이미지 업로드하고 URL 받기
            String imageUrl = s3Service.uploadImage(image);

            // ChatImage 엔티티 생성 및 저장
            ChatImage chatImage = ChatImage.builder()
                    .imageUrl(imageUrl)
                    .build();
            chatImageRepository.save(chatImage);

            String url = fastApiUrl + "/image-processing/process-image";  // FastAPI의 이미지 처리 엔드포인트

            // 파일을 MultiValueMap으로 변환
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", image.getResource());

            HttpHeaders headers = new HttpHeaders();
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // FastAPI의 /process-image API 호출
            ResponseEntity<Map> responseEntity = restTemplate.postForEntity(url, requestEntity, Map.class);
            Map<String, Object> response = responseEntity.getBody();

            // 저장된 이미지의 ID와 URL을 응답에 포함
            response.put("imageUrl", imageUrl);

            // FastAPI의 응답 반환
            return response;
        } catch (Exception e) {
            throw new RuntimeException("이미지 처리 오류: " + e.getMessage());
        }
    }
}