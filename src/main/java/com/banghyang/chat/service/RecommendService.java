package com.banghyang.chat.service;

import com.banghyang.chat.repository.ReasonRepository;
import com.banghyang.common.type.ChatType;
import com.banghyang.member.entity.Member;
import com.banghyang.member.repository.MemberRepository;
import com.banghyang.chat.entity.Chat;
import com.banghyang.chat.repository.ChatRepository;
import com.banghyang.object.perfume.repository.PerfumeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendService {

    private final LLMService llmService;
    private final ImageGenerationService imageGenerationService;
    private final ChatRepository chatRepository;
    private final MemberRepository memberRepository;
    private final ImageProcessingService imageProcessingService;
    private final PerfumeRepository perfumeRepository;
    private final ReasonRepository reasonRepository;

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

            if (image != null && !image.isEmpty()) {
                Map<String, Object> processedResult = imageProcessingService.processImage(image);
                userImageUrl = (String) processedResult.get("imageUrl");
                log.info("이미지 처리 완료 - URL: {}", userImageUrl);
            }

            if (member != null && (StringUtils.hasText(userInput) || StringUtils.hasText(userImageUrl))) {
                // USER 타입의 메시지는 line_id를 생성하지 않음
                Chat userChatEntity = Chat.builder()
                        .memberId(member.getId())
                        .type(ChatType.USER)
                        .imageUrl(userImageUrl)
                        .content(StringUtils.hasText(userInput) ? userInput : null)
                        .build();

                chatRepository.save(userChatEntity);
                log.info("사용자 채팅 저장 완료 - 텍스트: {}, 이미지: {}", userInput, userImageUrl);
            }

            if (StringUtils.hasText(userInput)) {
                Map<String, Object> llmResponse = llmService.processInputFromFastAPI(userInput);
//                String mode = (String) llmResponse.get("mode");
//                log.info("현재 모드: {}", mode);
//
//                if ("recommendation".equals(mode)) {
//                    log.info("추천 모드 진입");
//
//                    // LLM Response 안의 recommendations(향수아이디, reason, situation) 리스트로 받기
//                    List<Map<String, Object>> recommendationsList =
//                            (List<Map<String, Object>>) llmResponse.get("recommendations");
//
//                    List<Perfume> perfumeEntityList = recommendationsList.stream()
//                            .map(recommendation -> {
//
//                                Perfume recommendedPerfumeEntity =
//                                        perfumeRepository.findById((Long) recommendation.get("id"))
//                                        .orElseThrow(() -> new IllegalArgumentException(
//                                                "추천받은 향수 아이디에 해당하는 향수 정보를 찾을 수 없습니다."));
//                                return recommendedPerfumeEntity;
//                            }).toList();
//
//                    List<Cha> recommendations = recommendationsList.stream()
//                            .map(rec -> Chat.Recommendation.builder()
//                                    .id((String) rec.get("id"))
//                                    .reason((String) rec.get("reason"))
//                                    .situation((String) rec.get("situation"))
//                                    .build())
//                            .toList();
//
//                    String imagePrompt = (String) llmResponse.get("image_prompt");
//                    String aiGeneratedImageUrl = null;
//
//                    if (imagePrompt != null) {
//                        Map<String, Object> generatedImageResult = imageGenerationService.generateImage(
//                                "Generate an image based on the following feeling: " + imagePrompt
//                        );
//                        aiGeneratedImageUrl = (String) generatedImageResult.get("s3_url");
//                        response.put("generatedImage", generatedImageResult);
//                    }
//
//                    if (member != null) {
//                        // AI 타입의 응답에 대해서만 line_id를 응답에서 가져옴
//                        Integer lineId = (Integer) llmResponse.get("line_id");  // line_id가 포함된 응답을 사용
//                        ChatDto aiChatDto = ChatDto.builder()
//                                .memberId(member.getId())
//                                .content(null)
//                                .type(Chat.MessageType.AI)
//                                .timestamp(LocalDateTime.now())
//                                .chatImage(aiGeneratedImageUrl)
//                                .lineId(lineId)
//                                .recommendations(recommendations)
//                                .commonFeeling((String) llmResponse.get("common_feeling"))
//                                .imagePrompt(imagePrompt)
//                                .build();
//                        chatRepository.save(aiChatDto.toEntity(member));
//                        log.info("AI 응답 저장 완료 - 추천 모드");
//                    }
//                    response.put("mode", "recommendation");
//                    response.put("recommendations", recommendationsList);
//                    response.put("common_feeling", llmResponse.get("common_feeling"));
//                    response.put("line_id", llmResponse.get("line_id"));
//                    response.put("image_prompt", imagePrompt);
//
//                } else if ("chat".equals(mode)) {
//                    log.info("채팅 모드 진입");
//                    String chatResponse = (String) llmResponse.get("response");
//
//                    if (member != null) {
//                        Integer lineId = generateLineId();  // 기본적으로 generateLineId()를 사용
//                        ChatDto aiChatDto = ChatDto.builder()
//                                .memberId(member.getId())
//                                .content(chatResponse)
//                                .type(Chat.MessageType.AI)
//                                .timestamp(LocalDateTime.now())
//                                .chatImage(null)
//                                .lineId(lineId)
//                                .build();
//
//                        chatRepository.save(aiChatDto.toEntity(member));
//                        log.info("AI 응답 저장 완료 - 채팅 모드");
//                    }
//
//                    response.put("mode", "chat");
//                    response.put("response", chatResponse);
//                }
            }
        } catch (Exception e) {
            log.error("처리 중 오류 발생", e);
            response.put("error", "처리 중 오류: " + e.getMessage());
        }
        return response;
    }

    private Integer generateLineId() {
        return (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
    }

    public String generateBotResponse(String userInput) {
        try {
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
        List<Chat> chats = chatRepository.findByMemberId(memberId);
        log.info("조회된 채팅 수: {}", chats.size());

        List<ChatDto> chatDtos = chats.stream()
                .map(ChatDto::fromEntity)
                .toList();

        chatDtos.forEach(chat -> {
            log.info("Chat ID: {}, MemberId: {}, MessageText: {}, Image URL: {}, Type: {}",
                    chat.getId(), chat.getMemberId(), chat.getContent(), chat.getChatImage(), chat.getType());
        });

        Queue<ChatDto> userChats = new LinkedList<>();
        Queue<ChatDto> aiChats = new LinkedList<>();

        for (ChatDto chatDto : chatDtos) {
            if (chatDto.getType() == ChatType.USER) {
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
