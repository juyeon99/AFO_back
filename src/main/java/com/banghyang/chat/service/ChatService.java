package com.banghyang.chat.service;

import com.banghyang.chat.dto.PerfumeRecommendResponse;
import com.banghyang.chat.dto.UserRequest;
import com.banghyang.chat.dto.UserResponse;
import com.banghyang.chat.entity.Chat;
import com.banghyang.chat.repository.ChatRepository;
import com.banghyang.common.type.ChatType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final WebClient webClient;
    private final S3Service s3Service;

    public UserResponse answerToUserRequest(UserRequest userRequest) {
        // 1. 유저가 보낸 입력에서 이미지가 존재하면 imageToText 모델로 보내 imageProcessResult 생성하기

        // 유저가 입력한 이미지를 S3에 업로드하여 URL 을 반환받아 채팅기록에 저장할 imageUrl 을 초기화
        String userInputImageS3Url = null;
        // LLM 모델로 요청 보낼 데이터인 userInput 에 request 의 content 담기(없을시 null)
        String userInput = userRequest.getContent();

        // request 에서 image 파일 꺼내기
        MultipartFile userInputImage = userRequest.getImage();
        if (userInputImage != null) { // 이미지 존재할 시 처리
            // S3 URL 에 null 값 대신 업로드하고 받아온 URL 값 넣기
            userInputImageS3Url = s3Service.uploadImage(userInputImage);

            try {
                // BLIP 모델로 API 요청을 보내서 받아올 결과
                String imageProcessResult = webClient // webClient 로 api 요청 보내기
                        .post() // post 요청
                        .uri("http://localhost:8000/image-processing/process-image") // 요청 보낼 url
                        .contentType(MediaType.MULTIPART_FORM_DATA) // contentType 설정
                        .bodyValue(userInputImage.getResource()) // getResource 사용하여 전송
                        .retrieve()
                        .bodyToMono(String.class) // 응답을 String 값으로 받기
                        .block(); // 동기적으로 처리

                // userInput 에 이미지 분석 결과 더하기
                userInput = userInput + " " + imageProcessResult;
            } catch (Exception e) {
                throw new IllegalArgumentException("BLIP 모델 호출 중 오류 발생 : " + e.getMessage());
            }
        }

        // 2. 유저가 보낸 request 를 MongoDB 에 채팅기록으로 저장
        Chat userChat = Chat.builder()
                .type(ChatType.USER)
                .memberId(userRequest.getMemberId())
                .content(userRequest.getContent())
                .imageUrl(userInputImageS3Url)
                .build();
        chatRepository.save(userChat);

        // 3. 생성된 userInput 을 LLM 모델로 전송하여 향수 추천 받기
        try {
            PerfumeRecommendResponse perfumeRecommendResponse = webClient
                    .get()
                    .uri("http://localhost:8000/process-input")
                    .
        }
    }
}
