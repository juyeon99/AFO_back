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
        // 유저가 입력한 이미지를 S3에 업로드하여 URL 을 반환받아 채팅기록에 저장할 imageUrl 을 초기화
        String userInputImageS3Url = null;
        // LLM 모델로 요청 보낼 데이터인 userInput 에 request 의 content 담기(없을시 null)
        String userInput = userRequest.getContent();

        // 1. 유저가 보낸 request 에서 이미지가 존재하면 imageToText 모델로 보내 imageProcessResult 생성하기
        // request 에서 image 파일 꺼내기
        MultipartFile userInputImage = userRequest.getImage();
        if (userInputImage != null) { // 이미지 존재할 시 처리
            // S3 URL 에 null 값 대신 업로드하고 받아온 URL 값 넣기
            userInputImageS3Url = s3Service.uploadImage(userInputImage);

            // image to text 결과
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
        }

        // 2. 유저가 보낸 request 를 MongoDB 에 채팅기록으로 저장
        Chat userChat = Chat.builder()
                .type(ChatType.USER)
                .memberId(userRequest.getMemberId())
                .content(userRequest.getContent())
                .userInputImageS3Url(userInputImageS3Url)
                .build();
        chatRepository.save(userChat);

        // 3. 생성된 userInput 을 LLM 모델로 전송하여 향수 추천 받기
        // LLM 향수 추천 결과
        PerfumeRecommendResponse perfumeRecommendResponse = webClient // api 요청에 webClient 사용
                .post()
                .uri("http://localhost:8000/llm/process-input") // 요청 보낼 url
                // contentType 필요할 시 명시적으로 추후에 작성하기
                .bodyValue(userInput) // userInput 을 요청 body 에 담아서 보내
                .retrieve()
                .bodyToMono(PerfumeRecommendResponse.class) // 응답값을 PerfumeRecommendResponse 로 매핑
                .block(); // 동기 처리

        // 4. 생성된 userInput 을 LLM 모델로 전송하여 이미지 생성 프롬프트 받기
        // LLM 이미지 생성 프롬프트 결과
        String imageGeneratePrompt = webClient // api 요청에 webClient 사용
                .post()
                .uri("http://localhost:8000/llm/generate-image-description") // 요청 보낼 url
                // contentType 필요할 시 명시적으로 추후에 작성하기
                .bodyValue(userInput) // 요청 body 에 userInput 담기
                .retrieve()
                .bodyToMono(String.class) // 응답을 String 값으로 매핑
                .block(); // 동기 처리

        if (imageGeneratePrompt != null) {
            // 5. 이미지 생성 프롬프트로 이미지 생성하고 그 이미지의 URL 받기
            byte[] generatedImageByte = webClient // api 요청에 webClient 사용
                    .post()
                    .uri("http://localhost:8000/image-generation/generate-image") // 요청 보낼 url
                    // contentType 필요할 시 명시적으로 추후에 작성하기
                    .bodyValue(imageGeneratePrompt) // 요청 body 에 이미지 생성 프롬프트 담기
                    .retrieve()
                    .bodyToMono(byte[].class) // 응답을 byte[] 형태로 받기
                    .block(); // 동기 처리

            // 6. 생성된 이미지 S3 에 저장하고 S3 URL 받기
            if (generatedImageByte != null) {
                String generatedImageUrl = s3Service
                        .byteUploadImage(generatedImageByte, "generatedImage");
            } else {
                throw new IllegalArgumentException("이미지 생성 모델 호출 중 오류가 발생했습니다.");
            }
        } else {
            throw new IllegalArgumentException("LLM 이미지 생성 프롬프트 제작 모델 호출 중 오류가 발생했습니다.");
        }

        Chat aiChat = Chat.builder()
                .type(ChatType.AI)
                .memberId(userRequest.getMemberId())
                .content(perfumeRecommendResponse.getContent())
                .
                .build();
    }
}