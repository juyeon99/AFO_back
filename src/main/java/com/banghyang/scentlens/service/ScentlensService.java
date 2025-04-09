package com.banghyang.scentlens.service;

import com.banghyang.scentlens.dto.ProductResponse;
import com.banghyang.scentlens.dto.WrappedProductResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ScentlensService {
    private final WebClient webClient;

    @Value("${api.base-url}")
    private String baseUrl;

    public List<ProductResponse> getImageSearchResult(MultipartFile file, String language) {
        try {
            MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
            bodyBuilder.part("file", file.getResource());
            bodyBuilder.part("language", language)
                    .contentType(MediaType.TEXT_PLAIN);

            WrappedProductResponse wrappedResponse = webClient.post()
                    .uri(baseUrl + "/scentlens/get_image_search_result")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(bodyBuilder.build()))
                    .retrieve()
                    .bodyToMono(WrappedProductResponse.class)
                    .block();

            return wrappedResponse != null ? wrappedResponse.getProducts() : null;
        } catch (Exception e) {
            throw new RuntimeException("이미지 검색 요청 중 에러 발생: " + e.getMessage());
        }
    }
}
