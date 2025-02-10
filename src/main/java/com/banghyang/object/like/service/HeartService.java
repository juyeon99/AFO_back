package com.banghyang.object.like.service;

import com.banghyang.member.entity.Member;
import com.banghyang.member.service.MemberService;
import com.banghyang.object.like.dto.HeartRequest;
import com.banghyang.object.like.entity.Heart;
import com.banghyang.object.like.repository.HeartRepository;
import com.banghyang.object.review.entity.Review;
import com.banghyang.object.review.service.ReviewService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class HeartService {

    private final HeartRepository heartRepository;
    private final MemberService memberService;
    private final ReviewService reviewService;

    /**
     * 새로운 좋아요 생성 메소드
     */
    public void createLike(HeartRequest heartRequest) {
        // 좋아요 누른 사용자
        Member targetMemberEntity = memberService.getMemberById(heartRequest.getMemberId());
        // 좋아요 누른 리뷰
        Review targetReviewEntity = reviewService.getReviewById(heartRequest.getReviewId());
        // 좋아요 엔티티 생성
        Heart heart = Heart.builder()
                .member(targetMemberEntity)
                .review(targetReviewEntity)
                .build();
        heartRepository.save(heart);
    }

    /**
     * 좋아요 삭제 메소드
     */
    public void deleteLike(Long likeId) {
        heartRepository.deleteById(likeId);
    }

    /**
     * 리뷰에 해당하는 좋아요 삭제 메소드
     */
    public void deleteLikesByReview(Review targetReviewEntity) {
        // 리뷰에 해당하는 좋아요 엔티티 리스트
        List<Heart> likesToDelete = heartRepository.findByReview(targetReviewEntity);
        // 만약 존재한다면 삭제 진행, 아니면 별도의 처리없음.
        if (!likesToDelete.isEmpty()) {
            heartRepository.deleteAll(likesToDelete);
        }
    }
}
