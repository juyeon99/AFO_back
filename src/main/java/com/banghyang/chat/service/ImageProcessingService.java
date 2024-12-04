package com.banghyang.chat.service;

import com.banghyang.chat.repository.ChatImageRepository;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class ImageProcessingService {

    @Value("${base-url}")  // FastAPI 서버 URL 설정 (application.yml 에서 관리)
    private String fastApiUrl;

    private final RestTemplate restTemplate;
    private final S3Service s3Service;
    private final ChatImageRepository chatImageRepository;

    // FastAPI 로 이미지를 전송하여 결과를 받음
    public Map<String, Object> processImage(MultipartFile image) {
        try {
            String imageUrl = s3Service.uploadImage(image);

            // 생성된 이미지는 url 만 필요하고 객체로써의 저장을 필요없다고 판단하여 삭제
//            ChatImage chatImage = ChatImage.builder()
//                    .imageUrl(imageUrl)
//                    .build();
//            chatImageRepository.save(chatImage);

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