package com.banghyang.chat.service;

import com.banghyang.chat.dto.PerfumeRecommendResponse;
import com.banghyang.chat.dto.UserRequest;
import com.banghyang.chat.dto.UserResponse;
import com.banghyang.chat.entity.Chat;
import com.banghyang.chat.repository.ChatRepository;
import com.banghyang.common.type.ChatMode;
import com.banghyang.common.type.ChatType;
import com.banghyang.object.mapper.Mapper;
import com.banghyang.object.perfume.entity.Perfume;
import com.banghyang.object.perfume.repository.PerfumeRepository;
import com.banghyang.common.util.ValidUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final WebClient webClient;
    private final S3Service s3Service;
    private final PerfumeRepository perfumeRepository;

    /**
     * 유저 채팅에 답변하는 서비스 메소드
     */
    public UserResponse answerToUserRequest(UserRequest userRequest) {
        // request 에서 image 파일 꺼내기
        MultipartFile userInputImage = userRequest.getImage();

        // 유저 입력값에서 이미지가 있는지 없는지 검증
        if (userInputImage != null) { // 유저가 보낸 이미지가 있을 때의 처리
            // 유저가 입력한 이미지를 S3 에 저장하고 S3 URL 받기
            String userInputImageS3Url = s3Service.uploadImage(userInputImage);
            // 유저 입력 이미지의 S3 URL 값 없을 시 예외 발생시키기
            if (!ValidUtils.isNotBlank(userInputImageS3Url)) {
                throw new IllegalArgumentException("AWS S3에 유저 입력 이미지 저장을 실패했습니다.");
            }

            // image to text model 로 전송하여 이미지 분석 결과 받기
            String imageProcessResult = webClient // webClient 로 api 요청 보내기
                    .post() // post 요청
                    .uri("http://localhost:8000/image-processing/process-image") // 요청 보낼 url
                    .contentType(MediaType.MULTIPART_FORM_DATA) // contentType 설정
                    .bodyValue(userInputImage.getResource()) // getResource 사용하여 전송
                    .retrieve()
                    .bodyToMono(String.class) // 응답을 String 값으로 받기
                    .block(); // 동기적으로 처리
            // 이미지 분석 결과 없을 시 예외 발생시키기
            if (!ValidUtils.isNotBlank(imageProcessResult)) {
                throw new IllegalArgumentException("이미지 분석 결과 생성을 실패했습니다.");
            }

            // 유저가 보낸 request 를 MongoDB 에 채팅기록으로 저장
            Chat userChat = Chat.builder()
                    .type(ChatType.USER)
                    .memberId(userRequest.getMemberId())
                    .content(userRequest.getContent())
                    .userInputImageS3Url(userInputImageS3Url)
                    .build();
            chatRepository.save(userChat);

            // LLM 모델로 전송할 userInput 만들기(유저 입력 텍스트 + 유저 입력 이미지 분석 결과)
            String userInput = imageProcessResult + " " + userRequest.getContent();

            //  유저 텍스트 입력값과 이미지 분석 결과를 LLM 모델로 전송하여 향수 추천 받기
            PerfumeRecommendResponse perfumeRecommendResponse = getPerfumeRecommendFromLLM(userInput);
            // 생성된 추천 결과 필드값 중 빈 필드가 있으면 예외 발생 시키기
            ValidUtils.validPerfumeRecommendResponseFields(perfumeRecommendResponse);

            // 향수 추천 결과의 recommendation -> 채팅기록 저장 엔티티의 recommendation 으로 변환하는 메소드
            List<Chat.Recommendation> recommendations = mapAiRecommendationsToChatRecommendations(
                    perfumeRecommendResponse.getRecommendations()
            );

            // 유저 텍스트 입력값과 이미지 분석 결과를 LLM 모델로 전송하여 이미지 생성 프롬프트 받기
            String imageGeneratePrompt = getImageGeneratePromptFromLLM(userInput);
            // 이미지 프롬프트 값이 없으면 예외 발생시키기
            if (!ValidUtils.isNotBlank(imageGeneratePrompt)) {
                throw new IllegalArgumentException("이미지 프롬프트 생성을 실패했습니다.");
            }

            // 이미지 생성 프롬프트를 AI API 로 전송하여 이미지 생성하고 그 이미지를 byte[] 형식으로 받기
            byte[] generatedImageByte = getGeneratedImageByteFromStableDiffusion(imageGeneratePrompt);
            // 이미지 생성 byte[] 값이 없으면 예외 발생시키기
            if (generatedImageByte == null) {
                throw new IllegalArgumentException("AI 이미지 생성을 실패했습니다.");
            }

            // 생성된 byte[] 형식의 이미지를 S3 에 저장하고 S3 URL 받기
            String generatedImageS3Url = s3Service.byteUploadImage(generatedImageByte, "generatedImage");
            // S3에 생성된 이미지 저장 후 url 이 반환되지 않으면 예외 발생시키기
            if (!ValidUtils.isNotBlank(generatedImageS3Url)) {
                throw new IllegalArgumentException("S3에 AI 생성 이미지 저장을 실패했습니다.");
            }

            // AI API 들에게서 받아온 값으로 AI 채팅 기록 만들어 MongoDB에 저장하기
            Chat aiChat = Chat.builder()
                    .type(ChatType.AI)
                    .memberId(userRequest.getMemberId())
                    .content(perfumeRecommendResponse.getContent())
                    .mode(perfumeRecommendResponse.getMode())
                    .lineId(perfumeRecommendResponse.getLineId())
                    .generatedImageS3Url(generatedImageS3Url)
                    .recommendations(recommendations)
                    .build();
            chatRepository.save(aiChat);

            // UserResponse 에 생성된 값들 담기
            UserResponse userResponse = new UserResponse();
            userResponse.setId(aiChat.getId());
            userResponse.setMode(perfumeRecommendResponse.getMode());
            userResponse.setContent(perfumeRecommendResponse.getContent());
            userResponse.setLineId(perfumeRecommendResponse.getLineId());
            userResponse.setGeneratedImageS3Url(generatedImageS3Url);
            userResponse.setRecommendations(recommendations);
            // 값들 담은 userResponse 반환
            return userResponse;

        } else {
            // 이미지 없을 때의 처리
            if (userRequest.getContent() != null) { // 이미지는 없지만 텍스트 입력값은 있을 때의 처리
                // 유저가 보낸 내용을 MongoDB에 채팅기록으로 저장하기
                Chat userChat = Chat.builder()
                        .type(ChatType.USER)
                        .memberId(userRequest.getMemberId())
                        .content(userRequest.getContent())
                        .build();
                chatRepository.save(userChat);

                // 유저 텍스트 입력값을 LLM 모델로 전송하여 향수 추천 받기
                PerfumeRecommendResponse perfumeRecommendResponse = getPerfumeRecommendFromLLM(
                        userRequest.getContent()); // 이미지가 없으므로 입력 텍스트값만 LLM 으로 전송

                if (perfumeRecommendResponse.getMode() == ChatMode.recommendation) { // 답변이 추천 모드일 때의 처리
                    // 생성된 추천 결과 필드값 중 빈 필드가 있으면 예외 발생 시키기
                    ValidUtils.validPerfumeRecommendResponseFields(perfumeRecommendResponse);

                    // 향수 추천 결과의 recommendation -> 채팅기록 저장 엔티티의 recommendation 으로 변환하는 메소드
                    List<Chat.Recommendation> recommendations = mapAiRecommendationsToChatRecommendations(
                            perfumeRecommendResponse.getRecommendations()
                    );

                    // 유저 텍스트 입력값을 LLM 모델로 전송하여 이미지 생성 프롬프트 받기
                    String imageGeneratePrompt = getImageGeneratePromptFromLLM(userRequest.getContent());
                    // 이미지 프롬프트 값이 없으면 예외 발생시키기
                    if (!ValidUtils.isNotBlank(imageGeneratePrompt)) {
                        throw new IllegalArgumentException("이미지 프롬프트 생성을 실패했습니다.");
                    }

                    // 이미지 생성 프롬프트를 AI API 로 전송하여 이미지 생성하고 그 이미지를 byte[] 형식으로 받기
                    byte[] generatedImageByte = getGeneratedImageByteFromStableDiffusion(imageGeneratePrompt);
                    // 이미지 생성 byte[] 값이 없으면 예외 발생시키기
                    if (generatedImageByte == null) {
                        throw new IllegalArgumentException("AI 이미지 생성을 실패했습니다.");
                    }

                    // 생성된 byte[] 형식의 이미지를 S3 에 저장하고 S3 URL 받기
                    String generatedImageS3Url = s3Service.byteUploadImage(generatedImageByte, "generatedImage");
                    // S3에 생성된 이미지 저장 후 url 이 반환되지 않으면 예외 발생시키기
                    if (!ValidUtils.isNotBlank(generatedImageS3Url)) {
                        throw new IllegalArgumentException("S3에 AI 생성 이미지 저장을 실패했습니다.");
                    }

                    // AI API 들에게서 받아온 값으로 AI 채팅 기록 만들어 MongoDB에 저장하기
                    Chat aiChat = Chat.builder()
                            .type(ChatType.AI)
                            .mode(perfumeRecommendResponse.getMode())
                            .memberId(userRequest.getMemberId())
                            .content(perfumeRecommendResponse.getContent())
                            .lineId(perfumeRecommendResponse.getLineId())
                            .generatedImageS3Url(generatedImageS3Url)
                            .recommendations(recommendations)
                            .build();
                    chatRepository.save(aiChat);

                    // UserResponse 에 AI 로 생성된 값들 담기
                    UserResponse userResponse = new UserResponse();
                    userResponse.setId(aiChat.getId());
                    userResponse.setMode(perfumeRecommendResponse.getMode());
                    userResponse.setContent(perfumeRecommendResponse.getContent());
                    userResponse.setLineId(perfumeRecommendResponse.getLineId());
                    userResponse.setGeneratedImageS3Url(generatedImageS3Url);
                    userResponse.setRecommendations(recommendations);
                    // 값을 담은 userResponse 반환
                    return userResponse;

                } else { // 답변이 일반 모드일 때의 처리
                    if (userRequest.getContent() != null) {
                        Chat aiChat = Chat.builder()
                                .type(ChatType.AI)
                                .mode(perfumeRecommendResponse.getMode())
                                .memberId(userRequest.getMemberId())
                                .content(userRequest.getContent())
                                .build();
                        chatRepository.save(aiChat);

                        // UserResponse 에 AI 답변 값들 담기
                        UserResponse userResponse = new UserResponse();
                        userResponse.setId(aiChat.getId());
                        userResponse.setMode(perfumeRecommendResponse.getMode());
                        userResponse.setContent(perfumeRecommendResponse.getContent());
                        // 값을 담은 userResponse 반환
                        return userResponse;
                    } else {
                        throw new IllegalArgumentException("AI의 일반 답변 생성 중 오류가 발생했습니다.");
                    }
                }
            } else {
                throw new IllegalArgumentException("입력한 값이 없습니다.");
            }
        }
    }

    /**
     * webClient 로 LLM API 에 향수 추천 결과 요청하는 메소드
     */
    private PerfumeRecommendResponse getPerfumeRecommendFromLLM(String userInput) {
        return webClient // api 요청에 webClient 사용
                .post()
                .uri("http://localhost:8000/llm/process-input") // 향수 추천 api 요청 url
                .bodyValue(userInput) // 요청 body 에 user 입력값 담아서 보내기
                .retrieve()
                .bodyToMono(PerfumeRecommendResponse.class)
                .block();
    }

    /**
     * LLM 에 유저 입력값을 보내서 이미지 생성 프롬프트를 요청하는 메소드
     */
    private String getImageGeneratePromptFromLLM(String userInput) {
        return webClient // api 요청에 webClient 사용
                .post()
                .uri("http://localhost:8000/llm/generate-image-description") // 요청 보낼 url
                // contentType 필요할 시 명시적으로 추후에 작성하기
                .bodyValue(userInput) // 요청 body 에 userInput 담기
                .retrieve()
                .bodyToMono(String.class) // 응답을 String 값으로 매핑
                .block(); // 동기 처리
    }

    /**
     * Stable Diffusion API 에 이미지 생성 프롬프트를 전송하여 생성된 이미지를 byte[] 형식으로 받아오는 메소드
     */
    private byte[] getGeneratedImageByteFromStableDiffusion(String imageGeneratePrompt) {
        return webClient // api 요청에 webClient 사용
                .post()
                .uri("http://localhost:8000/image-generation/generate-image") // 요청 보낼 url
                // contentType 필요할 시 명시적으로 추후에 작성하기
                .bodyValue(imageGeneratePrompt) // 요청 body 에 이미지 생성 프롬프트 담기
                .retrieve()
                .bodyToMono(byte[].class) // 응답을 byte[] 형태로 받기
                .block(); // 동기 처리
    }

    /**
     * AI 가 답변해준 추천 중 향수 아이디로 향수 엔티티를 찾아 채팅 기록에 담을 추천 향수 정보로 변환하는 메소드
     */
    private List<Chat.Recommendation> mapAiRecommendationsToChatRecommendations(
            List<PerfumeRecommendResponse.Recommendation> aiRecommendations
    ) {
        return aiRecommendations.stream() // stream 으로 AI 의 향수 추천 리스트의 모든 항목에 접근
                .map(aiRecommendation -> {
                    // 향수 추천 리스트의 향수 아이디로 향수 엔티티 가져오기
                    Perfume recommendedPerfumeEntity = perfumeRepository
                            .findById(Long.valueOf(aiRecommendation.getId()))
                            .orElseThrow(() -> new RuntimeException(
                                    "추천된 향수 아이디에 해당하는 향수 정보를 찾을 수 없습니다."));

                    // Mapper 클래스의 향수엔티티 -> 채팅추천정보 변환 매퍼로 정보 담아서 반환
                    return Mapper.mapPerfumeEntityToChatRecommendation(
                            recommendedPerfumeEntity
                            , aiRecommendation.getReason(),
                            aiRecommendation.getSituation());
                }).toList(); // 변환한 객체들 리스트로 담기
    }
}