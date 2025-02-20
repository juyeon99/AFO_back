package com.banghyang.object.review.service;

import com.banghyang.member.entity.Member;
import com.banghyang.member.repository.MemberRepository;
import com.banghyang.object.heart.entity.Heart;
import com.banghyang.object.heart.repository.HeartRepository;
import com.banghyang.object.product.entity.Product;
import com.banghyang.object.product.repository.ProductRepository;
import com.banghyang.object.review.dto.MyReviewResponse;
import com.banghyang.object.review.dto.ReviewModifyRequest;
import com.banghyang.object.review.dto.ReviewRequest;
import com.banghyang.object.review.dto.ReviewResponse;
import com.banghyang.object.review.entity.Review;
import com.banghyang.object.review.repository.ReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@EnableCaching
@Transactional
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final HeartRepository heartRepository;

    /**
     * 리뷰 생성 메소드
     */
    public void createReview(ReviewRequest reviewRequest) {
        // 리뷰 작성자 엔티티 찾아오기
        Member targetMemberEntity = memberRepository.findById(reviewRequest.getMemberId()).orElseThrow(() ->
                new EntityNotFoundException("[리뷰-서비스-생성]아이디에 해당하는 멤버 엔티티를 찾을 수 없습니다."));
        // 리뷰 작성할 제품 엔티티 찾아오기
        Product targetProductEntity = productRepository.findById(reviewRequest.getProductId()).orElseThrow(() ->
                new EntityNotFoundException("[리뷰-서비스-생성]아이디에 해당하는 제품 엔티티를 찾을 수 없습니다."));
        // 리뷰 엔티티 생성
        Review newReviewEntity = Review.builder()
                .member(targetMemberEntity)
                .product(targetProductEntity)
                .content(reviewRequest.getContent())
                .build();
        reviewRepository.save(newReviewEntity);
    }

    /**
     * 리뷰 수정 메소드
     */
    public void modifyReview(ReviewModifyRequest reviewModifyRequest) {
        // 수정한 리뷰 찾아오기
        Review targetReviewEntity = reviewRepository.findById(reviewModifyRequest.getReviewId()).orElseThrow(() ->
                new EntityNotFoundException("[리뷰-서비스-수정]아이디에 해당하는 리뷰 엔티티를 찾을 수 없습니다."));
        // request 의 content 값으로 수정할 리뷰 내용 바꾸기
        targetReviewEntity.modify(reviewModifyRequest.getContent());
    }

    /**
     * 리뷰 삭제 메소드
     */
    public void deleteReview(Long reviewId) {
        // 삭제할 리뷰 엔티티 찾아오기
        Review targetReviewEntity = reviewRepository.findById(reviewId).orElseThrow(() ->
                new EntityNotFoundException("[리뷰-서비스-삭제]아이디에 해당하는 리뷰 엔티티를 찾을 수 없습니다."));
        // 삭제할 리뷰에 해당하는 좋아요부터 삭제하기
        heartRepository.deleteByReview(targetReviewEntity);
        // 리뷰 삭제하기
        reviewRepository.delete(targetReviewEntity);
    }

    /**
     * 특정 향수의 리뷰 목록 조회
     */
    @Cacheable(value = "productReviews", key = "'product_' + #productId")
    public List<ReviewResponse> getReviewsByProductId(Long memberId, Long productId) {
        // 제품에 해당하는 리뷰 찾기
        List<Review> reviews = reviewRepository.findByProductId(productId);

        // 비회원인지 확인
        boolean isGuest = (memberId == null || memberId <= 0);

        // 현재 사용자 정보 가져오기
        Member targetMemberEntity = isGuest ? null :
                memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException(
                        "[ReviewService-getReviewsByProductId]아이디에 해당하는 멤버 엔티티를 찾을 수 없습니다."));

        // 현재 사용자의 공감 리스트 가져오기
        List<Heart> memberHeartList = isGuest ? null : heartRepository.findByMember(targetMemberEntity);

        // DTO 로 변환하여 리스트에 담아 반환
        return reviews.stream().map(review -> {
            ReviewResponse reviewResponse = new ReviewResponse();
            reviewResponse.setId(review.getId());
            reviewResponse.setMemberName(review.getMember().getName());
            reviewResponse.setContent(review.getContent());
            // 리뷰에 해당하는 Heart 리스트에 담아 개수 반환
            reviewResponse.setHeartCount(heartRepository.findByReview(review).size());
            // 현재 조회할 리뷰가 사용자의 공감리뷰 리스트에 있는지 없는지 비교하여 유무값 담기
            reviewResponse.setMyHeart(!isGuest && memberHeartList.stream()
                    .anyMatch(memberHeart -> memberHeart.getReview().getId().equals(review.getId())));
            reviewResponse.setCreatedAt(review.getTimeStamp());
            return reviewResponse;
        }).toList();
    }

    /**
     * 특정 회원이 작성한 리뷰 목록 조회
     */
    public List<MyReviewResponse> getReviewsByMemberId(Long memberId) {
        // 회원 존재 여부 확인
        memberRepository.findById(memberId).orElseThrow(() ->
                new EntityNotFoundException("[리뷰-서비스-조회]아이디에 해당하는 멤버 엔티티를 찾을 수 없습니다."));

        // 특정 회원이 작성한 리뷰 목록 조회
        List<Review> reviews = reviewRepository.findByMemberId(memberId);

        log.info("Found {} reviews for member", reviews.size());

        // DTO 변환
        return reviews.stream().map(review -> new MyReviewResponse(
                review.getId(),
                review.getMember().getName(),
                review.getProduct().getId(),
                review.getContent(),
                review.getTimeStamp()
        )).collect(Collectors.toList());
    }

}
