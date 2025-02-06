package com.banghyang.history.service;

import com.banghyang.chat.entity.Chat;
import com.banghyang.chat.service.ChatService;
import com.banghyang.history.dto.HistoryResponse;
import com.banghyang.history.entity.History;
import com.banghyang.history.entity.Recommendation;
import com.banghyang.history.repository.HistoryRepository;
import com.banghyang.history.repository.RecommendationRepository;
import com.banghyang.member.entity.Member;
import com.banghyang.member.service.MemberService;
import com.banghyang.object.line.entity.Line;
import com.banghyang.object.line.service.LineService;
import com.banghyang.object.product.entity.Product;
import com.banghyang.object.product.entity.ProductImage;
import com.banghyang.object.product.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
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

    private final LineService lineService;
    private final ChatService chatService;
    private final MemberService memberService;
    private final ProductService productService;

    private final HistoryRepository historyRepository;
    private final RecommendationRepository recommendationRepository;

    /**
     * 히스토리 생성
     */
    public void createHistoryByChat(String chatId) {
        // 채팅 아이디로 채팅 정보 가져오기
        Chat targetChatEntity = chatService.getChatById(chatId);

        if (targetChatEntity != null) {
            // 채팅 정보의 회원 아이디로 회원 정보 가져오기
            Member targetMemberEntity = memberService.getMemberById(targetChatEntity.getMemberId());
            // 채팅 정보의 라인 아이디로 라인 정보 가져오기
            Line targetLineEntity = lineService.getLineById(targetChatEntity.getLineId());
            // 채팅 정보로 히스토리 생성하기
            History newHistoryEntity = History.builder()
                    .chatId(targetChatEntity.getId())
                    .line(targetLineEntity)
                    .member(targetMemberEntity)
                    .build();
            historyRepository.save(newHistoryEntity);

            // 채팅의 추천 속 향수 정보로 추천 엔티티 생성하기
            targetChatEntity.getRecommendations().forEach(chatRecommendation -> {
                Product targetProductEntity = productService.getProductByNameKr(chatRecommendation.getProductNameKr());
                Recommendation newRecommendationEntity = Recommendation.builder()
                        .history(newHistoryEntity)
                        .product(targetProductEntity)
                        .reason(chatRecommendation.getReason())
                        .situation(chatRecommendation.getSituation())
                        .build();
                recommendationRepository.save(newRecommendationEntity);
            });
        } else {
            throw new EntityNotFoundException("[History Service] 히스토리 생성중 채팅 정보 조회에 실패했습니다.");
        }
    }

    /**
     * 해당 멤버의 모든 히스토리 조회
     */
    public List<HistoryResponse> getMembersHistory(Long memberId) {
        // 조회 신청한 사용자 정보
        Member targetMemberEntity = memberService.getMemberById(memberId);
        return historyRepository.findByMember(targetMemberEntity).stream() // 사용자에 해당하는 모든 히스토리 조회
                .map(historyEntity -> {
                    HistoryResponse historyResponse = new HistoryResponse();
                    historyResponse.setMemberId(memberId); // 회원 아이디
                    historyResponse.setId(historyEntity.getId()); // 히스토리 아이디
                    historyResponse.setChatId(historyEntity.getChatId()); // 채팅 아이디
                    historyResponse.setLineId(historyEntity.getLine().getId()); // 계열 아이디
                    historyResponse.setTimeStamp(historyEntity.getTimeStamp()); // 히스토리 생성일시
                    // 추천 정보 처리
                    historyResponse.setRecommendations(
                            // 히스토리에 해당하는 추천정보 가져오기
                            recommendationRepository.findByHistory(historyEntity).stream()
                                    .map(recommendationEntity -> {
                                        // 추천정보 담을 response 생성
                                        HistoryResponse.RecommendationDto recommendationDto =
                                                new HistoryResponse.RecommendationDto();
                                        // 추천 제품 한글명 담기
                                        recommendationDto.setProductNameKr(recommendationEntity.getProduct().getNameKr());
                                        // 추천 제품 브랜드 담기
                                        recommendationDto.setProductBrand(recommendationEntity.getProduct().getBrand());
                                        // 추천 제품 부향률 담기
                                        recommendationDto.setProductGrade(recommendationEntity.getProduct().getGrade());
                                        // 추천 제품 이미지 URL 담기
                                        recommendationDto.setProductImageUrls(productService
                                                .getProductImagesByProduct(recommendationEntity.getProduct())
                                                .stream()
                                                .map(ProductImage::getUrl)
                                                .toList()
                                        );
                                        // 추천 이유 담기
                                        recommendationDto.setReason(recommendationEntity.getReason());
                                        // 추천 상황 담기
                                        recommendationDto.setSituation(recommendationEntity.getSituation());
                                        return recommendationDto;
                                    }).toList());
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
                        "[HistoryService]삭제하려는 히스토리 정보를 찾을 수 없습니다.")
                );
        // 삭제할 추천 정보 찾아오기
        List<Recommendation> recommendationsToDelete = recommendationRepository.findByHistory(historyEntity);

        recommendationRepository.deleteAll(recommendationsToDelete);
        historyRepository.delete(historyEntity);
    }
}