package com.banghyang.recommend.service;

import com.banghyang.member.entity.Member;
import com.banghyang.member.repository.MemberRepository;
import com.banghyang.recommend.entity.Chat;
import com.banghyang.recommend.entity.ChatHistory;
import com.banghyang.recommend.exception.CustomException;
import com.banghyang.recommend.repository.ChatHistoryRepository;
import com.banghyang.recommend.repository.ChatRepository;
import com.banghyang.recommend.type.ChatType;
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
    private final ChatHistoryRepository chatHistoryRepository;

    public RecommendService(LLMService llmService, ImageGenerationService imageGenerationService,
                            ChatRepository chatRepository, MemberRepository memberRepository, ImageProcessingService imageProcessingService, ChatHistoryRepository chatHistoryRepository) {
        this.llmService = llmService;
        this.imageGenerationService = imageGenerationService;
        this.chatRepository = chatRepository;
        this.memberRepository = memberRepository;
        this.imageProcessingService = imageProcessingService;
        this.chatHistoryRepository = chatHistoryRepository;
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
                Chat userChat = Chat.builder()
                        .memberId(member.getId())
                        .content(StringUtils.hasText(userInput) ? userInput : "")
                        .type(ChatType.USER)
                        .imageUrl(userImageUrl)
                        .build();

                chatRepository.save(userChat);
                log.info("사용자 채팅 저장 완료 - 텍스트: {}, 이미지: {}", userInput, userImageUrl);
            }

            // 텍스트 입력이 있는 경우에만 LLM 서비스 호출
            if (StringUtils.hasText(userInput)) {
                Map<String, Object> llmResponse = llmService.processInputFromFastAPI(userInput);
                String mode = (String) llmResponse.get("mode");
                log.info("현재 모드: {}", mode);

                if ("recommendation".equals(mode)) {
                    log.info("추천 모드 진입");
                    response.put("mode", "recommendation");
                    response.put("recommendedPerfumes", llmResponse.get("recommended_perfumes"));
                    response.put("commonFeeling", llmResponse.get("common_feeling"));

                    String imagePrompt = (String) llmResponse.get("image_prompt");
                    String prompt = "Generate an image based on the following feeling: " + imagePrompt;
                    Map<String, Object> generatedImageResult = imageGenerationService.generateImage(prompt);
                    response.put("generatedImage", generatedImageResult);
                    String aiGeneratedImageUrl = (String) generatedImageResult.get("s3_url");

                    //dto로 받아서 entity로 변환후 db에 저장
                    if (member != null) {
                        Chat aiChat = Chat.builder()
                                .memberId(member.getId())
                                .content(llmResponse.toString())
                                .type(ChatType.AI)
                                .imageUrl(aiGeneratedImageUrl)
                                .build();

                        chatRepository.save(aiChat);
                        log.info("AI 응답 저장 완료 - 추천 모드");
                    }
                } else if ("chat".equals(mode)) {
                    log.info("채팅 모드 진입");
                    String chatResponse = (String) llmResponse.get("response");
                    response.put("mode", "chat");
                    response.put("response", chatResponse);

                    if (member != null) {
                        Chat aiChat = Chat.builder()
                                .memberId(member.getId())
                                .content(chatResponse)
                                .type(ChatType.AI)
                                .imageUrl(null)
                                .build();

                        chatRepository.save(aiChat);
                        log.info("AI 응답 저장 완료 - 채팅 모드");
                    }
                }
            }
        } catch (Exception e) {
            log.error("처리 중 오류 발생", e);
            log.error("예외 종류: {}", e.getClass().getName());
            log.error("예외 메시지: {}", e.getMessage());
            response.put("error", "처리 중 오류: " + e.getMessage());
        }
        return response;
    }

    public List<Chat> getChatList(Long memberId) {
        // 엔티티 리스트로 받기
        List<Chat> chats = chatRepository.findByMemberId(memberId);
        log.info("조회된 채팅 수: {}", chats.size());

        // 디버깅을 위한 로깅
        chats.forEach(chat -> {
            log.info("Chat ID: {}, MemberId: {}, Content: {}, Image URL: {}, Type: {}",
                    chat.getId(), chat.getMemberId(), chat.getContent(), chat.getImageUrl(), chat.getType());
        });

        Queue<Chat> userChats = new LinkedList<>();
        Queue<Chat> aiChats = new LinkedList<>();

        // 유저와 AI 메시지 분리
        for (Chat chat : chats) {
            if (chat.getType() == ChatType.USER) {
                userChats.add(chat);
            } else {
                aiChats.add(chat);
            }
        }

        // 결과 리스트 생성
        List<Chat> result = new ArrayList<>();
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

    public ChatHistory createChatHistory(String chatId) {

        // 원본 채팅 조회
        Chat originalChat = chatRepository.findById(chatId)
                .orElseThrow(() -> new CustomException("채팅을 찾을 수 없습니다."));

        // 히스토리 생성
        ChatHistory history = ChatHistory.builder()
                .chatId(originalChat.getId())
                .memberId(originalChat.getMemberId())
                .type(originalChat.getType())
                .imageUrl(originalChat.getImageUrl())
                .timeStamp(originalChat.getTimeStamp())
                .content(originalChat.getContent())
                .mode(originalChat.getMode())
                .lineId(originalChat.getLineId())
                .recommendations(originalChat.getRecommendations())
                .build();

        System.out.println("================= history : "+ history);

        return chatHistoryRepository.save(history);

    }

    public List<ChatHistory> getChatHistory(Long memberId) {
        List<ChatHistory> chats = chatHistoryRepository.findByMemberIdOrderByTimeStampDesc(memberId);
        log.info("조회된 히스토리 수: {}", chats.size());
        return chats;
    }
}