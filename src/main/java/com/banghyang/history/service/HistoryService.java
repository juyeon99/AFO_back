package com.banghyang.history.service;

import com.banghyang.chat.entity.Chat;
import com.banghyang.chat.repository.ChatRepository;
import com.banghyang.history.dto.HistoryResponse;
import com.banghyang.history.entity.History;
import com.banghyang.history.entity.Recommendation;
import com.banghyang.history.repository.HistoryRepository;
import com.banghyang.history.repository.RecommendationRepository;
import com.banghyang.member.entity.Member;
import com.banghyang.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * 채팅 히스토리를 관리하는 서비스 클래스
 * 채팅 내역을 히스토리로 저장하고 조회하는 기능을 제공합니다.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class HistoryService {

    private final HistoryRepository historyRepository;
    private final ChatRepository chatRepository;
    private final MemberRepository memberRepository;
    private final RecommendationRepository recommendationRepository;

    /**
     * 히스토리 생성
     */
    public void createHistoryByChat(String chatImageUrl) {
        System.out.println("[히스토리 생성 메소드 진입]");
        System.out.println("[전달 받은 채팅 아이디] : " + chatImageUrl);
        // 채팅 아이디로 채팅 정보 가져오기
        Chat chatEntity = chatRepository.findByImageUrl(chatImageUrl);

        if (chatEntity != null) {

            // 채팅 정보의 회원 아이디로 회원 정보 가져오기
            Member memberEntity = memberRepository.findById(chatEntity.getMemberId())
                    .orElseThrow(() -> new NoSuchElementException(
                            "[History Service]히스토리 생성 중 회원 정보 조회에 실패했습니다."
                    ));

            // 채팅 정보로 히스토리 생성하기
            History newHistoryEntity = History.builder()
                    .chatId(chatEntity.getId())
                    .lineId(chatEntity.getLineId())
                    .member(memberEntity)
                    .build();
            historyRepository.save(newHistoryEntity);

            // 채팅의 추천 속 향수 정보로 추천 엔티티 생성하기
            List<Recommendation> recommnedationEntityList = chatEntity.getRecommendations().stream()
                    .map(chatRecommendation -> {
                        Recommendation newRecommendationEntity = Recommendation.builder()
                                .history(newHistoryEntity)
                                .perfumeName(chatRecommendation.getPerfumeName())
                                .perfumeBrand(chatRecommendation.getPerfumeBrand())
                                .perfumeGrade(chatRecommendation.getPerfumeGrade())
                                .perfumeImageUrl(chatRecommendation.getPerfumeImageUrl())
                                .reason(chatRecommendation.getReason())
                                .situation(chatRecommendation.getSituation())
                                .build();
                        recommendationRepository.save(newRecommendationEntity);
                        return newRecommendationEntity;
                    }).toList();
        } else {
            throw new NoSuchElementException("[History Service]히스토리 생성중 이미지 URL로 채팅 정보 조회에 실패했습니다.");
        }
    }

    /**
     * 해당 멤버의 모든 히스토리 조회
     */
    public List<HistoryResponse> getMembersHistory(Long memberId) {
        // 회원 아이디로 회원 정보 가져오기
        Member memberEntity = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException(
                        "[History Service]히스토리 조회 중 회원정보 조회에 실패했습니다."
                ));

        return memberEntity.getHistory().stream()
                .map(historyEntity -> {
                    HistoryResponse historyResponse = new HistoryResponse();
                    historyResponse.setMemberId(memberId);
                    historyResponse.setId(historyEntity.getId());
                    historyResponse.setChatId(historyEntity.getChatId());
                    historyResponse.setLineId(historyEntity.getLineId());
                    historyResponse.setTimeStamp(historyEntity.getTimeStamp());
                    historyResponse.setRecommendations(historyEntity.getRecommendation().stream()
                            .map(recommendationEntity -> {
                                HistoryResponse.RecommendationDto recommendationDto =
                                        new HistoryResponse.RecommendationDto();
                                recommendationDto.setPerfumeName(recommendationEntity.getPerfumeName());
                                recommendationDto.setPerfumeBrand(recommendationEntity.getPerfumeBrand());
                                recommendationDto.setPerfumeGrade(recommendationEntity.getPerfumeGrade());
                                recommendationDto.setPerfumeImageUrl(recommendationEntity.getPerfumeImageUrl());
                                recommendationDto.setReason(recommendationEntity.getReason());
                                recommendationDto.setSituation(recommendationEntity.getSituation());
                                return recommendationDto;
                            })
                            .toList());
                    return historyResponse;
                }).toList();
    }

    /**
     * 히스토리 삭제
     */
    public void deleteHistory(Long historyId) {
        // 삭제할 히스토리 정보 찾아오기
        History historyEntity = historyRepository.findById(historyId)
                .orElseThrow(() -> new NoSuchElementException(
                        "[History Service]히스토리 삭제 중 히스토리 정보 조회에 실패했습니다."
                ));
        historyRepository.delete(historyEntity);
    }
}