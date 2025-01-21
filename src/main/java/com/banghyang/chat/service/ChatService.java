package com.banghyang.chat.service;

import com.banghyang.chat.dto.PerfumeRecommendResponse;
import com.banghyang.chat.dto.UserRequest;
import com.banghyang.chat.dto.UserResponse;
import com.banghyang.chat.entity.Chat;
import com.banghyang.chat.repository.ChatRepository;
import com.banghyang.common.mapper.Mapper;
import com.banghyang.common.type.ChatMode;
import com.banghyang.common.type.ChatType;
import com.banghyang.object.perfume.entity.Perfume;
import com.banghyang.object.perfume.repository.PerfumeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.File;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final WebClient webClient;
    private final S3Service s3Service;
    private final PerfumeRepository perfumeRepository;

    public List<UserResponse> getAllChats(Long memberId) {
        // memberId 에 해당하는 모든 채팅 기록 가져오기
        List<Chat> chatEntityList = chatRepository.findChatByMemberId(memberId);
        return chatEntityList.stream().map(chatEntity -> {
            UserResponse userResponse = new UserResponse();
            userResponse.setId(chatEntity.getId());
            userResponse.setMemberId(chatEntity.getMemberId());
            userResponse.setType(chatEntity.getType());
            userResponse.setMode(chatEntity.getMode());
            userResponse.setContent(chatEntity.getContent());
            userResponse.setLineId(chatEntity.getLineId());
            userResponse.setImageUrl(chatEntity.getImageUrl());
            userResponse.setRecommendations(chatEntity.getRecommendations());
            userResponse.setTimeStamp(chatEntity.getTimeStamp());
            return userResponse;
        }).toList();
    }

    /**
     * 유저 채팅에 답변하는 서비스 메소드
     */
    public UserResponse answerToUserRequest(UserRequest userRequest) {
        System.out.println("[채팅 서비스] 유저 답변 생성 메소드 진입...");
        System.out.println("[채팅 서비스] 유저 입력 정보 : " + userRequest.toString());

        if (userRequest.getImage() != null) {
            // 유저가 보낸 이미지가 있을 때의 처리
            System.out.println("이미지 포함 입력 처리 진입");

            // 유저가 입력한 이미지를 S3 에 저장하고 S3 URL 받기
            String userInputImageS3Url = s3Service.uploadImage(userRequest.getImage());
//            System.out.println("[이미지입력향수]사용자 입력 이미지 저장 S3 url : " + userInputImageS3Url);

            // image to text model 로 전송하여 이미지 분석 결과 받기
            String imageProcessResult = getImageToTextProcessResult(userRequest.getImage());
//            System.out.println("[이미지입력향수]BLIP 모델 이미지 분석 결과 : " + imageProcessResult);

            // 유저가 보낸 request 를 MongoDB 에 채팅기록으로 저장
            Chat userChat = Chat.builder()
                    .type(ChatType.USER)
                    .memberId(userRequest.getMemberId())
                    .content(userRequest.getContent())
                    .imageUrl(userInputImageS3Url)
                    .build();
            chatRepository.save(userChat);

            // LLM 모델로 전송할 userInput 만들기(유저 입력 텍스트 + 유저 입력 이미지 분석 결과)
            String userInput = imageProcessResult + " " + userRequest.getContent();
//            System.out.println("[이미지입력향수]LLM 모델로 전송할 최종 userInput : " + userInput);

            //  유저 텍스트 입력값과 이미지 분석 결과를 LLM 모델로 전송하여 향수 추천 받기
            PerfumeRecommendResponse perfumeRecommendResponse = getPerfumeRecommendFromLLM(userInput);
//            System.out.println("[이미지입력향수]LLM 모델의 향수 추천 답변 내용 : " + perfumeRecommendResponse.toString());

            // 향수 추천 결과의 recommendation -> 채팅기록 저장 엔티티의 recommendation 으로 변환하는 메소드
            List<Chat.Recommendation> recommendations = mapAiRecommendationsToChatRecommendations(
                    perfumeRecommendResponse.getRecommendations()
            );

            // 유저 텍스트 입력값과 이미지 분석 결과를 LLM 모델로 전송하여 이미지 생성 프롬프트 받기
            String imageGeneratePrompt = getImageGeneratePromptFromLLM(userInput);
            System.out.println("[이미지입력향수]LLM 모델이 생성해준 이미지 프롬프트 : " + imageGeneratePrompt);

            // AI 가 생성한 이미지의 저장경로로 이미지 파일을 가져오고 byte[] 로 형변환하여 반환하는 메소드
            byte[] generatedImageByte = getGeneratedImageByteFromStableDiffusion(imageGeneratePrompt);
//            System.out.println("[이미지입력향수]AI 생성 이미지 byte[] : " + generatedImageByte.length);

            // 생성된 byte[] 형식의 이미지를 S3 에 저장하고 S3 URL 받기
            String generatedImageS3Url = s3Service.byteUploadImage(generatedImageByte, "generatedImage");
//            System.out.println("[이미지입력향수]AI 생성 이미지 저장 S3 URL : " + generatedImageS3Url);

            // AI API 들에게서 받아온 값으로 AI 채팅 기록 만들어 MongoDB에 저장하기
            Chat aiChat = Chat.builder()
                    .type(ChatType.AI)
                    .mode(perfumeRecommendResponse.getMode())
                    .memberId(userRequest.getMemberId())
                    .content(perfumeRecommendResponse.getContent())
                    .lineId(perfumeRecommendResponse.getLineId())
                    .imageUrl(generatedImageS3Url)
                    .recommendations(recommendations)
                    .build();
            chatRepository.save(aiChat);

            // UserResponse 에 생성된 값들 담기
            UserResponse userResponse = new UserResponse();
            userResponse.setId(aiChat.getId());
            userResponse.setMemberId(aiChat.getMemberId());
            userResponse.setType(aiChat.getType());
            userResponse.setMode(perfumeRecommendResponse.getMode());
            userResponse.setContent(perfumeRecommendResponse.getContent());
            userResponse.setLineId(perfumeRecommendResponse.getLineId());
            userResponse.setImageUrl(generatedImageS3Url);
            userResponse.setRecommendations(recommendations);
            userResponse.setTimeStamp(aiChat.getTimeStamp());
            System.out.println("[이미지입력향수]사용자에게 보낼 최종 response 값 : " + userResponse.toString());
            // 값들 담은 userResponse 반환
            return userResponse;

        } else {
            // 이미지 없을 때의 처리
            System.out.println("이미지 미포함 입력 처리 진입");

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

                if (perfumeRecommendResponse.getMode() == ChatMode.recommendation) {
                    // 답변이 추천 모드일 때의 처리

                    // 향수 추천 결과의 recommendation -> 채팅기록 저장 엔티티의 recommendation 으로 변환하는 메소드
                    List<Chat.Recommendation> recommendations = mapAiRecommendationsToChatRecommendations(
                            perfumeRecommendResponse.getRecommendations());

                    // 유저 텍스트 입력값을 LLM 모델로 전송하여 이미지 생성 프롬프트 받기
                    String imageGeneratePrompt = getImageGeneratePromptFromLLM(userRequest.getContent());

                    // 이미지 생성 프롬프트를 AI API 로 전송하여 이미지 생성하고 그 이미지를 byte[] 형식으로 받기
                    byte[] generatedImageByte = getGeneratedImageByteFromStableDiffusion(imageGeneratePrompt);

                    // 생성된 byte[] 형식의 이미지를 S3 에 저장하고 S3 URL 받기
                    String generatedImageS3Url = s3Service.byteUploadImage(
                            generatedImageByte, "generatedImage");


                    // AI API 들에게서 받아온 값으로 AI 채팅 기록 만들어 MongoDB에 저장하기
                    Chat aiChat = Chat.builder()
                            .type(ChatType.AI)
                            .mode(perfumeRecommendResponse.getMode())
                            .memberId(userRequest.getMemberId())
                            .content(perfumeRecommendResponse.getContent())
                            .lineId(perfumeRecommendResponse.getLineId())
                            .imageUrl(generatedImageS3Url)
                            .recommendations(recommendations)
                            .build();
                    chatRepository.save(aiChat);

                    // UserResponse 에 AI 로 생성된 값들 담기
                    UserResponse userResponse = new UserResponse();
                    userResponse.setId(aiChat.getId());
                    userResponse.setType(aiChat.getType());
                    userResponse.setMemberId(aiChat.getMemberId());
                    userResponse.setMode(perfumeRecommendResponse.getMode());
                    userResponse.setContent(perfumeRecommendResponse.getContent());
                    userResponse.setLineId(perfumeRecommendResponse.getLineId());
                    userResponse.setImageUrl(generatedImageS3Url);
                    userResponse.setRecommendations(recommendations);
                    userResponse.setTimeStamp(aiChat.getTimeStamp());
                    // 값을 담은 userResponse 반환
                    return userResponse;

                } else {
                    // 답변이 일반 모드일 때의 처리
                    System.out.println("일반 답변 모드 진입");

                    Chat aiChat = Chat.builder()
                            .type(ChatType.AI)
                            .mode(perfumeRecommendResponse.getMode())
                            .memberId(userRequest.getMemberId())
                            .content(perfumeRecommendResponse.getContent())
                            .build();
                    chatRepository.save(aiChat);

                    // UserResponse 에 AI 답변 값들 담기
                    UserResponse userResponse = new UserResponse();
                    userResponse.setId(aiChat.getId());
                    userResponse.setType(aiChat.getType());
                    userResponse.setMemberId(aiChat.getMemberId());
                    userResponse.setMode(perfumeRecommendResponse.getMode());
                    userResponse.setContent(perfumeRecommendResponse.getContent());
                    userResponse.setTimeStamp(aiChat.getTimeStamp());
                    // 값을 담은 userResponse 반환
                    return userResponse;
                }
            } else {
                throw new IllegalArgumentException("입력한 값이 없습니다.");
            }
        }
    }

    /**
     * 이미지에 대한 설명을 반환해주는 BLIP 모델에 API 요청을 보내는 메소드
     */
    private String getImageToTextProcessResult(MultipartFile image) {
        try {
            // 요청 보낼 request 생성
            MultiValueMap<String, Object> request = new LinkedMultiValueMap<>();
            // file 이라는 이름으로 사용자 입력 이미지를 보낸다.
            request.add("file", image.getResource());
            System.out.println("이미지 설명 분석 리퀘스트 값 확인 : " + request.toString());

            Map<String, String> response = webClient // webClient 로 api 요청 보내기
                    .post() // post 요청
                    .uri("http://localhost:8000/image-processing/process-image") // 요청 보낼 url
                    .contentType(MediaType.MULTIPART_FORM_DATA) // contentType 설정
                    .bodyValue(request) // 요청 body 에 request 담기
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {
                    }) // Json 응답을 Map 형태로 파싱하여 받기
                    .block(); // 동기적으로 처리
            if (response != null) {
                // Map 에서 키값으로 해당 밸류 반환
                return response.get("imageProcessResult");
            } else {
                throw new IllegalArgumentException("이미지 설명 추출을 실패했습니다.");
            }
        } catch (Exception e) {
            throw new RuntimeException("BLIP 모델 호출 중 에러 발생 : " + e.getMessage());
        }
    }

    /**
     * webClient 로 LLM API 에 향수 추천 결과 요청하는 메소드
     */
    private PerfumeRecommendResponse getPerfumeRecommendFromLLM(String userInput) {
        try {
            // 요청 보낼 리퀘스트 생성
            Map<String, String> request = new LinkedHashMap<>();
            request.put("user_input", userInput);

            return webClient // api 요청에 webClient 사용
                    .post()
                    .uri("http://localhost:8000/llm/process-input") // 향수 추천 api 요청 url
                    .contentType(MediaType.APPLICATION_JSON) // Json 형태로 요청 보내기
                    .bodyValue(request) // 요청 body 에 request 담아서 보내기
                    .retrieve()
                    .bodyToMono(PerfumeRecommendResponse.class)
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("LLM 향수 추천 모델 호출 중 에러 발생 : " + e.getMessage());
        }
    }

    /**
     * LLM 에 유저 입력값을 보내서 이미지 생성 프롬프트를 요청하는 메소드
     */
    private String getImageGeneratePromptFromLLM(String userInput) {
        try {
            // 요청 보낼 리퀘스트 생성
            Map<String, String> request = new LinkedHashMap<>();
            request.put("user_input", userInput);

            Map<String, String> response = webClient // api 요청에 webClient 사용
                    .post()
                    .uri("http://localhost:8000/llm/generate-image-description") // 요청 보낼 url
                    .contentType(MediaType.APPLICATION_JSON) // Json 타입으로 요청 보내기
                    .bodyValue(request) // 요청 body 에 userInput 담기
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {
                    }) // Json 응답을 Map 형식으로 파싱하여 받기
                    .block(); // 동기 처리
            if (response != null) {
                // Map 으로 파싱해놓은 응답에서 키값으로 밸류만 반환
                return response.get("imageGeneratePrompt");
            } else {
                throw new IllegalArgumentException("이미지 프롬프트 생성을 실패했습니다.");
            }
        } catch (Exception e) {
            throw new RuntimeException("LLM 이미지 프롬프트 생성 모델 호출 중 에러 발생 : " + e.getMessage());
        }
    }

    /**
     * Stable Diffusion API 에 이미지 생성 프롬프트를 전달하여 이미지를 생성하고 저장하면, 저장한 경로를 받아오는 메소드
     */
    private byte[] getGeneratedImageByteFromStableDiffusion(String imageGeneratePrompt) {
        try {
            // 요청 보낼 리퀘스트 생성
            Map<String, String> request = new LinkedHashMap<>();
            request.put("imageGeneratePrompt", imageGeneratePrompt);

            Map<String, String> response = webClient // api 요청에 webClient 사용
                    .post()
                    .uri("http://localhost:8000/image-generation/generate-image") // 요청 보낼 url
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request) // 요청 body 에 이미지 생성 프롬프트 담기
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {
                    }) // 이미지 경로 String 값으로 받기
                    .block();

            if (response != null) {
                File file = new File(response.get("path"));
                if (!file.exists() || !file.isFile()) {
                    throw new RuntimeException("파일이 존재하지 않거나 올바르지 않은 경로입니다.");
                } else {
                    return Files.readAllBytes(file.toPath());
                }
            } else {
                throw new RuntimeException("AI 이미지 파일 경로 획득을 실패했습니다");
            }
        } catch (Exception e) {
            throw new RuntimeException("Stable Diffusion 이미지 생성 모델 호출 중 에러 발생 : " + e.getMessage());
        }
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
                }).toList(); // 변환한 객체들 리스트로 담아서 반환
    }
}