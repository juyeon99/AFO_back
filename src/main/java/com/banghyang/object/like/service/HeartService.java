package com.banghyang.object.like.service;

import com.banghyang.member.entity.Member;
import com.banghyang.member.repository.MemberRepository;
import com.banghyang.object.like.dto.HeartRequest;
import com.banghyang.object.like.entity.Heart;
import com.banghyang.object.like.repository.HeartRepository;
import com.banghyang.object.review.entity.Review;
import com.banghyang.object.review.repository.ReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class HeartService {

    private final HeartRepository heartRepository;
    private final MemberRepository memberRepository;
    private final ReviewRepository reviewRepository;

    /**
     * 새로운 좋아요 생성 메소드
     */
    public void createLike(HeartRequest heartRequest) {
        // 좋아요 누른 사용자
        Member targetMemberEntity = memberRepository.findById(heartRequest.getMemberId()).orElseThrow(() ->
                new EntityNotFoundException("[좋아요-서비스-생성]아이디에 해당하는 멤버 엔티티를 찾을 수 없습니다."));
        // 좋아요 누른 리뷰
        Review targetReviewEntity = reviewRepository.findById(heartRequest.getReviewId()).orElseThrow(() ->
                new EntityNotFoundException("[좋아요-서비스-생성]아이디에 해당하는 리뷰 엔티티를 찾을 수 없습니다."));
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
