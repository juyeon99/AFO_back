package com.banghyang.recommend.service;

import com.banghyang.member.entity.Member;
import com.banghyang.member.repository.MemberRepository;
import com.banghyang.recommend.dto.ChatDto;
import com.banghyang.recommend.entity.Chat;
import com.banghyang.recommend.repository.ChatRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class RecommendService {

    private final LLMService llmService;
    private final ImageGenerationService imageGenerationService;
    private final ChatRepository chatRepository;
    private final MemberRepository memberRepository;
    private final ImageProcessingService imageProcessingService;

    public RecommendService(LLMService llmService, ImageGenerationService imageGenerationService,
                            ChatRepository chatRepository, MemberRepository memberRepository, ImageProcessingService imageProcessingService) {
        this.llmService = llmService;
        this.imageGenerationService = imageGenerationService;
        this.chatRepository = chatRepository;
        this.memberRepository = memberRepository;
        this.imageProcessingService = imageProcessingService;
    }

    public Map<String, Object> processInputAndImage(String userInput, MultipartFile image, Long memberId) {
        Map<String, Object> response = new HashMap<>();
        try {
            log.info("처리 시작 - 회원 ID: {}", memberId);
            Member member = null;
            String userImageUrl = null;

            if (memberId != null) {
                member = memberRepository.findById(memberId)
                        .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다"));
                log.info("회원 조회 성공: {}", member.getId());
            }

            // 이미지가 있는 경우 처리
            if (image != null && !image.isEmpty()) {
                Map<String, Object> processedResult = imageProcessingService.processImage(image);
                userImageUrl = (String) processedResult.get("imageUrl");
                log.info("이미지 처리 완료 - URL: {}", userImageUrl);
            }

            // 사용자가 보낸 이미지나 텍스트가 있는 경우에만 사용자 채팅 저장(dto로 받아서 entity로 변환후 db에 저장)
            if (member != null && (StringUtils.hasText(userInput) || StringUtils.hasText(userImageUrl))) {
                ChatDto userChatDto = ChatDto.builder()
                        .memberId(member.getId())
                        .messageText(StringUtils.hasText(userInput) ? userInput : null)
                        .type(Chat.MessageType.USER)
                        .timestamp(LocalDateTime.now())
                        .chatImage(userImageUrl)
                        .lineId(generateLineId())
                        .build();

                chatRepository.save(userChatDto.toEntity(member));
                log.info("사용자 채팅 저장 완료 - 텍스트: {}, 이미지: {}", userInput, userImageUrl);
            }

            // 텍스트 입력이 있는 경우에만 LLM 서비스 호출
            if (StringUtils.hasText(userInput)) {
                Map<String, Object> llmResponse = llmService.processInputFromFastAPI(userInput);
                String mode = (String) llmResponse.get("mode");
                log.info("현재 모드: {}", mode);

                if ("recommendation".equals(mode)) {
                    log.info("추천 모드 진입");

                    // Recommendation 리스트 생성
                    List<Map<String, Object>> recommendationsList = (List<Map<String, Object>>) llmResponse.get("recommendations");
                    List<Chat.Recommendation> recommendations = recommendationsList.stream()
                            .map(rec -> Chat.Recommendation.builder()
                                    .id((String) rec.get("id"))
                                    .reason((String) rec.get("reason"))
                                    .situation((String) rec.get("situation"))
                                    .build())
                            .toList();

                    String imagePrompt = (String) llmResponse.get("image_prompt");
                    String aiGeneratedImageUrl = null;

                    // 이미지 생성 처리
                    if (imagePrompt != null) {
                        Map<String, Object> generatedImageResult = imageGenerationService.generateImage(
                                "Generate an image based on the following feeling: " + imagePrompt
                        );
                        aiGeneratedImageUrl = (String) generatedImageResult.get("s3_url");
                        response.put("generatedImage", generatedImageResult);
                    }

                    //dto로 받아서 entity로 변환후 db에 저장
                    if (member != null) {
                        ChatDto aiChatDto = ChatDto.builder()
                                .memberId(member.getId())
                                .messageText(null) // 추천 모드에서는 텍스트 대신 추천 데이터를 저장
                                .type(Chat.MessageType.AI)
                                .timestamp(LocalDateTime.now())
                                .chatImage(aiGeneratedImageUrl)
                                .lineId((Integer) llmResponse.get("line_id"))
                                .recommendations(recommendations)
                                .commonFeeling((String) llmResponse.get("common_feeling"))
                                .imagePrompt(imagePrompt)
                                .build();
                        chatRepository.save(aiChatDto.toEntity(member));
                        log.info("AI 응답 저장 완료 - 추천 모드");
                    }
                    // 응답 구성
                    response.put("mode", "recommendation");
                    response.put("recommendations", recommendationsList);
                    response.put("common_feeling", llmResponse.get("common_feeling"));
                    response.put("line_id", llmResponse.get("line_id"));
                    response.put("image_prompt", imagePrompt);

                } else if ("chat".equals(mode)) {
                    log.info("채팅 모드 진입");
                    String chatResponse = (String) llmResponse.get("response");

                    if (member != null) {
                        ChatDto aiChatDto = ChatDto.builder()
                                .memberId(member.getId())
                                .messageText(chatResponse) // 채팅 응답 텍스트 저장
                                .type(Chat.MessageType.AI)
                                .timestamp(LocalDateTime.now())
                                .chatImage(null)
                                .lineId(generateLineId())
                                .build();

                        chatRepository.save(aiChatDto.toEntity(member));
                        log.info("AI 응답 저장 완료 - 채팅 모드");
                    }

                    // 응답 구성
                    response.put("mode", "chat");
                    response.put("response", chatResponse);
                }
            }
        } catch (Exception e) {
            log.error("처리 중 오류 발생", e);
            response.put("error", "처리 중 오류: " + e.getMessage());
        }
        return response;
    }

    // line_id 생성 메서드
    private Integer generateLineId() {
        return (int) (System.currentTimeMillis() % Integer.MAX_VALUE); // Unix 타임스탬프 기반
    }

    public String generateBotResponse(String userInput) {
        try {
            // LLM 서비스 호출하여 봇 응답 생성
            Map<String, Object> llmResponse = llmService.processInputFromFastAPI(userInput);

            if ("chat".equals(llmResponse.get("mode"))) {
                return (String) llmResponse.get("response");
            } else if ("recommendation".equals(llmResponse.get("mode"))) {
                return "Recommendation mode detected. Response: " + llmResponse.get("recommended_perfumes");
            } else {
                return "Unknown mode: Unable to generate response.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error while generating bot response: " + e.getMessage();
        }
    }

    public List<ChatDto> getChatHistory(Long memberId) {
        // 엔티티 리스트로 받아서
        List<Chat> chats = chatRepository.findByMemberId(memberId);
        log.info("조회된 채팅 수: {}", chats.size());

        // DTO로 변환
        List<ChatDto> chatDtos = chats.stream()
                .map(ChatDto::fromEntity)
                .toList();

        chatDtos.forEach(chat -> {
            log.info("Chat ID: {}, MemberId: {}, MessageText: {}, Image URL: {}, Type: {}",
                    chat.getId(), chat.getMemberId(), chat.getMessageText(), chat.getChatImage(), chat.getType());
        });

        Queue<ChatDto> userChats = new LinkedList<>();
        Queue<ChatDto> aiChats = new LinkedList<>();

        for (ChatDto chatDto : chatDtos) {
            if (chatDto.getType() == Chat.MessageType.USER) {
                userChats.add(chatDto);
            } else {
                aiChats.add(chatDto);
            }
        }

        List<ChatDto> result = new ArrayList<>();
        while (!userChats.isEmpty() || !aiChats.isEmpty()) {
            if (!userChats.isEmpty()) {
                result.add(userChats.poll());
            }
            if (!aiChats.isEmpty()) {
                result.add(aiChats.poll());
            }
        }

        return result;
    }
}