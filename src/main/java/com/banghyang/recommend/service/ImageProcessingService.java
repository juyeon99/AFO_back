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
            String imageUrl = s3Service.uploadImage(image);

            ChatImage chatImage = ChatImage.builder()
                    .imageUrl(imageUrl)
                    .build();
            chatImageRepository.save(chatImage);

            String url = fastApiUrl + "/image-processing/process-image";

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", image.getResource());

            HttpHeaders headers = new HttpHeaders();
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<Map> responseEntity = restTemplate.postForEntity(url, requestEntity, Map.class);
            Map<String, Object> response = responseEntity.getBody();

            if (response == null) {
                throw new RuntimeException("FastAPI 응답이 null입니다.");
            }

            // 응답에 imageUrl 추가
            response.put("imageUrl", imageUrl);

            return response;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("FastAPI 호출 오류: " + e.getMessage());
        }
    }

}