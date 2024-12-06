package com.banghyang.history.service;

import com.banghyang.chat.entity.Chat;
import com.banghyang.chat.repository.ChatRepository;
import com.banghyang.history.dto.HistoryResponse;
import com.banghyang.history.entity.History;
import com.banghyang.history.entity.Recommendations;
import com.banghyang.history.repository.HistoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * 채팅 히스토리를 관리하는 서비스 클래스
 * 채팅 내역을 히스토리로 저장하고 조회하는 기능을 제공합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class HistoryService {

    private final HistoryRepository historyRepository;
    private final ChatRepository chatRepository;

    /**
     * 채팅 ID를 기반으로 새로운 히스토리를 생성합니다.
     *
     * @param chatId 히스토리로 저장할 채팅의 ID
     * @return 생성된 히스토리의 응답 DTO
     */
    @Transactional
    public HistoryResponse createHistory(String chatId) {
        Chat chat = findChatById(chatId);
        History history = createHistoryFromChat(chat);
        History savedHistory = historyRepository.save(history);
        return convertToHistoryResponse(savedHistory);
    }

    /**
     * 채팅 ID로 채팅을 조회합니다.
     *
     * @param chatId 조회할 채팅의 ID
     * @return 조회된 채팅 엔티티
     * @throws NoSuchElementException 채팅을 찾을 수 없는 경우 발생
     */
    private Chat findChatById(String chatId) {
        return chatRepository.findById(chatId)
                .orElseThrow(() -> new NoSuchElementException("채팅을 찾을 수 없습니다."));
    }

    /**1
     * 채팅 엔티티로부터 히스토리 엔티티를 생성합니다.
     *
     * @param chat 원본 채팅 엔티티
     * @return 생성된 히스토리 엔티티
     */
    private History createHistoryFromChat(Chat chat) {
        String content = chat.getContent();
        System.out.println("==============content length: " + (content != null ? content.length() : "null"));
        System.out.println("==============content: " + content);

        return History.builder()
                .chatId(chat.getId())
                .memberId(chat.getMemberId())
                .type(chat.getType())
                .imageUrl(chat.getImageUrl())
                .content(content)
                .mode(chat.getMode())
                .lineId(chat.getLineId())
                .recommendations(convertToHistoryRecommendations(chat.getRecommendations()))
                .build();
    }

    /**
     * 채팅의 추천 정보를 히스토리의 추천 정보로 변환합니다.
     *
     * @param chatRecommendations 채팅 엔티티의 추천 정보 리스트
     * @return 히스토리 엔티티의 추천 정보 리스트
     */
    private List<Recommendations> convertToHistoryRecommendations(List<Chat.Recommendation> chatRecommendations) {
        return chatRecommendations.stream()
                .map(chatRec -> Recommendations.builder()
                        .perfumeName(chatRec.getPerfumeName())
                        .perfumeBrand(chatRec.getPerfumeBrand())
                        .perfumeGrade(chatRec.getPerfumeGrade())
                        .perfumeImageUrl(chatRec.getPerfumeImageUrl())
                        .reason(chatRec.getReason())
                        .situation(chatRec.getSituation())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 히스토리 엔티티를 응답 DTO로 변환합니다.
     *
     * @param history 변환할 히스토리 엔티티
     * @return 변환된 히스토리 응답 DTO
     */
    private HistoryResponse convertToHistoryResponse(History history) {
        return HistoryResponse.builder()
                .chatId(history.getChatId())
                .memberId(history.getMemberId())
                .type(history.getType())
                .imageUrl(history.getImageUrl())
                .content(history.getContent())
                .mode(history.getMode())
                .lineId(history.getLineId())
                .timeStamp(history.getTimeStamp())
                .recommendations(convertToRecommendationDtos(history))
                .build();
    }

    /**
     * 히스토리의 추천 정보를 DTO로 변환합니다.
     *
     * @param history 추천 정보를 포함한 히스토리 엔티티
     * @return 변환된 추천 정보 DTO 리스트
     */
    private List<HistoryResponse.RecommendationDto> convertToRecommendationDtos(History history) {
        return history.getRecommendations().stream()
                .map(rec -> HistoryResponse.RecommendationDto.builder()
                        .perfumeName(rec.getPerfumeName())
                        .perfumeBrand(rec.getPerfumeBrand())
                        .perfumeGrade(rec.getPerfumeGrade())
                        .perfumeImageUrl(rec.getPerfumeImageUrl())
                        .reason(rec.getReason())
                        .situation(rec.getSituation())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 회원의 카드 히스토리를 조회하는 메서드
     *
     * @param memberId 조회할 회원의 ID
     * @return 시간 역순으로 정렬된 채팅 히스토리 목록
     */
    public List<History> getCardHistory(Long memberId) {
        List<History> chats = historyRepository.findByMemberIdOrderByTimeStampDesc(memberId);
        System.out.println("조회된 히스토리 수:"+ chats.size());
        return chats;
    }
}