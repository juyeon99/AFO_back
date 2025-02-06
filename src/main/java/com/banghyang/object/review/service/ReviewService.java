package com.banghyang.object.review.service;

import com.banghyang.member.entity.Member;
import com.banghyang.member.service.MemberService;
import com.banghyang.object.like.service.LikeService;
import com.banghyang.object.product.entity.Product;
import com.banghyang.object.product.service.ProductService;
import com.banghyang.object.review.dto.ReviewRequest;
import com.banghyang.object.review.entity.Review;
import com.banghyang.object.review.repository.ReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MemberService memberService;
    private final ProductService productService;
    private final LikeService likeService;

    /**
     * 리뷰 생성 메소드
     */
    public void createReview(ReviewRequest reviewRequest) {
        // 리뷰 작성자 엔티티 찾아오기
        Member targetMemberEntity = memberService.getMemberById(reviewRequest.getMemberId());
        // 리뷰 작성할 제품 엔티티 찾아오기
        Product targetProductEntity = productService.getProductById(reviewRequest.getProductId());
        // 리뷰 엔티티 생성
        Review newReviewEntity = Review.builder()
                .member(targetMemberEntity)
                .product(targetProductEntity)
                .content(reviewRequest.getContent())
                .build();
        reviewRepository.save(newReviewEntity);
    }

    /**
     * 리뷰 삭제 메소드
     */
    public void deleteReview(Long reviewId) {
        // 삭제할 리뷰 엔티티 찾아오기
        Review targetReviewEntity = getReviewById(reviewId);
        // 삭제할 리뷰에 해당하는 좋아요부터 삭제하기
        likeService.deleteLikesByReview(targetReviewEntity);
        // 리뷰 삭제하기
        reviewRepository.delete(targetReviewEntity);
    }

    /**
     * 아이디로 리뷰 엔티티 반환하는 메소드
     */
    public Review getReviewById(Long reviewId) {
        return reviewRepository.findById(reviewId).orElseThrow(() -> new EntityNotFoundException(
                "[ReviewService:getReviewById]아이디에 해당하는 리뷰 엔티티를 찾을 수 없습니다."
        ));
    }
}
